package com.yzrilyzr.ui;
import android.widget.*;
import android.content.*;
import android.util.*;
import android.view.*;
import android.graphics.*;
import com.yzrilyzr.myclass.util;

public class myImageButton extends ImageButton
{

    public myImageButton(Context c,AttributeSet a)
    {
        super(c,a);
        init(c);
        WidgetUtils.setIcon(this,a);
    }
    private myRippleDrawable mrd;
    private void init(Context c)
    {
        setScaleType(ImageView.ScaleType.FIT_XY);
        //int p=util.dip2px(getContext(),3);
        //setPadding(p,p,p,p);
        mrd=new myRippleDrawable();
        setBackground(mrd);
        mrd.setLayer(this);
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
