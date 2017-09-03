package com.yzrilyzr.ui;
import android.view.*;
import android.widget.*;

import android.content.Context;
import android.util.AttributeSet;
import com.yzrilyzr.FAQ.R;
import com.yzrilyzr.myclass.util;

public class myTitleButton extends ImageButton
{
	private Context ctx;
	private String text=null;
	private myRippleDrawable mrd;
	public myTitleButton(Context c,AttributeSet a){
		super(c,a);
		ctx=c;
		text=c.obtainStyledAttributes(a,R.styleable.yzr).getString(R.styleable.yzr_text);
		int p=uidata.UI_PADDING_DEFAULT;
		setPadding(p,p,p,p);
			mrd=new myRippleDrawable(uidata.UI_COLOR_MAIN,uidata.UI_COLOR_MAINHL,0);
			setBackground(mrd);
			WidgetUtils.setIcon(this,a);
		setScaleType(ImageView.ScaleType.FIT_XY);
		setOnTouchListener(new myTouchListener(){
						@Override public void onDown(View v,MotionEvent m){mrd.shortRipple(m.getX(),m.getY());}
						@Override public boolean onLongClick(View v,MotionEvent m){
								if(text!=null){
										Toast t=Toast.makeText(ctx,text,0);
										t.setDuration(1000);
										t.setGravity(Gravity.LEFT|Gravity.TOP,(int)(m.getRawX()-m.getX()),(int)(m.getRawY()-m.getY()+getHeight()/2));
										t.show();
									}
								return true;
						}
					});
		}
	public myTitleButton(Context c){
		this(c,null);
	}
public void setText(String s){
	text=s;
}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
		{
			// TODO: Implement this method
			int i=util.dip2px(50);
			setMeasuredDimension(WidgetUtils.measureWidth(widthMeasureSpec,i),WidgetUtils.measureHeight(heightMeasureSpec,i));
					}

	
}
