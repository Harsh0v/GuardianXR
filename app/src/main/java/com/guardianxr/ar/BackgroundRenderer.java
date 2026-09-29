package com.guardianxr.ar;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import com.google.ar.core.Coordinates2d;
import com.google.ar.core.Frame;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

final class BackgroundRenderer {
  private int tex, program;
  private FloatBuffer vb, tb;
  private static final float[] V = {-1,-1, 1,-1, -1,1, 1,1};
  private static final float[] T = {0,0, 0,0, 0,0, 0,0};

  int textureId(){ return tex; }

  void create(){
    int[] a = new int[1];
    GLES20.glGenTextures(1,a,0); tex=a[0];
    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,tex);
    GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
    GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
    GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);
    GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);
    vb=buf(V); tb=buf(T);
    String vs="attribute vec2 p;attribute vec2 t;varying vec2 v;void main(){gl_Position=vec4(p,0.,1.);v=t;}";
    String fs="#extension GL_OES_EGL_image_external : require\nprecision mediump float;uniform samplerExternalOES s;varying vec2 v;void main(){gl_FragColor=texture2D(s,v);}";
    program=link(vs,fs);
  }

  void draw(Frame frame){
    // ARCore computes the correct UVs for portrait/landscape and device rotation.
    vb.position(0); tb.position(0);
    frame.transformCoordinates2d(
        Coordinates2d.OPENGL_NORMALIZED_DEVICE_COORDINATES, vb,
        Coordinates2d.TEXTURE_NORMALIZED, tb);
    vb.position(0); tb.position(0);

    GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    GLES20.glDepthMask(false);
    GLES20.glUseProgram(program);
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,tex);
    GLES20.glUniform1i(GLES20.glGetUniformLocation(program,"s"),0);
    int p=GLES20.glGetAttribLocation(program,"p"), t=GLES20.glGetAttribLocation(program,"t");
    GLES20.glEnableVertexAttribArray(p); GLES20.glEnableVertexAttribArray(t);
    GLES20.glVertexAttribPointer(p,2,GLES20.GL_FLOAT,false,0,vb);
    GLES20.glVertexAttribPointer(t,2,GLES20.GL_FLOAT,false,0,tb);
    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
    GLES20.glDisableVertexAttribArray(p); GLES20.glDisableVertexAttribArray(t);
    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,0);
    GLES20.glDepthMask(true);
    GLES20.glEnable(GLES20.GL_DEPTH_TEST);
  }

  static FloatBuffer buf(float[] x){
    FloatBuffer b=ByteBuffer.allocateDirect(x.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
    b.put(x).position(0); return b;
  }
  static int shader(int type,String s){
    int h=GLES20.glCreateShader(type); GLES20.glShaderSource(h,s); GLES20.glCompileShader(h); return h;
  }
  static int link(String v,String f){
    int p=GLES20.glCreateProgram();
    GLES20.glAttachShader(p,shader(GLES20.GL_VERTEX_SHADER,v));
    GLES20.glAttachShader(p,shader(GLES20.GL_FRAGMENT_SHADER,f));
    GLES20.glLinkProgram(p); return p;
  }
}
