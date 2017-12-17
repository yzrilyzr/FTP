package com.yzrilyzr.FAQ.Main;
import com.yzrilyzr.FAQ.Server.Server;
import java.io.BufferedInputStream;
import java.net.Socket;
import java.io.IOException;
import java.io.ByteArrayInputStream;

public class RemoteControlService extends BaseService
{
	public RemoteControlService(Socket s,Server m)
	{
		super(s,m);
		TAG="RemoteControlClient";
		setName("FAQServer_RemoteControlService");
	}
	@Override
	public void onDestroy()
	{
		// TODO: Implement this method
	}
	@Override
	public void onRead(BufferedInputStream buff)
	{
		try
		{
			int r=buff.read();
			if(r==1){
				byte[] bb=ctx.onGetScreen();
				if(bb==null){
					writeInt(Writer,0);
					Writer.flush();
				}
				else{
					writeInt(Writer,bb.length);
					ByteArrayInputStream is=new ByteArrayInputStream(bb);
					byte[] bu=new byte[2048];int k=0;
					while((k=is.read(bu))!=-1)Writer.write(bu,0,k);
					Writer.flush();
				}
			}
		}
		catch (IOException e)
		{
			Toast("Error","远程控制请求读取失败");
			running=false;
		}
	}
	
}
