package com.yzrilyzr.FAQ.BG;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.MotionEvent;
public class BGdraw extends Thread
{
	public Paint p;
	private SurfaceHolder hold;
	public BGdraw(){
		p=new Paint(Paint.ANTI_ALIAS_FLAG);
	}
	public void onDraw(Canvas c){}
	public void onCreate(SurfaceHolder h){
		hold=h;
		start();
	}
	public void onCompute(){}
	public void onDestory(){
		interrupt();
	}
	public void onChange(SurfaceHolder s,int format,int w,int h){
		hold=s;
	}
	public void onTouchEvent(MotionEvent e){};
	public void onVisibilityChanged(boolean b){}
	public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset){}
	@Override
	public void run()
	{
		// TODO: Implement this method
		super.run();
		while(!isInterrupted()){
			Canvas c=hold.lockCanvas();
			onCompute();
			onDraw(c);
			hold.unlockCanvasAndPost(c);
		}
	}
	
}
