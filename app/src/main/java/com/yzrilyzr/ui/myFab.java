package com.yzrilyzr.ui;
import android.view.*;
import android.content.*;
import android.util.*;
import android.widget.*;
import android.graphics.*;
import android.graphics.drawable.shapes.*;
import android.graphics.drawable.*;
import com.yzrilyzr.myclass.*;

public class myFab extends ImageView
	{
public myFab(Context c,AttributeSet a){
	super(c,a);
	WidgetUtils.setIcon(this,a);
}
public myFab(Context c){
	this(c,null);
	}


private myRippleDrawable mrd;
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
			{
				// TODO: Implement this method
				//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
				int i=util.dip2px(60);
				setMeasuredDimension(WidgetUtils.measureWidth(widthMeasureSpec,i),WidgetUtils.measureHeight(heightMeasureSpec,i));
				int a=i/4;
				setPadding(a,a,a,a);
				mrd=new myRippleDrawable(uidata.UI_COLOR_MAIN,uidata.UI_COLOR_MAINHL,i/2);
				mrd.setLayer(this);
                setBackgroundDrawable(mrd);
				setOnTouchListener(new myTouchListener(){
                        @Override public boolean onLongClick(View v,MotionEvent m)
                        {
                            mrd.longRipple(m.getX(),m.getY());
                            return false;
                        };
                        @Override public void onDown(View v,MotionEvent m)
                        {
                            mrd.shortRipple(m.getX(),m.getY());
                        }});
				
			}
			
}
