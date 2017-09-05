package com.yzrilyzr.FAQ;
import com.yzrilyzr.ui.*;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import com.yzrilyzr.FAQ.Main.ClientService;
import com.yzrilyzr.FAQ.Main.Data;
import com.yzrilyzr.FAQ.SplashActivity;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myIconDrawer.DrawType;
import com.yzrilyzr.ui.uidata.icon;

public class SplashActivity extends BaseActivity 
{

	static boolean isInit=false;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		SharedPreferences sp=getSharedPreferences("server",MODE_PRIVATE);
		ClientService.hostIp=sp.getString("ip","127.0.0.1");
		if(!isInit)
		{
			uidata.readData(this);
			initIcon();
		}
		isInit=true;
		super.onCreate(savedInstanceState);
		Data.DefaultHead=new myRoundDrawable(ctx,R.drawable.launcher);
		if(ClientService.isLogin)
		{
			startActivity(new Intent(SplashActivity.this,ListActivity.class));
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
								isc=false;
								Thread.sleep(500);
								runOnUiThread(new Runnable(){
										@Override
										public void run()
										{
											// TODO: Implement this metho
											startActivity(new Intent(SplashActivity.this,LoginActivity.class));
											finish();
										}
									});
							}
							catch (Exception e)
							{
								ClientService.toast(SplashActivity.this,"无法连接到服务器\n5秒后重试");
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
