package com.yzrilyzr.FAQ.Controller;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import com.yzrilyzr.FAQ.Main.RemoteControlService;
import java.io.IOException;
import android.graphics.Matrix;

public class ControlActivity extends BaseActivity implements Runnable,RemoteControlService.Listener
{
	SurfaceView sur;
	Paint p;
	boolean aa=false;
	RemoteControlService re;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN|WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		sur=new SurfaceView(this);
		p=new Paint();
		setContentView(sur);
		re=new RemoteControlService();
		re.setListener(this);
		re.startService();
		new Thread(this).start();
	}
	@Override
	public void rev(byte[] data)
	{
		if(data!=null)
		{
			Bitmap bb=BitmapFactory.decodeByteArray(data,0,data.length);
			SurfaceHolder h=sur.getHolder();
			Canvas c=h.lockCanvas();
			if(c==null)return;
			c.drawColor((aa=!aa)?0xffffffff:0xffeeeeee);
			c.drawBitmap(bb,0,0,p);
			h.unlockCanvasAndPost(c);
			bb.recycle();
		}
	}
	@Override
	public void run()
	{
		while(true)
			try
			{
				re.get();
				Thread.sleep(10);
			}
			catch (Exception e)
			{}
	}
}
