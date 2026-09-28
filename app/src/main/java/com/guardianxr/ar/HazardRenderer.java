package com.guardianxr.ar;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.SystemClock;
import com.google.ar.core.Pose;
import java.nio.FloatBuffer;

/** Soft animated AR hazard particles rendered with GLES2 point sprites. */
final class HazardRenderer {
  private int program;
  private final float[] anchorM=new float[16], mv=new float[16], mvp=new float[16];
  private final float[] onePoint={0f,0f,0f};
  private FloatBuffer point;
  private long startMs;

  void create(){
    point=BackgroundRenderer.buf(onePoint);
    startMs=SystemClock.uptimeMillis();
    String vs=
      "uniform mat4 m;uniform float size;attribute vec3 p;"+
      "void main(){vec4 q=m*vec4(p,1.0);gl_Position=q;gl_PointSize=size;}";
    String fs=
      "precision mediump float;uniform vec4 c;uniform float kind;"+
      "void main(){vec2 d=gl_PointCoord-vec2(.5);float r=length(d)*2.0;"+
      "if(r>1.0)discard;float soft=1.0-smoothstep(.45,1.0,r);"+
      "float a=c.a*soft;"+
      "if(kind<.5){"+
      // Flame sprites are tapered vertically: broad hot base, narrow flickering tip.
      "float yy=gl_PointCoord.y;float taper=mix(1.0,.22,yy);float fx=abs(gl_PointCoord.x-.5)*2.0/taper;"+
      "float fy=abs(yy-.42)*1.45;float flame=sqrt(fx*fx+fy*fy);if(flame>1.0)discard;"+
      "float edge=1.0-smoothstep(.48,1.0,flame);float core=1.0-smoothstep(.0,.58,flame);"+
      "vec3 hot=mix(c.rgb,vec3(1.0,.94,.20),core*.88);gl_FragColor=vec4(hot,c.a*edge);}"+
      "else if(kind<1.5){float mist=.72+.28*(1.0-r);gl_FragColor=vec4(c.rgb*mist,a);}"+
      "else{float smoke=1.0-smoothstep(.25,1.0,r);gl_FragColor=vec4(c.rgb,c.a*smoke);}"+
      "}";
    program=BackgroundRenderer.link(vs,fs);
  }

  void draw(Pose pose,float[] view,float[] proj,boolean fire){
    pose.toMatrix(anchorM,0);
    Matrix.multiplyMM(mv,0,view,0,anchorM,0);
    Matrix.multiplyMM(mvp,0,proj,0,mv,0);
    float t=(SystemClock.uptimeMillis()-startMs)/1000f;

    GLES20.glEnable(GLES20.GL_BLEND);
    GLES20.glDepthMask(false);
    if(fire){
      // Additive blending gives the flame a luminous centre instead of flat polygons.
      GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE);
      drawFire(t);
    }else{
      GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);
      drawGas(t);
    }
    GLES20.glDepthMask(true);
    GLES20.glDisable(GLES20.GL_BLEND);
  }

  private void drawFire(float t){
    // Layered teardrop sprites form one coherent flame instead of glowing bubbles.
    for(int i=0;i<24;i++){
      float phase=fract(i*.6180339f + t*(.42f + (i%4)*.025f));
      float y=.02f + phase*.34f;
      float width=.075f*(1f-phase*.76f);
      float sway=(float)Math.sin(t*5.0f+i*1.91f)*.014f*(.35f+phase);
      float x=signed(i*19+5)*width+sway;
      float z=signed(i*31+9)*width*.42f;
      float size=58f + (1f-phase)*34f;
      float g=.22f+.48f*(1f-phase);
      float a=.24f+.24f*(1f-phase);
      drawParticle(x,y,z,size,1f,g,.015f,a,0f);
    }
    // Dense yellow core close to the floor.
    for(int i=0;i<8;i++){
      float phase=fract(i*.277f+t*.58f);
      drawParticle(signed(i*13)*.035f,.018f+phase*.13f,signed(i*7)*.018f,
          54f,1f,.72f,.04f,.34f,0f);
    }
    // A small amount of dark smoke rises above the flame.
    for(int i=0;i<7;i++){
      float phase=fract(i*.414f+t*.12f);
      float y=.30f+phase*.30f;
      float x=signed(i*23)*(.025f+.035f*phase)+(float)Math.sin(t*.8f+i)*.018f;
      drawParticle(x,y,signed(i*11)*.025f,52f+i*3f,.16f,.17f,.18f,.055f,2f);
    }
  }

  private void drawGas(float t){
    // Larger, denser cloud with slow lateral drift and soft edges.
    for(int i=0;i<42;i++){
      float phase=fract(i*.381966f+t*(.035f+(i%5)*.004f));
      float angle=i*2.39996f+t*.11f;
      float radius=.07f+.29f*((i%9)/8f);
      float drift=(float)Math.sin(t*.34f+i*.7f)*.045f;
      float x=(float)Math.cos(angle)*radius+drift;
      float z=(float)Math.sin(angle)*radius*.68f;
      float y=.06f+phase*.38f+(float)Math.sin(i*1.37f+t*.45f)*.022f;
      float size=86f+(i%7)*10f;
      float fade=1f-Math.abs(phase-.5f)*1.35f;
      float a=.105f+.075f*Math.max(0f,fade);
      drawParticle(x,y,z,size,.12f,.78f,.55f,a,1f);
    }
    // Pale centre gives visibility against both dark and light backgrounds.
    for(int i=0;i<10;i++){
      float phase=fract(i*.213f+t*.025f);
      drawParticle(signed(i*17)*.16f,.12f+phase*.24f,signed(i*29)*.11f,
          96f,.38f,.88f,.70f,.075f,1f);
    }
  }

  private void drawParticle(float x,float y,float z,float size,float r,float g,float b,float a,float kind){
    float[] local=new float[16], model=new float[16], vm=new float[16], clip=new float[16];
    Matrix.setIdentityM(local,0); Matrix.translateM(local,0,x,y,z);
    Matrix.multiplyMM(model,0,anchorM,0,local,0);
    // Rebuild view/projection using cached anchor-relative MVP decomposition is avoided here;
    // model is converted to anchor-local translation and composed against the cached anchor MVP.
    Matrix.setIdentityM(local,0); Matrix.translateM(local,0,x,y,z);
    Matrix.multiplyMM(clip,0,mvp,0,local,0);
    GLES20.glUseProgram(program);
    int p=GLES20.glGetAttribLocation(program,"p");
    GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program,"m"),1,false,clip,0);
    GLES20.glUniform1f(GLES20.glGetUniformLocation(program,"size"),size);
    GLES20.glUniform4f(GLES20.glGetUniformLocation(program,"c"),r,g,b,a);
    GLES20.glUniform1f(GLES20.glGetUniformLocation(program,"kind"),kind);
    GLES20.glEnableVertexAttribArray(p); point.position(0);
    GLES20.glVertexAttribPointer(p,3,GLES20.GL_FLOAT,false,0,point);
    GLES20.glDrawArrays(GLES20.GL_POINTS,0,1);
    GLES20.glDisableVertexAttribArray(p);
  }

  private float fract(float v){return v-(float)Math.floor(v);}
  private float signed(int seed){int n=(seed*1103515245+12345);return (((n>>>16)&0x7fff)/16383.5f)-1f;}
}
