package com.yzrilyzr.FAQ;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.yzrilyzr.FAQ.Main.C;
import com.yzrilyzr.FAQ.Main.ClientService;
import com.yzrilyzr.FAQ.Main.Data;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.uidata;
import com.yzrilyzr.ui.myAlertDialog;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AboutActivity extends BaseActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		String a="Copyright © 2017 yzrilyzr";
		TextView t=(TextView) findViewById(R.id.aboutmyTextView1);
		if(!t.getText().toString().equals(a))
		{
			StringBuilder sb=new StringBuilder();
			sb.append('请').append('勿').append('使').append('用')
				.append('盗').append('版').append('软').append('件');
			util.toast(ctx,sb.toString());
			uidata.mod=true;
			uidata.saveData(ctx);
		}
	}
	public void bug(View v)
	{

	}
	public void web(View v)
	{
		Intent in=new Intent(ctx,BrowserActivity.class);
		in.putExtra("url","http://"+ClientService.hostIp+":10002");
		startActivity(in);
	}
	public void abo(View v)
	{
		StringBuilder me=new StringBuilder();
		try
		{
			BufferedReader dis=new BufferedReader(new InputStreamReader(getAssets().open("txt/about")));
			String buff=null;
			while((buff=dis.readLine())!=null)
			{
				me.append(buff);
				me.append("\n");
			}
			dis.close();
		}
		catch (IOException e)
		{}
		new myAlertDialog(ctx)
			.setTitle("关于")
			.setMessage(me.toString())
			.setPositiveButton("确定",null)
			.show();
	}
	public void upd(View v)
	{
		ClientService.sendMsg(C.UPD);
	}

	@Override
	public void rev(byte cmd, String msg)
	{
		// TODO: Implement this method
		if(cmd==C.UPD)
		{
			String[] ms=msg.split("/");
			final int i=Data.getInt(ms[0].getBytes());
			runOnUiThread(new Runnable(){
					@Override
					public void run()
					{
						// TODO: Implement this method
						if(util.VersionCode<i)
						{
							util.toast(ctx,"发现新版本\n请到上面的网站下载");
						}
						else util.toast(ctx,"当前已经是最新版本");
					}
				});

		}
	}

}
