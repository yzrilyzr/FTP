package com.yzrilyzr.FAQ;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.util.Base64;
import com.yzrilyzr.FAQ.Data.MessageObj;
import com.yzrilyzr.FAQ.Data.ToStrObj;
import com.yzrilyzr.FAQ.Data.User;
import com.yzrilyzr.FAQ.Main.C;
import com.yzrilyzr.FAQ.Main.ClientService;
import com.yzrilyzr.FAQ.Main.Data;
import com.yzrilyzr.FAQ.Main.T;
import com.yzrilyzr.myclass.util;

public class MsgService extends Service implements ClientService.Listener
{

	@Override
	public void onCreate()
	{
		// TODO: Implement this method
		super.onCreate();
		ClientService.addListener(this);
	}

	@Override
	public void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();
		ClientService.removeListener(this);
	}

	@Override
	public IBinder onBind(Intent p1)
	{
		// TODO: Implement this method
		return null;
	}
	@Override
	public void rev(byte cmd, String msg)
	{
		// TODO: Implement this method
		if(cmd==C.MSG)
		{
			final MessageObj m=(MessageObj) ToStrObj.s2o(msg);
			if(m.type==T.VMS)
			{
				Data.msglist.put("1000",m);
				Data.msgBuffer.add(m);
				Data.msgs.add(m);
			}
			else
			{
				String from=m.from+"";
				Data.msglist.put(from,m);
				Data.msgBuffer.add(m);
				Data.msgs.add(m);
				if(Data.getUser(m.from)==null)
				{
					ClientService.sendMsg(C.GUS,from);
					byte[] hd=Data.getHead(m.from,false);
					if(hd==null)ClientService.sendMsg(C.GHU,from);
				}
			}
			runOnUiThread(new Runnable(){
					@Override
					public void run()
					{
						// TODO: Implement this method
						byte[] b=Data.getHead(m.from,m.isGroup);
						Bitmap bmp=null;
						if(b!=null)bmp=BitmapFactory.decodeByteArray(b,0,b.length);
						else bmp=BitmapFactory.decodeResource(getResources(),R.drawable.launcher);
						Intent intent=new Intent(MsgService.this,MainActivity.class);
						User us=Data.getUser(m.from);
						intent.putExtra("user",us.o2s());
						PendingIntent p=PendingIntent.getActivity(MsgService.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
						Notification n=new Notification.Builder(MsgService.this)
							.setSmallIcon(R.drawable.launcher)
							.setLargeIcon(bmp)
							.setTicker(String.format("<%s>:%s",us.nick,m.msg))
							.setContentText(m.msg)
							.setContentTitle(us.nick)
							.setContentIntent(p)
							.setDefaults(Notification.DEFAULT_ALL)
							.getNotification();
						n.flags=Notification.FLAG_AUTO_CANCEL;
						((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(0,n);
					}
				});
		}
		else if(cmd==C.GUS)
		{
			if("-1".equals(msg))return;
			User u=(User) ToStrObj.s2o(msg);
			Data.users.put(u.faq+"",u);
		}
		else if(cmd==C.FLO)
		{
			ClientService.isLogin=false;
			runOnUiThread(new Runnable(){
					@Override
					public void run()
					{
						// TODO: Implement this method
						util.toast(MsgService.this,"您的帐号在其他客户端登录，您已被强制下线");
						Intent in=new Intent(MsgService.this,LoginActivity.class);
						in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						for(BaseActivity a:BaseActivity.activities)a.finish();
						System.gc();
						ClientService.isLogin=false;
						startActivity(in);
					}
				});

		}
		else if(cmd==C.GHG||cmd==C.GHU)
		{
			if("-1".equals(msg))return;
			byte[] by=Base64.decode(msg,0);
			Data.saveHead(cmd==C.GHG,by);
		}
	}
	private void runOnUiThread(Runnable r)
	{
		new Handler(getMainLooper()).post(r);
	}
}
