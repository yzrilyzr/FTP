package com.yzrilyzr.ui;
import android.graphics.*;
import com.yzrilyzr.myclass.*;
import android.content.*;
import android.graphics.drawable.*;

public class myIconDrawer
{
	private Paint paint;
	private Path path;
	private Bitmap bitmap;
	private Canvas canvas;
	public static final enum DrawType{
		CIRCLE,RECT,ROUNDRECT,ARC,LINE,POINT,PATH;
	};
	public float ww,cw,hw,qw;
	public myIconDrawer(int w){
		bitmap=Bitmap.createBitmap(w,w,Bitmap.Config.ARGB_8888);
		canvas=new Canvas(bitmap);
		paint=new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(0xffffffff);
		paint.setStyle(Paint.Style.STROKE);
		path=new Path();
		paint.setStrokeWidth(w/20f);
		cw=w;hw=w/2f;ww=w/20f;qw=w/4f;
	}
	public void setLine(){
		Paint p=new Paint();
		p.setColor(0xff000000);
		p.setStyle(Paint.Style.STROKE);
		p.setTextSize(10);
		for(float i=0;i<cw;i+=ww){
			canvas.drawLine(0,i,cw,i,p);
			canvas.drawLine(i,0,i,cw,p);
		}
		for(float i=0;i<cw;i+=ww){
			for(float j=ww;j<cw+ww;j+=ww){
					canvas.drawText((int)(i/ww+1)+","+(int)(j/ww),i,j,p);
				}
			
		}
	}
	public void clear(){
			bitmap=Bitmap.createBitmap((int)cw,(int)cw,Bitmap.Config.ARGB_8888);
			canvas=new Canvas(bitmap);
			paint=new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(0xffffffff);
			paint.setStyle(Paint.Style.STROKE);
			path=new Path();
			paint.setStrokeWidth(cw/20f);
			
		}
	public void move(float x,float y){path.moveTo(x,y);}
	public void line(float x,float y){path.lineTo(x,y);}
	public void clearPath(){path=new Path();}
	public void setFill(boolean b){paint.setStyle(b? Paint.Style.FILL:Paint.Style.STROKE);}
	public Canvas getCanvas(){return canvas;}
	public Drawable getDrawable(){return new BitmapDrawable(bitmap);}
	public Bitmap getBitmap(){return bitmap;}
	public Paint getPaint(){return paint;}
	public void draw(DrawType type,boolean fill,float... p){
		setFill(fill);
		switch(type){
			case CIRCLE:canvas.drawCircle(p[0],p[1],p[2],paint);break;
			case RECT:canvas.drawRect(p[0],p[1],p[2],p[3],paint);break;
			case ROUNDRECT:canvas.drawRoundRect(new RectF(p[0],p[1],p[2],p[3]),p[4],p[5],paint);break;
			case LINE:canvas.drawLine(p[0],p[1],p[2],p[3],paint);break;
			case ARC:canvas.drawArc(new RectF(p[0],p[1],p[2],p[3]),p[4],p[5],false,paint);break;
			case POINT:canvas.drawPoint(p[0],p[1],paint);break;
			case PATH:canvas.drawPath(path,paint);break;
		}
	}
}
