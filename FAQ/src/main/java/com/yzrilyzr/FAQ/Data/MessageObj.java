package com.yzrilyzr.FAQ.Data;

import java.io.UnsupportedEncodingException;
import android.util.Base64;

public class MessageObj extends ToStrObj
{
	public int from,to;
	public byte type;
	public String msg;
	public long time;
	public boolean read=false,isGroup=false;
	public MessageObj(int fro,int t,byte ty,boolean isg,String m){
		from=fro;to=t;
		type=ty;
		isGroup=isg;
		msg=m;
	}
	public MessageObj setTime(){
		time=System.currentTimeMillis();
		return this;
	}
}
