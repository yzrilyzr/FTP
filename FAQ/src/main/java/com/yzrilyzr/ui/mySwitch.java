package com.yzrilyzr.ui;
import android.graphics.*;
import android.view.*;

import android.content.Context;
import android.util.AttributeSet;
import com.yzrilyzr.FAQ.R;
import com.yzrilyzr.myclass.util;

public class mySwitch extends View
{
    private Context ctx;
    private float drawx=0;
    private String method;
    private boolean isChecked=false,isOn=false,last=false;
    private OnCheckedChangeListener occl=null;

    private Paint paint;
    public mySwitch(Context c,AttributeSet a)
    {
        super(c,a);
        method=c.obtainStyledAttributes(a,R.styleable.yzr).getString(R.styleable.yzr_onClick);
        ctx=c;
        paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        
    }
    public mySwitch(Context c)
    {this(c,null);}
    public void setChecked(boolean c)
    {
        isChecked=c;invalidate();
    }
    public boolean getChecked()
    {
        return isChecked;
    }
    public void setListener(OnCheckedChangeListener o)
    {
        occl=o;
    }
    public interface OnCheckedChangeListener
    {
        public abstract void onCheckedChange(mySwitch mcb,boolean state);
    }
    @Override
    protected void onDraw(Canvas canvas)
    {
        // TODO: Implement this method
         int w=getWidth(),h=getHeight();
        float radius=h/3;
        if(drawx<radius)drawx=radius;
        if(drawx>w-radius)drawx=w-radius;
        paint.setColor(0xffeeeeee);
        //paint.setShader(new LinearGradient(0,radius,0,h-radius,new int[]{0xffffffff,uidata.UI_COLOR_MAINHL/*,uidata.UI_COLOR_MAIN*/},null,Shader.TileMode.CLAMP));
        canvas.drawRoundRect(new RectF(radius/2,radius,w-radius/2,radius*2),radius/2,radius/2,paint);
        paint.setColor(uidata.UI_COLOR_MAIN);
        //paint.setShader(new LinearGradient(0,radius,0,h-radius,new int[]{uidata.UI_COLOR_MAIN,uidata.UI_COLOR_BACK,uidata.UI_COLOR_MAIN},null,Shader.TileMode.CLAMP));
        canvas.drawRoundRect(new RectF(radius/2,radius,drawx,radius*2),radius/2,radius/2,paint);
        paint.setColor(0xffffffff);
        //paint.setShader(new RadialGradient(drawx,h/2,radius,uidata.UI_COLOR_MAINHL,uidata.UI_COLOR_MAIN,Shader.TileMode.CLAMP));
        canvas.drawCircle(drawx,h/2,radius,paint);
        if(!isOn&&!isChecked&&drawx>radius)
        {
            drawx-=w/20;
            invalidate();
        }
        if(!isOn&&isChecked&&drawx<w-radius)
        {
            drawx+=w/20;
            invalidate();
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // TODO: Implement this method
        getParent().requestDisallowInterceptTouchEvent(true);
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                isOn=true;last=false;
                break;
            case MotionEvent.ACTION_MOVE:
                isOn=true;
                drawx=event.getX();
                boolean l=drawx>getWidth()/2;
                last=l==isChecked;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isChecked=drawx>getWidth()/2;
                if(last)isChecked=!isChecked;
                isOn=false;
                invalidate();
                if(occl!=null)occl.onCheckedChange(this,isChecked);
                if(method!=null)util.call(getContext().getClass(),method,new Class[]{View.class,boolean.class},getContext(),new Object[]{this,isChecked});
                break;
        }
        return true;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(WidgetUtils.measureWidth(widthMeasureSpec,util.dip2px(50)),WidgetUtils.measureHeight(heightMeasureSpec,util.dip2px(40)));
    }

}
