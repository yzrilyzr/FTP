package com.yzrilyzr.ui;
import android.view.*;
import android.content.*;
import android.graphics.*;
import android.util.*;
import com.yzrilyzr.myclass.*;

public class myProgressBar extends View
{
    private Context ctx;
    private int progress=0,secondProgress=0,maxpro=100,wid=0,hei=0;
    public myProgressBar(Context c,AttributeSet a)
    {
        super(c,a);	
        ctx=c;
    }
    public myProgressBar(Context c)
    {this(c,null);}
    public int getProgress()
    {return progress;}
    public int getMax()
    {return maxpro;}
    public int getSecondaryProgress(){
        return secondProgress;
    }
    public void setMax(int m)
    {maxpro=m;invalidate();}
    public void setProgress(int m)
    {progress=m;invalidate();}
    public void setSecondaryProgress(int p)
    {secondProgress=p;invalidate();}
    @Override
    protected void onDraw(Canvas canvas)
    {
        // TODO: Implement this metho
        if(progress<0)progress=0;
        if(progress>maxpro)progress=maxpro;
        if(secondProgress<0)secondProgress=0;
        if(secondProgress>maxpro)secondProgress=maxpro;
        hei=getHeight();wid=getWidth();
        Paint paint=new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        float r=hei/2;
        paint.setColor(0xffeeeeee);
        canvas.drawRoundRect(new RectF(0,0,wid,hei),r,r,paint);
        paint.setColor(0xffaaaaaa);
        canvas.drawRoundRect(new RectF(0,0,wid*secondProgress/maxpro,hei),r,r,paint);
        paint.setColor(uidata.UI_COLOR_MAIN);
        canvas.drawRoundRect(new RectF(0,0,wid*progress/maxpro,hei),r,r,paint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(WidgetUtils.measureWidth(widthMeasureSpec,wid),WidgetUtils.measureHeight(heightMeasureSpec,util.dip2px(10)));
    }

}
