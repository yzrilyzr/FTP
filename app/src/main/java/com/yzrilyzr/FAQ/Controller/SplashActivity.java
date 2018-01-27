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

public class SplashActivity extends BaseActivity
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
			setContentView(R.layout.splash);
			inited=true;
			SharedPreferences sp=getSharedPreferences("server",MODE_PRIVATE);
			ClientService.hostIp=sp.getString("ip","127.0.0.1");
			ClientService.startService();
			ClientService.sendMsg(C.CON);
		}
		else
		{
			startActivity(new Intent(ctx,MainActivity.class));
			finish();
		}
	}
	@Override
	public void rev(byte cmd, String msg)
	{
		if(cmd==C.CON)ClientService.sendMsg(C.LGN);
		else if(cmd==C.LGN)runOnUiThread(new Runnable(){
					@Override
					public void run()
					{
						util.toast(ctx,"连接成功");
						startActivity(new Intent(ctx,MainActivity.class));
						finish();
					}
				});
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
