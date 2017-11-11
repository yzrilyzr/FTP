package com.yzrilyzr.FAQ.Data;
import android.util.Base64;
import com.yzrilyzr.FAQ.Main.ClientService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class User extends ToStrObj
{
	public int faq;
	public String pwd,nick,sign,ip,email;
	public ToStrList<Integer> friends=new ToStrList<Integer>()
	,groups=new ToStrList<Integer>();
/*
	@Override
	public String o2s()
	{
		// TODO: Implement this method
		try
		{
			ByteArrayOutputStream b=new ByteArrayOutputStream();
			ObjectOutputStream oos=new ObjectOutputStream(b);
			oos.writeObject(this);
			oos.writeInt(friends.size());
			for(int g:friends)oos.writeInt(g);
			oos.writeInt(groups.size());
			for(int g:groups)oos.writeInt(g);
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
	public static User s2o(String b64){
		try
		{
			byte[] bb=Base64.decode(b64,0);
			ByteArrayInputStream b=new ByteArrayInputStream(bb);
			ObjectInputStream ois=new ObjectInputStream(b);
			User u=(User)ois.readObject();
			u.friends=new ArrayList<Integer>();
			u.groups=new ArrayList<Integer>();
			int a=ois.readInt();
			for(int i=0;i<a;i++)u.friends.add(ois.readInt());
			int vb=ois.readInt();
			for(int i=0;i<vb;i++)u.groups.add(ois.readInt());
			return u;
		}
		catch (Exception e)
		{
			return null;
		}
	}*/
}
