package com.yzrilyzr.FAQ.Controller;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import com.yzrilyzr.FAQ.Main.C;
import com.yzrilyzr.FAQ.Main.ClientService;
import com.yzrilyzr.myclass.myActivity;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myAlertDialog;
import com.yzrilyzr.ui.myDialogInterface;
import com.yzrilyzr.ui.myEditText;
import com.yzrilyzr.ui.uidata;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

public class SplashActivity extends myActivity
{
	static boolean inited=false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if(!inited)
		{
			uidata.readData(this);
			uidata.initIcon(this);
			inited=true;
			SharedPreferences sp=getSharedPreferences("server",MODE_PRIVATE);
			ClientService.hostIp=sp.getString("ip","127.0.0.1");
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
								Thread.sleep(300);
							}
							catch(SocketTimeoutException ste)
							{
								util.toast(ctx,"连接超时\n5秒后重试");
								ClientService.deckey=null;
								try
								{
									Thread.sleep(4000);
								}
								catch (InterruptedException ey)
								{}
							}
							catch (Exception e)
							{
								util.toast(ctx,"无法连接到服务器\n5秒后重试");
								ClientService.deckey=null;
								try
								{
									Thread.sleep(4000);
								}
								catch (InterruptedException ey)
								{}
							}
						util.toast(ctx,"连接成功");
						startActivity(new Intent(ctx,MainActivity.class));
						finish();
						ClientService.sendMsg(C.LGN);
					}
				}).start();
			setContentView(R.layout.splash);
		}
		else
		{
			startActivity(new Intent(ctx,MainActivity.class));
			finish();
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
}
