package com.yzrilyzr.ui;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.yzrilyzr.FAQ.R;
import com.yzrilyzr.myclass.util;
import java.lang.reflect.Method;

public class myToolBar extends myLinearLayout
{
	private myTitleButton b1,b2,b3,b4,b0;
	private myTextViewTitle title;
	private String m0,m1,m2,m3,m4;
	private Context ctx;
	private int stH=0;
	public myToolBar(Context c,AttributeSet a)
	{
		super(c,a);
		ctx= c;
		try
		{minit(a);}
		catch(Exception e)
		{util.check(c,e);}
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT)
			stH=util.getStatusBarHeight(getContext());
		int margin=0;
		if(uidata.UI_USESHADOW)margin=util.dip2px(3);
		setPadding(0,stH,0,margin);

	}
	public void setContentSize()
	{
		FrameLayout f=(FrameLayout)getParent();
		View cc=f.getChildAt(0);
		FrameLayout.LayoutParams lp=(FrameLayout.LayoutParams)cc.getLayoutParams();
		int mm=util.dip2px(55)+stH;
		if(lp.topMargin!=mm)lp.topMargin=mm;
		lp.height=ViewGroup.LayoutParams.MATCH_PARENT;
		cc.setLayoutParams(lp);
	}
	public myToolBar(Context c)
	{
		this(c,null);
	}
	private drawable draw;
	private void minit(AttributeSet a)
	{
		setBackground(draw=new drawable());
		draw.setLayer(this);
		b1=new myTitleButton(ctx);
		b2=new myTitleButton(ctx);
		b3=new myTitleButton(ctx);
		b4=new myTitleButton(ctx);
		b0=new myTitleButton(ctx);
		title=new myTextViewTitle(ctx);
		title.setLines(1);
		setOrientation(0);
		setGravity(Gravity.CENTER);
		if(a!=null)
		{
			TypedArray t=ctx.obtainStyledAttributes(a,R.styleable.yzr);
			b0.setText(t.getString(R.styleable.yzr_tip0));
			Drawable d=null;
			try
			{d=t.getDrawable(R.styleable.yzr_src0);}
			catch(Exception e)
			{}
			String o=t.getString(R.styleable.yzr_src0);
			if(d!=null||o!=null)
			{
				m0=t.getString(R.styleable.yzr_onClick0);
				if(o.indexOf("preset")!=-1)b0.setImageBitmap(uidata.getIconByName(o.replace("preset/","")));
				else b0.setImageDrawable(d);
				addView(b0);
				if(m0!=null)b0.setOnClickListener(new OnClickListener(){@Override public void onClick(View v)
							{call(m0);}});
			}
			title.setText(t.getString(R.styleable.yzr_title));
			LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-2,-2);
			lp.setMargins(util.dip2px(10),0,0,0);
			lp.weight=1.0f;
			title.setLayoutParams(lp);
			addView(title);
			title.setGravity(t.getInt(R.styleable.yzr_titleGravity,Gravity.LEFT));
			b1.setText(t.getString(R.styleable.yzr_tip1));
			try
			{d=t.getDrawable(R.styleable.yzr_src1);}
			catch(Exception e)
			{}
			o=t.getString(R.styleable.yzr_src1);
			if(d!=null||o!=null)
			{m1=t.getString(R.styleable.yzr_onClick1);
				if(o.indexOf("preset")!=-1)b1.setImageBitmap(uidata.getIconByName(o.replace("preset/","")));
				else b1.setImageDrawable(d);
				addView(b1);
				if(m1!=null)b1.setOnClickListener(new OnClickListener(){@Override public void onClick(View v)
							{call(m1);}});}
			b2.setText(t.getString(R.styleable.yzr_tip2));
			try
			{d=t.getDrawable(R.styleable.yzr_src2);}
			catch(Exception e)
			{}
			o=t.getString(R.styleable.yzr_src2);
			if(d!=null||o!=null)
			{m2=t.getString(R.styleable.yzr_onClick2);
				if(o.indexOf("preset")!=-1)b2.setImageBitmap(uidata.getIconByName(o.replace("preset/","")));
				else b2.setImageDrawable(d);
				addView(b2);
				if(m2!=null)b2.setOnClickListener(new OnClickListener(){@Override public void onClick(View v)
							{call(m2);}});}
			b3.setText(t.getString(R.styleable.yzr_tip3));
			try
			{d=t.getDrawable(R.styleable.yzr_src3);}
			catch(Exception e)
			{}
			o=t.getString(R.styleable.yzr_src3);
			if(d!=null||o!=null)
			{m3=t.getString(R.styleable.yzr_onClick3);
				if(o.indexOf("preset")!=-1)b3.setImageBitmap(uidata.getIconByName(o.replace("preset/","")));
				else b3.setImageDrawable(d);
				addView(b3);
				if(m3!=null)b3.setOnClickListener(new OnClickListener(){@Override public void onClick(View v)
							{call(m3);}});}
			b4.setText(t.getString(R.styleable.yzr_tip4));
			try
			{d=t.getDrawable(R.styleable.yzr_src4);}
			catch(Exception e)
			{}
			o=t.getString(R.styleable.yzr_src4);
			if(d!=null||o!=null)
			{m4=t.getString(R.styleable.yzr_onClick4);
				if(o.indexOf("preset")!=-1)b4.setImageBitmap(uidata.getIconByName(o.replace("preset/","")));
				else b4.setImageDrawable(d);

				addView(b4);
				if(m4!=null)b4.setOnClickListener(new OnClickListener(){@Override public void onClick(View v)
							{call(m4);}});}

			t.recycle();
		}
	}
	private void call(String mname)
	{
		try
		{
			Method c=getContext().getClass().getMethod(mname,View.class);
			c.invoke(getContext(),this);
		}
		catch(Throwable e)
		{util.check(getContext(),e);}
	}
	public myTitleButton getButton(int i)
	{
		switch(i)
		{
			case 0:return b0;
			case 1:return b1;
			case 2:return b2;
			case 3:return b3;
			case 4:return b4;
			default:return null;
		}
	}
	public void setTitle(String s)
	{
		title.setText(s);
	}
	private class drawable extends Drawable
	{
		private int margin,w,h;
		private Paint paint;

		@Override
		public void setBounds(int left, int top, int right, int bottom)
		{
			// TODO: Implement this method
			super.setBounds(left, top, right, bottom);
			w=right;h=bottom;
		}

		public drawable()
		{
			paint=new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(uidata.UI_COLOR_MAIN);
		}
		@Override
		public void draw(Canvas p1)
		{
			// TODO: Implement this method
			p1.drawRect(0,0,w,h-margin,paint);
		}
		public void setLayer(View v)
		{
			if(!uidata.UI_USESHADOW)return;
			margin=util.dip2px(3);
			paint.setShadowLayer(margin,0,margin/3,0x88666666);
			v.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
		}
		@Override
		public void setAlpha(int p1)
		{
			// TODO: Implement this method
		}

		@Override
		public void setColorFilter(ColorFilter p1)
		{
			// TODO: Implement this method
		}

		@Override
		public int getOpacity()
		{
			// TODO: Implement this method
			return 0;
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		// TODO: Implement this method
		super.onSizeChanged(w, h, oldw, oldh);
		setContentSize();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		// TODO: Implement this method
		super.onMeasure(widthMeasureSpec,heightMeasureSpec);
		if(!uidata.UI_USESHADOW)setMeasuredDimension(WidgetUtils.measureWidth(widthMeasureSpec,getWidth()),WidgetUtils.measureHeight(heightMeasureSpec,util.dip2px(55)+stH));
		else setMeasuredDimension(WidgetUtils.measureWidth(widthMeasureSpec,getWidth()),WidgetUtils.measureHeight(heightMeasureSpec,util.dip2px(58)+stH));
	}
}
