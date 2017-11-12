package com.yzrilyzr.FAQ.Server;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

public class MainActivity extends Activity 
{
	MainActivity ctx;
	LongTextView te;
	static Loader loader;
	static String logbuff=null;
	@Override
    protected void onCreate(Bundle savedInstanceState)
    {
		Thread.currentThread().setName("FAQServer_MainActivity");
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
		te=(LongTextView) findViewById(R.id.mainTextView1);
		ctx=this;
		if(loader==null)loader=new Loader(this);
		loader.setCtx(this);
		if(logbuff!=null)te.setText(logbuff);
		//((ToggleButton)findViewById(R.id.mainToggleButton1)).setChecked(started);
    }
	public void send(View v)
	{
		EditText e=(EditText)((ViewGroup)v.getParent()).getChildAt(0);
		String s=e.getText().toString();
		loader.cmd(s);
		e.setText("");
	}
	@Override
	protected void onDestroy()
	{
		logbuff=te.getText();
		super.onDestroy();
	}
	public void toast(String o)
	{
		te.addText(o);
		scroll();
	}
	private void scroll()
	{
		try
		{
			int a=te.stringLines.size();
			if(a!=0)
			{
				te.currentLine=a-1;
				te.cursorStart=te.stringLines.get(a-1).length();
				te.yOff=-te.th*a+ctx.getWindowManager().getDefaultDisplay().getHeight()*0.8f;
			}
			else te.yOff=0;
		}
		catch(Throwable e)
		{}
	}
	public void logout(View v)
	{

	}
	public void open(View v)
	{

	}
	public void us(View v)
	{
		startActivity(new Intent(this,UserManager.class));
	}
	public void control(View v)
	{
		startActivity(new Intent(this,ControlActivity.class));
	}
	public void clear(View v)
	{
		te.clear();
	}
	public void fstop(View v)
	{
		System.exit(0);
	}
	
}
