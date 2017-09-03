package com.yzrilyzr.ui;
import android.content.*;
import android.util.*;
import android.content.res.*;
import android.graphics.*;

public class myTextView extends android.widget.TextView
{
	protected final static int DEF=0,BACK=1,TITLE=2,TITLEBACK=3;
	public myTextView(Context c,AttributeSet a){
		super(c,a);
		init(DEF);
	}
	public myTextView(Context c){
		super(c);
		init(DEF);
	}
	protected void init(int type){
			setTextColor(type==DEF||type==TITLE?uidata.UI_TEXTCOLOR_MAIN:uidata.UI_TEXTCOLOR_BACK);
			if(uidata.UI_USETYPEFACE)setTypeface(uidata.UI_TYPEFACE);
			setTextSize(type==DEF||type==BACK?uidata.UI_TEXTSIZE_DEFAULT:uidata.UI_TEXTSIZE_TITLE);
	}
}
