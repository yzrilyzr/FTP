package com.yzrilyzr.FAQ;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.yzrilyzr.FAQ.Data.ToStrObj;
import com.yzrilyzr.FAQ.Data.User;
import com.yzrilyzr.FAQ.Main.AES;
import com.yzrilyzr.FAQ.Main.C;
import com.yzrilyzr.FAQ.Main.ClientService;
import com.yzrilyzr.FAQ.Main.Data;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myRoundDrawable;

public class LoginActivity extends BaseActivity
{
	Activity ctx=this;
	EditText u,p;
	ImageView iv;
	myRoundDrawable defhead;
	boolean autoLog=true;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		autoLog=getIntent().getBooleanExtra("al",true);
		u=(EditText) findViewById(R.id.loginEditText1);
		iv=(ImageView) findViewById(R.id.loginImageView1);
		iv.setImageDrawable(defhead=new myRoundDrawable(this,R.drawable.launch));
		u.addTextChangedListener(new TextWatcher(){
				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
					setHead(p1.toString());
				}

				@Override
				public void afterTextChanged(Editable p1)
				{
					// TODO: Implement this method

				}
			});
		p=(EditText) findViewById(R.id.loginEditText2);
		try
		{
			SharedPreferences sp=getSharedPreferences("login",MODE_PRIVATE);
			String h=null;
			u.setText(h=sp.getString("user",""));
			String f=sp.getString("pwd","");
			if(!"".equals(f))
			{
				p.setText(AES.decrypt(h,f));
				if(autoLog)login(null);
			}
		}
		catch(Throwable e)
		{}
	}
	private void setHead(String s)
	{
		Bitmap h=BitmapFactory.decodeFile(util.mainDir+"/head/"+s+".user.png");
		myRoundDrawable hd2=null;
		if(h==null)hd2=defhead;
		else hd2=new myRoundDrawable(h);
		iv.setImageDrawable(hd2);
	}

	@Override
	public void rev(final byte cmd,final String msg)
	{
		// TODO: Implement this method
		ctx.runOnUiThread(new Runnable(){
				@Override
				public void run()
				{
					// TODO: Implement this method
					if(cmd==C.USE)ClientService.toast(ctx,"用户名错误");
					else if(cmd==C.PWE)ClientService.toast(ctx,"密码错误");
					else if(cmd==C.LSU)
					{
						ClientService.sendMsg(C.GUS,Data.me.faq+"");
					}
					else if(cmd==C.GHU){
						byte[] by=Base64.decode(msg,0);
						Data.saveHead(false,by);
						startService(new Intent(ctx,MsgService.class));
						ClientService.toast(ctx,"登录成功");
						ClientService.isLogin=true;
						startActivity(new Intent(ctx,ListActivity.class));
						finish();
					}
					else if(cmd==C.GUS)
					{
						Data.me=User.s2o(msg);
						util.toast(ctx,Data.me.friends.size()+"");
						byte[] by=Data.getHead(Data.me.faq+"",false);
						if(by==null)ClientService.sendMsg(C.GHU,Data.me.faq+"");
						else{
							ClientService.toast(ctx,"登录成功");
							startService(new Intent(ctx,MsgService.class));
							ClientService.isLogin=true;
							startActivity(new Intent(ctx,ListActivity.class));
							finish();
						}
					}
				}
			});
	}
	public void login(View v)
	{
		final String a=u.getText().toString();
		final String b=p.getText().toString();
		if("".equals(a)||"".equals(b))ClientService.toast(ctx,"帐号或密码不能为空");
		else ClientService.login(a,b);
		try
		{
			setHead(a);
			getSharedPreferences("login",MODE_PRIVATE).edit()
				.putString("user",a)
				.putString("pwd",AES.encrypt(a,b))
				.commit();
		}
		catch (Exception e)
		{}
	}

	public void help(View v)
	{

	}
	public void register(View v)
	{
		startActivity(new Intent(this,RegisterActivity.class));
	}
}
