package com.yzrilyzr.ui;
import android.graphics.*;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.yzrilyzr.myclass.util;


public class myRippleDrawable extends Drawable
{
    private Paint paint,rpaint;
    private RectF rectF;
    private float radius,Rippleradius,xx,yy,speed=30f;
    private int margin=0,width,height;
    private boolean isRipple=false;
    public myRippleDrawable(int color,int rcolor,float radius)
    {
        this.radius=radius;
        xx=0;yy=0;
        Rippleradius=0;
        paint=new Paint(Paint.ANTI_ALIAS_FLAG);//初始化画笔
        rpaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        rpaint.setColor(rcolor);
        rpaint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
    }
    public myRippleDrawable()
    {
		radius=uidata.UI_RADIUS;
		xx=0;yy=0;
		Rippleradius=0;
		paint=new Paint(Paint.ANTI_ALIAS_FLAG);//初始化画笔
		rpaint=new Paint(Paint.ANTI_ALIAS_FLAG);
		rpaint.setColor(uidata.UI_COLOR_MAINHL);
		rpaint.setStyle(Paint.Style.FILL);
		paint.setColor(uidata.UI_COLOR_MAIN);
		paint.setStyle(Paint.Style.FILL);
    }
    public void setLayer(View v)
    {
        if(!uidata.UI_USESHADOW)return;
		margin=util.dip2px(3);
		paint.setShadowLayer(margin,0,margin/3,0x88666666);
		//int l=v.getPaddingLeft(),t=v.getPaddingTop(),r=v.getPaddingRight(),b=v.getPaddingBottom();
		//v.setPadding(l,t,r,b+=margin);
		v.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
    }
    @Override
    public void setBounds(int left, int top, int right, int bottom)
    {
        super.setBounds(left, top, right, bottom);
        width=right;height=bottom;
        rectF = new RectF(margin,margin, right-margin, bottom-margin);
        bitmap=Bitmap.createBitmap(right,bottom,Bitmap.Config.ARGB_8888);
        new Canvas(bitmap).drawRoundRect(rectF,radius,radius,new Paint(Paint.ANTI_ALIAS_FLAG));
    }
    @Override
    public void draw(Canvas canvas)
    {
        canvas.drawRoundRect(rectF,radius,radius,paint);
        int sc=canvas.saveLayer(0,0,width,height, null, Canvas.ALL_SAVE_FLAG);  
        canvas.drawCircle(xx,yy,Rippleradius,rpaint);
        rpaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));  
        canvas.drawBitmap(bitmap,0,0,rpaint);  
        rpaint.setXfermode(null);  
        canvas.restoreToCount(sc);
        float w=(float)Math.sqrt(rectF.right*rectF.right+rectF.bottom*rectF.bottom);
        if(Rippleradius<w&&isRipple)
        {
            Rippleradius+=w/speed;
            invalidateSelf();
        }
        else if(isRipple){
            Rippleradius=0;
            isRipple=false;
            invalidateSelf();
        }
    }
	private Bitmap bitmap;
    public void longRipple(float x,float y)
    {
        Rippleradius=0;
        isRipple=true;
        speed=60f;
        xx=x;
        yy=y;
        invalidateSelf();
    }
    public void shortRipple(float x,float y)
    {
        Rippleradius=0;
        isRipple=true;
        speed=30f;
        xx=x;
        yy=y;
        invalidateSelf();
    }
    public void selector(boolean focus)
    {
        if(focus)Rippleradius=(int)Math.max((double)rectF.width(),(double)rectF.height());
        else Rippleradius=0;
        invalidateSelf();
    }
    //设置透明度
    @Override
    public void setAlpha(int alpha)
    {
    }
    //设置滤镜渲染颜色
    @Override
    public void setColorFilter(ColorFilter colorFilter)
    {
    }
    //获取透明图
    @Override
    public int getOpacity()
    {
        return 0;
    }

}
