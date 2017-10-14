package com.yzrilyzr.FAQ.Server;
import com.yzrilyzr.FAQ.Data.ToStrObj;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleMsg extends ToStrObj
{
	public String tag,at,msg,ip;
	public long time;
	public ConsoleMsg(String t,String a,String m,String i){
		tag=t;
		at=a;
		msg=m;
		ip=i;
		time=System.currentTimeMillis();
	}

	@Override
	public String toString()
	{
		// TODO: Implement this method
		return String.format("[%s]<%s(%s)@%s>%s",new SimpleDateFormat("yy/MM/dd hh:mm:ss").format(new Date(time)),tag,at,ip,msg);
	}
	
}
