package com.yzrilyzr.ui;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.nineoldandroids.view.ViewHelper;
import com.yzrilyzr.myclass.util;
import java.util.ArrayList;

public class mySlidingMenu extends LinearLayout
{
	private int mMenuWidth;
	private boolean isOpen;
	private View mMenu,mContent;
	private float dX=0,cxx=0;
	private boolean bool=false;
	public mySlidingMenu(Context context, AttributeSet attrs)
	{
		super(context,attrs);
		mMenuWidth=util.dip2px(260);
		cxx=mMenuWidth;objs.add(new Cir());
	}
	public mySlidingMenu(Context context)
	{
		this(context,null);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mMenu = (ViewGroup)getChildAt(0);
		mContent = (ViewGroup)getChildAt(1);
		mMenu.getLayoutParams().width=mMenuWidth;
		mContent.getLayoutParams().width=util.getScreenWidth();
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed,l,t,r,b);
		if (changed)this.scrollTo(mMenuWidth, 0);
	}
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		int act=ev.getAction();
		if(act==MotionEvent.ACTION_UP||act==MotionEvent.ACTION_CANCEL)
		{
			bool=false;
			if(getScrollX()>mMenuWidth/2)closeMenu();
			else openMenu();
		}
		if(act==MotionEvent.ACTION_DOWN)
		{
			dX=ev.getX();
			bool=false;
		}
		else if(act==MotionEvent.ACTION_MOVE)
		{
			cxx+=dX-ev.getX();
			if(cxx<0)cxx=0;
			else if(cxx>mMenuWidth)cxx=mMenuWidth;
			this.scrollTo((int)cxx,0);
			dX=ev.getX();
			bool=true;
		}
		return true;
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent e)
	{
		if(e.getAction()==MotionEvent.ACTION_MOVE)return bool;
		return super.onInterceptTouchEvent(e);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		// TODO: Implement this method
		super.onDraw(canvas);
		if(!bool)
		{
			int sx=getScrollX();
			if(sx!=cxx)scrollBy((int)((cxx-sx)*0.5),0);
			if(sx<0)scrollTo((int)(cxx=0),0);
			else if(sx>mMenuWidth)scrollTo((int)(cxx=mMenuWidth),0);
		}
		for(Obj obj:objs)obj.draw(canvas);
		invalidate();
	}

	public void openMenu()
	{
		if (isOpen)return;
		cxx=0;
		invalidate();
		isOpen = true;
	}
	public void closeMenu()
	{
		if(!isOpen)return;
		cxx=mMenuWidth;
		invalidate();
		isOpen = false;
	}
	public boolean getIsOpen()
	{return isOpen;}
	public void toggle()
	{
		if (isOpen)closeMenu();
		else openMenu();
	}
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt)
	{
		super.onScrollChanged(l, t, oldl, oldt);
		float scale = l * 1.0f / mMenuWidth;
		float leftScale = 1 - 0.3f * scale;
		float rightScale = 0.8f + scale * 0.2f;
		ViewHelper.setScaleX(mMenu, leftScale);
		ViewHelper.setScaleY(mMenu, leftScale);
		ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
		ViewHelper.setTranslationX(mMenu, mMenuWidth * scale * 0.6f);
		ViewHelper.setPivotX(mContent, 0);
		ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
		ViewHelper.setScaleX(mContent, rightScale);
		ViewHelper.setScaleY(mContent, rightScale);
		ViewHelper.setAlpha(mContent,1);
	}

	ArrayList<Obj> objs=new ArrayList<Obj>();
	private abstract class Obj
	{
		public Paint p;
		public Obj()
		{
			p=new Paint();
			p.setAntiAlias(true);
		}
		public abstract void draw(Canvas c);
	}
	class Cir extends Obj
	{
		private float x=0,y=0,T=0;
		public Cir()
		{
			super();
			p.setColor(0xffff0000);
			p.setStyle(Paint.Style.FILL_AND_STROKE);
			p.setStrokeWidth(10);
			x=120;
			y=1600;
		}
		@Override
		public void draw(Canvas c)
		{
			// TODO: Implement this method
			T+=Math.PI/6f;
			c.drawCircle(x+50*(float)Math.cos(T),y+50*(float)Math.sin(T),20,p);
		}
	}
}
