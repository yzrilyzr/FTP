package com.yzrilyzr.ui;
import android.content.*;
import android.util.*;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.graphics.drawable.shapes.*;
import android.graphics.drawable.*;
import javax.crypto.spec.*;
import android.transition.*;
import com.yzrilyzr.myclass.*;

public class mySeekBar extends View
	{
		private Paint paint;
		private OnChange oc=null;
		private boolean ondown=false;
		private Context ctx;
		private float drawx=0;

		private float prowidstart,prowidend,curprowid;
		private int progress=0,maxpro=100,wid=0,hei=0,lastpro=0,tmppro=0;
		public mySeekBar(Context c, AttributeSet a)
			{
				super(c, a);	
				ctx = c;
				paint = new Paint();
				paint.setDither(true);
				paint.setAntiAlias(true);
				paint.setStyle(Paint.Style.FILL);

			}
		public mySeekBar(Context c)
			{
				this(c, null);
			}
		public void setOnChangeListener(OnChange o)
			{oc = o;}
		public interface OnChange
			{
				public abstract void onChange(mySeekBar msb, int progress);
				public abstract void onDown(mySeekBar msb);
				public abstract void onUp(mySeekBar msb);
			}
		public int getProgress()
			{return progress;}
		public int getMax()
			{return maxpro;}
		public void setMax(int m)
			{maxpro = m;invalidate();}
		public void setProgress(int m)
			{
				tmppro = m;
				if (m >= 0 && m <= maxpro)
					{progress = m;
						drawx = prowidstart + curprowid * m / maxpro;
						invalidate();
					}}
		@Override
		protected void onDraw(Canvas canvas)
			{
				// TODO: Implement this metho
				hei = getHeight();wid = getWidth();
				int circler=hei / 3;
				prowidstart = circler * 1.2f;
				prowidend = wid - circler * 1.2f;
				curprowid = prowidend - prowidstart;
				if (drawx <= prowidstart)drawx = prowidstart;
				if (drawx >= prowidend)drawx = prowidend;
				paint.setColor(0xffeeeeee);
				//paint.setShader(new LinearGradient(0,circler,0,circler*2,new int[]{0xffffffff,uidata.UI_COLOR_MAINHL/*,uidata.UI_COLOR_MAIN*/},null,Shader.TileMode.CLAMP));
				canvas.drawRoundRect(new RectF(prowidstart, circler, prowidend, circler * 2), circler / 2, circler / 2, paint);
				//paint.setShader(null);//new LinearGradient(0,circler,0,circler*2,new int[]{uidata.UI_COLOR_MAIN,uidata.UI_COLOR_BACK,uidata.UI_COLOR_MAIN},null,Shader.TileMode.REPEAT));
				paint.setColor(uidata.UI_COLOR_MAIN);
				canvas.drawRoundRect(new RectF(prowidstart, circler, drawx, circler * 2), circler / 2, circler / 2, paint);
				//paint.setShader(null);//new RadialGradient(drawx,hei/2,circler,uidata.UI_COLOR_MAINHL,uidata.UI_COLOR_MAIN,Shader.TileMode.CLAMP));
				paint.setColor(0xffffffff);
				if (ondown)canvas.drawCircle(drawx, hei / 2, circler * 1.2f, paint);
				canvas.drawCircle(drawx, hei / 2, circler, paint);//画滑块,y位置为中心
				//super.onDraw(canvas);
			}

		@Override
		public boolean onTouchEvent(MotionEvent event)
			{
				// TODO: Implement this method
				getParent().requestDisallowInterceptTouchEvent(true);
				drawx = event.getX();
				if (drawx <= prowidstart)drawx = prowidstart;
				if (drawx >= prowidend)drawx = prowidend;

				progress = (int)Math.ceil((drawx - prowidstart) / curprowid * (float)maxpro);
				switch (event.getAction())
					{
						case MotionEvent.ACTION_DOWN:
							if (oc != null)oc.onDown(this);
							ondown = true;
							invalidate();
							break;
						case MotionEvent.ACTION_UP:
							if (oc != null)oc.onUp(this);
							ondown = false;
							//setProgress(progress);
							invalidate();
							break;
						case MotionEvent.ACTION_MOVE:
							if (lastpro != progress)
								{
									if (oc != null)oc.onChange(this, progress);
									lastpro = progress;
									setProgress(progress);
									ondown = true;
									invalidate();
								}
							break;

					}
				return true;//super.onTouchEvent(event);
			}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
			{
				setMeasuredDimension(WidgetUtils.measureWidth(widthMeasureSpec, wid), WidgetUtils.measureHeight(heightMeasureSpec, util.dip2px(30)));
				setProgress(tmppro);
			}
	}
