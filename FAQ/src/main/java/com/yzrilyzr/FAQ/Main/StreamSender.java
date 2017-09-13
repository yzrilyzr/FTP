package com.yzrilyzr.FAQ.Main;

import android.util.Base64;
import com.yzrilyzr.FAQ.Data.MessageObj;
import com.yzrilyzr.FAQ.Data.ToStrObj;
import com.yzrilyzr.myclass.util;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

public class StreamSender implements ClientService.Listener,Runnable
{
	private RandomAccessFile is;
	private MessageObj mobj;
	private String sha1,filepath,name;
	private int channel=-1,outtime=0;
	public StreamSender(byte[] b,MessageObj obj)
	{
		try
		{
			filepath=util.mainDir+"/tmpfile";
			BufferedOutputStream os=new BufferedOutputStream(new FileOutputStream(filepath));
			os.write(b);
			os.flush();
			os.close();
			is=new RandomAccessFile(filepath,"r");
			mobj=obj;
			sha1=Data.getByteSha1(b);
			ClientService.addListener(this);
		}
		catch (Exception e)
		{}
	}
	public StreamSender(String file,MessageObj obj)
	{
		try
		{
			filepath=file;
			is=new RandomAccessFile(file,"r");
			mobj=obj;
			sha1=Data.getFileSha1(file);
			ClientService.addListener(this);
		}
		catch (FileNotFoundException e)
		{}
	}
	public StreamSender send()
	{
		new Thread(this).start();
		return this;
	}

	@Override
	public void run()
	{
		// TODO: Implement this method
		try
		{
			name=new File(filepath).getName();
			mobj.msg=String.format("LEN%d/%s/%s",is.length(),name,sha1);
			ClientService.sendMsg(C.MSG,mobj.o2s());
			while(channel==-1){
				outtime++;
				Thread.sleep(1);
				if(outtime>5000)throw new Exception("请求超时");
			}
			//ClientService.sendMsg(C.LOG,"CHANNEL"+channel);
			byte[] buff=new byte[4096];int i=0;long index=0;
			while((i=is.read(buff))!=-1){
				mobj.msg=String.format("%s●%d●%s",name,index,Base64.encodeToString(buff,0,i,0));
				ClientService.sendMsg(C.MSG,mobj.o2s());
				index+=i;
			}
			is.close();
		}
		catch (Exception e)
		{
			ClientService.sendMsg(C.LOG,util.getStackTrace(e));
		}
		finally{
			ClientService.sendMsg(C.LOG,"FILE OK");
			ClientService.removeListener(this);
		}
	}


	@Override
	public void rev(byte cmd, String msg)
	{
		// TODO: Implement this method
		if(cmd==C.MSG){
			MessageObj m=(MessageObj) ToStrObj.s2o(msg);
			if(m.type==T.FLE&&m.msg.contains(name)){
				channel=Integer.parseInt(m.msg.substring(name.length()));
			}
		}
	}


}
