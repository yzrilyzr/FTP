package com.yzrilyzr.FAQ;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Base64;
import com.yzrilyzr.FAQ.Data.MessageObj;
import com.yzrilyzr.FAQ.Data.ToStrObj;
import com.yzrilyzr.FAQ.Data.User;
import com.yzrilyzr.FAQ.Main.ClientService;
import com.yzrilyzr.FAQ.Main.Data;
import com.yzrilyzr.myclass.util;
import com.yzrilyzr.FAQ.Main.T;
import com.yzrilyzr.FAQ.Main.C;
import android.os.Handler;

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
				MessageObj m=(MessageObj) ToStrObj.s2o(msg);
				if(m.type==T.VMS){
					Data.msglist.put("1000",m);
					Data.msgBuffer.add(m);
					Data.msgs.add(m);
				}
				else{
				String from=m.from+"";
				Data.msglist.put(from,m);
				Data.msgBuffer.add(m);
				Data.msgs.add(m);
				if(Data.users.get(from)==null){
					ClientService.sendMsg(C.GUS,from);
					byte[] hd=Data.getHead(from,false);
					if(hd==null)ClientService.sendMsg(C.GHU,from);
				}
				}
			}
			else if(cmd==C.GUS){
				if("-1".equals(msg))return;
				User u=User.s2o(msg);
				Data.users.put(u.faq+"",u);
			}
			else if(cmd==C.FLO){
				ClientService.isLogin=false;
				runOnUiThread(new Runnable(){
						@Override
						public void run()
						{
							// TODO: Implement this method
							util.toast(MsgService.this,"您的帐号在其他客户端登录，您已被强制下线");
							Intent in=new Intent(MsgService.this,LoginActivity.class);
							in.putExtra("al",false);
							in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							for(BaseActivity a:BaseActivity.activities)a.finish();
							System.gc();
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
		private void runOnUiThread(Runnable r){
			new Handler(getMainLooper()).post(r);
		}
}
