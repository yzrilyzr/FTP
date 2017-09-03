package com.yzrilyzr.ui;
import android.view.*;

public class myTouchListener implements View.OnTouchListener
{
    public void onDown(View v, MotionEvent m)
    {}
    public void onUp(View v, MotionEvent m)
    {}
    public boolean onView(View v, MotionEvent m)
    {return false;}
    public void onClick(View v)
    {}
    public boolean onLongClick(View v, MotionEvent m)
    {return false;}
    private boolean onView=false,LC=false,CC=false,VC=false,PE=true;
    private long millis=0;
    public void setParentEvent(boolean b)
    {PE = b;}
    @Override
    public boolean onTouch(View v, MotionEvent e)
    {
        ViewParent vp=v.getParent();
        if (!PE)vp.requestDisallowInterceptTouchEvent(true);
        int x=(int)e.getX(),y=(int)e.getY(),a=e.getAction(),w=v.getWidth(),h=v.getHeight();
        if (a == MotionEvent.ACTION_DOWN)
        {
            onDown(v, e);
            millis = System.currentTimeMillis();
            onView = true;
            LC = false;
            CC = false;
            VC = false;
        }
        if (onView)
        {
            if (a == MotionEvent.ACTION_UP)
            {
                onUp(v, e);
                if (!CC && !VC)onClick(v);
            }
            else
            {
                if (x > 0 && x < w && y > 0 && y < h)
                {
                    VC = onView(v, e);
                    onView = true;
                    if (System.currentTimeMillis() - millis > 500 && !VC && !LC)
                    {
                        CC = onLongClick(v, e);
                        LC = true;
                    }
                }
                else
                {
                    onView = false;
                    onUp(v, e);
                }
            }
        }
        return VC || CC;
    }

}
