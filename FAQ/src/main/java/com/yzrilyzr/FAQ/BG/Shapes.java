package com.yzrilyzr.FAQ.BG;
import android.graphics.Canvas;
import com.yzrilyzr.ui.uidata;
import android.view.SurfaceHolder;
import java.util.ArrayList;
import android.graphics.PointF;
import java.util.Random;
import com.yzrilyzr.myclass.util;
import android.graphics.Paint;

public class Shapes extends BGdraw
{
	ArrayList<PF> pos=new ArrayList<PF>();
	int ww,wh;
	float rad;
	float vel;
	public Shapes()
	{
		super();
		p.setStrokeWidth(util.dip2px(3));
		ww=util.getScreenWidth();
		wh=util.getScreenHeight();
		rad=util.dip2px(15);
		p.setTextSize(200);
		vel=rad/16;
		Random r=new Random();
		for(int i=0;i<50;i++)pos.add(new PF(r.nextInt(ww),r.nextInt(wh)));
	}

	@Override
	public void onDraw(Canvas c)
	{
		// TODO: Implement this method
		super.onDraw(c);
		c.drawColor(uidata.UI_COLOR_BACK);
		for(PF pp:pos)
		{
			p.setColor(uidata.UI_COLOR_MAIN);
			p.setStyle(Paint.Style.FILL);
			c.drawCircle(pp.x,pp.y,rad,p);
			p.setColor(0xff000000);
			p.setStyle(Paint.Style.STROKE);
			c.drawCircle(pp.x,pp.y,rad,p);
		}
		//c.drawText("FPS:"+(1000/dt),0,500,p);
	}

	@Override
	public void onDestory()
	{
		// TODO: Implement this method
		super.onDestory();
		pos.clear();
	}

	@Override
	public void onCompute()
	{
		// TODO: Implement this method
		super.onCompute();
		Random r=new Random();
		for(PF pp:pos)
		{
			pp.vx+=pp.ax;
			pp.vy+=pp.ay;
			pp.x+=pp.vx;
			pp.y+=pp.vy;
			if(pp.x<-rad)pp.x=ww+rad;
			if(pp.y<-rad)pp.y=wh+rad;
			if(pp.x>ww+rad)pp.x=-rad;
			if(pp.y>wh+rad)pp.y=-rad;
			if(pp.vx>vel)pp.vx=vel;
			if(pp.vx<-vel)pp.vx=-vel;
			if(pp.vy>vel)pp.vy=vel;
			if(pp.vy<-vel)pp.vy=-vel;
			pp.ax=r.nextBoolean()?0.2f:-0.2f;
			pp.ay=r.nextBoolean()?0.2f:-0.2f;
		}
	}
	class PF
	{
		public float x,y,ax=0,ay=0,vx,vy;
		public PF(float x,float y)
		{
			this.x=x;
			this.y=y;
		}
	}
}
