import java.util.*;
import java.net.Socket;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.OutputStream;
import com.yzrilyzr.FAQ.Main.C;

public class uploadToServer
{
	public static void main(String[] args)
	{
		String ip="192.168.0.3";
		System.out.println("Hello World!");
		try
		{
			Socket so=new Socket(ip,20001);
			BufferedOutputStream b=new BufferedOutputStream(so.getOutputStream());
			b.write(1);
			File f=new File("/storage/emulated/0/AppProjects/FAQ/Server/bin/release/dex/classes.dex");
			BufferedInputStream is=new BufferedInputStream(
			new FileInputStream(f));
			writeInt(b,(int)f.length());
			writeStr(b,"ha");
			writeStr(b,"/storage/emulated/0/yzr的app/FAQ_server/server.dat");
			b.flush();
			byte[] by=new byte[10240];
			int i=0;
			while((i=is.read(by))!=-1)
			{
				b.write(by,0,i);
				b.flush();
			}
			System.out.println("上传完毕");
			Thread.sleep(2000);
			Socket so2=new Socket(ip,20000);
			OutputStream os=so2.getOutputStream();
			Thread.sleep(10);
			os.write(C.REL);
			os.write(new byte[4]);
			os.flush();
			System.out.println("重载完毕");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public static void writeInt(BufferedOutputStream b,int i) throws IOException
	{
		byte[] bb=getIBytes(i);
		b.write(bb);
	}
	public static byte[] getIBytes(int data)  
	{  
		byte[] bytes = new byte[4];  
		bytes[0] = (byte) (data & 0xff);  
		bytes[1] = (byte) ((data & 0xff00) >> 8);  
		bytes[2] = (byte) ((data & 0xff0000) >> 16);  
		bytes[3] = (byte) ((data & 0xff000000) >> 24);  
		return bytes;  
	}  
	public static void writeStr(BufferedOutputStream b,String s) throws IOException
	{
		byte[] bb=s.getBytes();
		writeInt(b,bb.length);
		b.write(bb);
	}
}
