package com.yzrilyzr.myclass;
import android.content.*;
import android.widget.*;
import com.yzrilyzr.ui.*;
import android.app.*;
import android.view.*;
import android.view.View.*;
import android.os.*;

public class myLog implements Runnable,OnTouchListener
	{
		private Context ctx;
		private Activity act;
		private PopupWindow win;
		private myTextViewBack text;
		private Object[] obj;
		private int x=0,y=0;
		public myLog(Context c)
			{
				ctx = c;
				text = new myTextViewBack(c);	
				win = new PopupWindow(text, -2, -2, false);
				text.setOnTouchListener(this);
				new Handler(c.getMainLooper()).postDelayed(new Runnable(){@Override public void run()
								{
									show();
								}}, 1000);
			}

		int lastX, lastY;
		int paramX, paramY;
		@Override
		public boolean onTouch(View v, MotionEvent event)
			{
				switch (event.getAction())
					{
						case MotionEvent.ACTION_DOWN:
							lastX = (int) event.getRawX();
							lastY = (int) event.getRawY();
							paramX = x;
							paramY = y;
							break;
						case MotionEvent.ACTION_MOVE:
							int dx = (int) event.getRawX() - lastX;
							int dy = (int) event.getRawY() - lastY;
							x = paramX + dx;
							y = paramY + dy;
							win.update(x, y, -2, -2);
							break;
					}
				return true;
			}
		@Override
		public void run()
			{
				// TODO: Implement this method
				try
					{
						String s="";
						for (Object o:obj)
							{
								s += o.toString() + "\n";
							}
						text.setText(s);
					}
				catch (Exception e)
					{}
			}


		public void log(Object... p)
			{
				try
					{
						obj = p;
						act.runOnUiThread(this);
					}
				catch (Exception e)
					{}
			}
		public void dismiss()
			{
				win.dismiss();
			}
		public void show()
			{
				win.showAtLocation((act = (Activity)ctx).getWindow().getDecorView(), Gravity.CENTER, 0, 0);
			}
	}
