package com.yzrilyzr.ui;
import android.view.*;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;
import com.yzrilyzr.FAQ.R;

public class myButton extends Button// implements Runnable
{
    public myButton(Context c,AttributeSet a)
    {
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
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        // TODO: Implement this method
        super.onSizeChanged(w, h, oldw, oldh);
        if(useRound)radius=h/2f;
        init();
    }

	@Override
	public void setEnabled(boolean enabled)
	{
		// TODO: Implement this method
		super.setEnabled(enabled);
		mrd=new myRippleDrawable(enabled?(!color2Back?uidata.UI_COLOR_MAIN:uidata.UI_COLOR_BACK):0xffaaaaaa,uidata.UI_COLOR_MAINHL,radius);
        mrd.setLayer(this);
        setBackgroundDrawable(mrd);
	}


    public myButton(Context c)
    {
        super(c);
        init();
    }

    private myRippleDrawable mrd;
    private void init()
    {
        setTextColor(color2Back?uidata.UI_TEXTCOLOR_BACK:uidata.UI_TEXTCOLOR_MAIN);
        if(uidata.UI_USETYPEFACE)setTypeface(uidata.UI_TYPEFACE);
        setTextSize(uidata.UI_TEXTSIZE_DEFAULT);
        mrd=new myRippleDrawable(isEnabled()?(!color2Back?uidata.UI_COLOR_MAIN:uidata.UI_COLOR_BACK):0xffaaaaaa,uidata.UI_COLOR_MAINHL,radius);
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
