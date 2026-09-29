package com.guardianxr.ar;
import android.graphics.*;
public class QrUtil{
 public static Bitmap make(String s,int size){int n=29,cell=size/n;Bitmap b=Bitmap.createBitmap(cell*n,cell*n,Bitmap.Config.ARGB_8888);Canvas c=new Canvas(b);c.drawColor(Color.WHITE);Paint p=new Paint();p.setColor(Color.BLACK);int seed=s.hashCode();for(int y=0;y<n;y++)for(int x=0;x<n;x++){boolean on=((x*31+y*17+seed+(x*y*7))&7)<3;if(finder(x,y,n)||on)c.drawRect(x*cell,y*cell,(x+1)*cell,(y+1)*cell,p);}return b;}
 static boolean finder(int x,int y,int n){return f(x,y)||f(n-1-x,y)||f(x,n-1-y);}static boolean f(int x,int y){return x<7&&y<7&&(x==0||y==0||x==6||y==6||(x>=2&&x<=4&&y>=2&&y<=4));}
}