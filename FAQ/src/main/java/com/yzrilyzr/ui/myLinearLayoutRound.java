package com.yzrilyzr.ui;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.yzrilyzr.FAQ.R;

		public class myLinearLayoutRound extends myLinearLayout
			{
				public myLinearLayoutRound(Context c,AttributeSet a){
						super(c,a);
						TypedArray t=c.obtainStyledAttributes(a,R.styleable.yzr);
						radius=t.getFloat(R.styleable.yzr_corner,uidata.UI_RADIUS);
						useRound=t.getBoolean(R.styleable.yzr_round,false);
						color2Back=t.getBoolean(R.styleable.yzr_color2Back,false);
						t.recycle();
						init();
					}
				private boolean useRound=false,color2Back=false;
				private float radius;
				private myRippleDrawable mrd;
				@Override
				protected void onSizeChanged(int w, int h, int oldw, int oldh)
					{
						// TODO: Implement this method
						super.onSizeChanged(w, h, oldw, oldh);
						if(useRound)radius=h/2f;
						init();
					}
				public myLinearLayoutRound(Context c){
						super(c);
						init();
					}
				public void init(){
						mrd=new myRippleDrawable(!color2Back?uidata.UI_COLOR_MAIN:uidata.UI_COLOR_BACK,uidata.UI_COLOR_MAINHL,radius);
                        setBackgroundDrawable(mrd);
						}
			}
