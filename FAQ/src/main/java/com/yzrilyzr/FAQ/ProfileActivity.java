package com.yzrilyzr.FAQ;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.yzrilyzr.FAQ.Data.Group;
import com.yzrilyzr.FAQ.Data.MessageObj;
import com.yzrilyzr.FAQ.Data.ToStrObj;
import com.yzrilyzr.FAQ.Data.User;
import com.yzrilyzr.FAQ.Main.C;
import com.yzrilyzr.FAQ.Main.ClientService;
import com.yzrilyzr.FAQ.Main.Data;
import com.yzrilyzr.FAQ.Main.T;
import com.yzrilyzr.myclass.util;

public class ProfileActivity extends BaseActivity
{
	boolean isGroup=false;
	int faq;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		faq=getIntent().getIntExtra("faq",-1);
		if(faq!=-1)
		{
			Data.getUser(faq);
			ClientService.sendMsg(C.GUS,faq+"");
			((ImageView)findViewById(R.id.profileImageView1)).setImageDrawable(Data.getHeadDrawable(faq,isGroup));
		}
		else if(faq==-1)
		{
			faq=getIntent().getIntExtra("gro",-1);
			isGroup=true;
			ClientService.sendMsg(C.GGR,faq+"");
			((ImageView)findViewById(R.id.profileImageView1)).setImageDrawable(Data.getHeadDrawable(faq,isGroup));
		}
	}
	public void add(View v)
	{
		ClientService.sendMsg(C.MSG,new MessageObj(Data.myfaq,faq,T.VMS,false,String.format("%s 请求添加您为好友",Data.getMyself().nick)).setTime().o2s());
		util.toast(ctx,"已发送请求，等待同意");
	}
	@Override
	public void rev(final byte cmd, final String msg)
	{
		// TODO: Implement this method
		if(cmd==C.GUS||cmd==C.GGR)
			runOnUiThread(new Runnable(){
					@Override
					public void run()
					{
						// TODO: Implement this method
						TextView nick=(TextView) findViewById(R.id.profileTextView1);
						if("-1".equals(msg))
						{
							nick.setText(String.format("查无此%s",isGroup?"群":"人"));
							return;
						}
						if(!isGroup)
						{
							User u=(User) ToStrObj.s2o(msg);
							if(u.faq!=faq)return;
							nick.setText(u.nick);
							((TextView) findViewById(R.id.profileTextView2)).setText(String.format("%s:%s",isGroup?"描述":"个性签名",u.sign));
							((TextView) findViewById(R.id.profileTextView3)).setText(String.format("FAQ:%d",u.faq));
						}
						else
						{
							Group u=(Group) Group.s2o(msg);
							if(u.faq!=faq)return;
							nick.setText(u.nick);
							((TextView) findViewById(R.id.profileTextView2)).setText(String.format("%s:%s",isGroup?"描述":"个性签名",u.sign));
							((TextView) findViewById(R.id.profileTextView3)).setText(String.format("FAQ:%d",u.faq));
						}

					}
				});
		else if(cmd==C.GHG||cmd==C.GHU)
			runOnUiThread(new Runnable(){
					@Override
					public void run()
					{
						((ImageView)findViewById(R.id.profileImageView1)).setImageDrawable(Data.getHeadDrawable(faq,isGroup));
					}});
	}


}
