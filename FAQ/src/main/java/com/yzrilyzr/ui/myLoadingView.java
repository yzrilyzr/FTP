package com.yzrilyzr.ui;
import android.view.*;
import android.content.*;
import android.util.*;
import android.graphics.*;
import com.yzrilyzr.myclass.*;
//import com.yzrilyzr.myclass.*;

public class myLoadingView extends View
{
//myLog ml;
    public myLoadingView(Context c)
    {
        this(c,null);
    }
    public myLoadingView(Context c,AttributeSet a)
    {
        super(c,a);
        paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(uidata.UI_COLOR_MAIN);
        paint.setStyle(Paint.Style.STROKE);
    }
    @Override
    protected void onDraw(Canvas canvas)
    {
        // TODO: Implement t
        int w=getWidth(),h=getHeight();
        r=(float)Math.sqrt((double)(w*h))/10;
        paint.setStrokeWidth(r);
        canvas.drawArc(new RectF(r,r,w-r,h-r),first%360,second%360,false,paint);
        // TODO: Implement this method
        int s=330,d=7;
        if(second<s&&!b)
            second+=d;
        else if(second>=s&&!b)
            b=true;
        else if(second>360-s&&b)
        {
            second-=d;
            first+=d;
        }
        else if(second<=360-s&&b)
            b=false;
        first+=d;
        if(first>=360)first=0;
        invalidate();
    }
    public Paint paint;
    private boolean b=false;
    private float first=0,second=0,r;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // TODO: Implement this method
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int i=util.dip2px(60);
        //ml=new myLog(getContext());
        setMeasuredDimension(WidgetUtils.measureWidth(widthMeasureSpec,i),WidgetUtils.measureHeight(heightMeasureSpec,i));
    }

}
