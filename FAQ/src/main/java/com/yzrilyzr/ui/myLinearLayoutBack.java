package com.yzrilyzr.ui;
import android.content.*;
import android.util.*;
import android.widget.*;

public class myLinearLayoutBack extends myLinearLayout
	{
		public myLinearLayoutBack(Context c,AttributeSet a){
				super(c,a);
				init();
			}
		public myLinearLayoutBack(Context c){
				super(c);
				init();
			}
		public void init(){
				setBackgroundColor(uidata.UI_COLOR_BACK);
			}
	}
