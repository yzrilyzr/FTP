package com.yzrilyzr.ui;
import android.widget.*;
import android.content.*;
import android.util.*;

public class myLinearLayout extends LinearLayout
	{
		public myLinearLayout(Context c, AttributeSet a)
			{
				super(c, a);
				init();
			}
		public myLinearLayout(Context c)
			{
				super(c);
				init();
			}
		public void init()
			{
				setBackgroundColor(uidata.UI_COLOR_MAIN);
			}
	}
