package com.yzrilyzr.FAQ.Server;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.ImageReader;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.view.Display;
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
	MediaProjectionManager mgr;
	static ImageReader imr;
	static AvcEncoder avc;
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
		rec();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO: Implement this method
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==6)
		{
			try
			{
				MediaProjection m=mgr.getMediaProjection(resultCode,data);
				Display d=getWindowManager().getDefaultDisplay();
				int w=d.getWidth(),h=d.getHeight();
				int dpi=getResources().getDisplayMetrics().densityDpi;
				imr=ImageReader.newInstance(w,h,PixelFormat.RGBA_8888,5);
				m.createVirtualDisplay("record",w,h,dpi,DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,imr.getSurface(),null,null);
				//avc=new AvcEncoder(w,h,200,30);
			}
			catch (Exception e)
			{
				toast(e+"");
			}
		}
	}

	public void rec()
	{
		if(imr!=null)return;
		mgr=(MediaProjectionManager) ctx.getSystemService(ctx.MEDIA_PROJECTION_SERVICE);
		Intent in=mgr.createScreenCaptureIntent();
		ctx.startActivityForResult(in,6);
	}
}
