package com.yzrilyzr.FAQ.Data;
import android.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ToStrObj implements Serializable
{
	public static ToStrObj s2o(String b64){
		try
		{
			byte[] bb=Base64.decode(b64,0);
			ByteArrayInputStream b=new ByteArrayInputStream(bb);
			ObjectInputStream ois=new ObjectInputStream(b);
			return (ToStrObj)ois.readObject();
		}
		catch (Exception e)
		{
			return null;
		}
	}
	public String o2s(){
		try
		{
			ByteArrayOutputStream b=new ByteArrayOutputStream();
			ObjectOutputStream oos=new ObjectOutputStream(b);
			oos.writeObject(this);
			oos.flush();
			oos.close();
			b.flush();
			b.close();
			byte[] a=b.toByteArray();
			return Base64.encodeToString(a,0);
		}
		catch (IOException e)
		{
			return "";
		}
	}
}
