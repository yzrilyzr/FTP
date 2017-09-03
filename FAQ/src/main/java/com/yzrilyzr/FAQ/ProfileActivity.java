package com.yzrilyzr.FAQ;
import android.graphics.*;
import android.widget.*;
import com.yzrilyzr.FAQ.Data.*;
import com.yzrilyzr.FAQ.Main.*;

import android.os.Bundle;
import android.view.View;
import com.yzrilyzr.myclass.myActivity;
import com.yzrilyzr.ui.myRoundDrawable;
import com.yzrilyzr.myclass.util;
import android.util.Base64;

public class ProfileActivity extends BaseActivity
{
	boolean isUser=true;
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
			isUser=true;
			ClientService.sendMsg(C.GUS,faq+"");
			byte[] hd=Data.getHead(faq+"",!isUser);
			if(hd==null)ClientService.sendMsg(C.GHU,faq+"");
			else{
				Bitmap b=BitmapFactory.decodeByteArray(hd,0,hd.length);
				((ImageView)findViewById(R.id.profileImageView1)).setImageDrawable(new myRoundDrawable(b));
			}
		}
		else if(faq==-1)
		{
			faq=getIntent().getIntExtra("gro",-1);
			isUser=false;
			ClientService.sendMsg(C.GGR,faq+"");
			byte[] hd=Data.getHead(faq+"",!isUser);
			if(hd==null)ClientService.sendMsg(C.GHG,faq+"");
			else{
				Bitmap b=BitmapFactory.decodeByteArray(hd,0,hd.length);
				((ImageView)findViewById(R.id.profileImageView1)).setImageDrawable(new myRoundDrawable(b));
			}
		}
	}
public void add(View v)
	{
		ClientService.sendMsg(C.MSG,new MessageObj(Data.me.faq,faq,T.VMS,false,String.format("%s 请求添加您为好友",Data.me.nick)).setTime().o2s());
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
							nick.setText(String.format("查无此%s",isUser?"人":"群"));
							return;
						}
						if(isUser)
						{
							User u=User.s2o(msg);
							if(u.faq!=faq)return;
							nick.setText(u.nick);
							((TextView) findViewById(R.id.profileTextView2)).setText(String.format("%s:%s",isUser?"个性签名":"描述",u.sign));
							((TextView) findViewById(R.id.profileTextView3)).setText(String.format("FAQ:%d",u.faq));
						}
						else
						{
							Group u=(Group) Group.s2o(msg);
							if(u.faq!=faq)return;
							nick.setText(u.nick);
							((TextView) findViewById(R.id.profileTextView2)).setText(String.format("%s:%s",isUser?"个性签名":"描述",u.sign));
							((TextView) findViewById(R.id.profileTextView3)).setText(String.format("FAQ:%d",u.faq));
						}

					}
				});
		else if(cmd==C.GHG||cmd==C.GHU)
		{
			if("-1".equals(msg))return;
			byte[] b=Base64.decode(msg,0);
			int f=Data.getInt(new byte[]{b[0],b[1],b[2],b[3]});
			if(f!=faq)return;
			final Bitmap bm=BitmapFactory.decodeByteArray(b,4,b.length-4);
			runOnUiThread(new Runnable(){
					@Override
					public void run()
					{
						((ImageView)findViewById(R.id.profileImageView1)).setImageDrawable(new myRoundDrawable(bm));
					}});
		}
	}


}
