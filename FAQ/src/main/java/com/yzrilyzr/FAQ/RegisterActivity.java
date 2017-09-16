package com.yzrilyzr.FAQ;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.yzrilyzr.FAQ.Main.C;
import com.yzrilyzr.FAQ.Main.ClientService;
import com.yzrilyzr.FAQ.RegisterActivity;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.ui.myAlertDialog;
import com.yzrilyzr.FAQ.Data.User;

public class RegisterActivity extends BaseActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
	}

	@Override
	public void rev(final byte cmd,final String msg)
	{
		// TODO: Implement this method
		if(cmd==C.RSU)
		{
			runOnUiThread(new Runnable(){
					@Override
					public void run()
					{
						// TODO: Implement this method
						new myAlertDialog(ctx)
							.setTitle("注册成功")
							.setMessage("您的FAQ号:"+msg)
							.setPositiveButton("确定",null)
							.show();
					}
				});
		}
		else if(cmd==C.VFE)util.toast(ctx,"验证码错误");
		else if(cmd==C.RFL)util.toast(ctx,"注册失败");
		else if(cmd==C.VFD)util.toast(ctx,"重新发送邮件还有"+msg+"秒");
	}
	public void register(View v)
	{
		final String a=((EditText)findViewById(R.id.registerEditText1)).getText().toString();
		String b=((EditText)findViewById(R.id.registerEditText2)).getText().toString();
		String c=((EditText)findViewById(R.id.registerEditText3)).getText().toString();
		String d=((EditText)findViewById(R.id.registerEditText4)).getText().toString();
		if("".equals(a)||"".equals(b))util.toast(this,"密码不能为空");
		else if("".equals(c))util.toast(this,"邮箱地址不能为空");
		else if(!a.equals(b))util.toast(this,"两次输入的密码不一致");
		else if(c.split("@").length!=2)util.toast(this,"邮箱地址不合法");
		else if(a.length()<6)util.toast(this,"密码长度不能小于6位");
		else if(d.equals(""))util.toast(this,"验证码不能为空");
		else if(a.equals(b))
		{
			User us=new User();
			us.pwd=a;
			us.email=c;
			us.nick=d;
			ClientService.sendMsg(C.REG,us.o2s());
		}
	}
	public void send(View v){
		String c=((EditText)findViewById(R.id.registerEditText3)).getText().toString();
		if(c.equals(""))util.toast(this,"邮箱地址不能为空");
		else if(c.split("@").length!=2)util.toast(this,"邮箱地址不合法");
		else ClientService.sendMsg(C.VFC,c);
	}
}
