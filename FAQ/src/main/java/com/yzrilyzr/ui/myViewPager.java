package com.yzrilyzr.ui;
import android.widget.*;
import android.content.*;
import android.util.*;
import android.view.*;
import android.app.*;
import android.graphics.*;

public class myViewPager extends HorizontalScrollView
{
		private Context mContext;
		private int screenWidth=0;
		private int curScreen=0;
		private LinearLayout ll;
		public myViewPager(Context context) {
				this(context,null);
			}
		public myViewPager(Context context, AttributeSet attrs) {
				super(context, attrs);
				mContext = context;
				init();
			}
			private void init(){
				ll=new LinearLayout(mContext);
				addView(ll);
				setHorizontalScrollBarEnabled(false);
				
			}
		public void setPages(View... views){
			ll.removeAllViews();
			for(View v:views){
				ll.addView(v);
				v.setLayoutParams(new LinearLayout.LayoutParams(screenWidth,-2));
			}
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh)
		{
			// TODO: Implement this method
			super.onSizeChanged(w, h, oldw, oldh);
			for(int i = 0; i < ll.getChildCount(); i++) {
				View view = ll.getChildAt(i);
				view.setLayoutParams(new LinearLayout.LayoutParams(screenWidth,-2));
			}
		}
		
		public void setCurrentItem(int i,boolean b){
			curScreen=i;
			if(b)smoothScrollTo(screenWidth*i,0);
			else scrollTo(screenWidth*i,0);
		}
		public int getCurrentItem(){
			return curScreen;
		}
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
				screenWidth = MeasureSpec.getSize(widthMeasureSpec);
			}

		@Override
		public boolean onTouchEvent(MotionEvent ev)
			{
				// TODO: Implement this method
				getParent().requestDisallowInterceptTouchEvent(true);
				if(ev.getAction()==MotionEvent.ACTION_UP){
					int l=(getScrollX()+screenWidth/2)/screenWidth;
					smoothScrollTo((curScreen=l)*screenWidth,0);
					return true;
				}
				return super.onTouchEvent(ev);
			}
		
		/*@Override
		protected void onLayout(boolean changed, int l, int t, int r, int b) {
			super.onLayout(changed,l,t,r,b);
			for(int i = 0; i < ll.getChildCount(); i++) {
				View view = ll.getChildAt(i);
				view.setLayoutParams(new LinearLayout.LayoutParams(screenWidth,-2));
			}
		}*/
}
