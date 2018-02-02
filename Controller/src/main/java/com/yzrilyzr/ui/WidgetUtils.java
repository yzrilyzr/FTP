package com.yzrilyzr.ui;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import com.yzrilyzr.FAQ.Controller.R;
public class WidgetUtils
	{

		public static int measureHeight(int measureSpec, int def)
			{
				int result = 0;
				int mode = MeasureSpec.getMode(measureSpec);
				int size = MeasureSpec.getSize(measureSpec);

				if (mode == MeasureSpec.EXACTLY)
					{
						result = size;
					}
				else
					{
						result = def;
						if (mode == MeasureSpec.AT_MOST)
							{
								result = Math.min(result, size);
							}
					}
				return result;

			}
//setMeasuredDimension(WidgetUtils.measureWidth(widthMeasureSpec,wid),WidgetUtils.measureHeight(heightMeasureSpec,tool.dip2px(ctx,30)));
		public static int measureWidth(int measureSpec, int def)
			{
				int result = 0;
				int mode = MeasureSpec.getMode(measureSpec);
				int size = MeasureSpec.getSize(measureSpec);

				if (mode == MeasureSpec.EXACTLY)
					{
						result = size;
					}
				else
					{
						result = def;
						if (mode == MeasureSpec.AT_MOST)
							{
								result = Math.min(result, size);
							}
					}
				return result;

			}	
		public static Drawable getIcon(Context ctx,AttributeSet attr,int[] styleable,int ResId){
			TypedArray t=ctx.obtainStyledAttributes(attr,styleable);
			Drawable d=null;
			try{d=t.getDrawable(ResId);}catch(Exception e){}
			String s=t.getString(ResId);
			if(d==null&&s!=null&&s.indexOf("preset")!=-1)d=new BitmapDrawable(uidata.getIconByName(s.replace("preset/","")));
			return d;
		}
		public static void setIcon(ImageView v,AttributeSet a){
			if(a==null)return;
				Drawable d=WidgetUtils.getIcon(v.getContext(),a,R.styleable.yzr,R.styleable.yzr_src);
				if(d!=null)v.setImageDrawable(d);
		}
	}
