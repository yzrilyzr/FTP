import java.io.*;

import com.yzrilyzr.FAQ.Main.C;
import com.yzrilyzr.FAQ.Main.FileService;
import java.net.Socket;
import java.nio.channels.FileChannel;

public class uploadToServer
{
	public static void main(String[] args)
	{
		String ip="192.168.0.3";
		System.out.println("Hello World!");
		try
		{
			FileChannel in=new FileInputStream("/storage/emulated/0/AppProjects/FAQ/Server/bin/release/dex/classes.dex").getChannel();
			FileChannel out=new FileOutputStream("/storage/emulated/0/yzr的app/FAQ_server/server.dat").getChannel();
			in.transferTo(0,in.size(),out);
			System.out.println("已复制到本地");
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		try
		{
			Socket so2=new Socket(ip,20000);
			BufferedOutputStream os=new BufferedOutputStream(so2.getOutputStream());
			os.write(C.LGN);
			os.write(new byte[4]);
			os.flush();
			os.write(C.EXE);
			writeStr(os,"unlock");
			os.flush();
			Socket so=new Socket(ip,20001);
			BufferedOutputStream b=new BufferedOutputStream(so.getOutputStream());
			BufferedInputStream in=new BufferedInputStream(so.getInputStream());
			b.write(1);
			b.flush();
			File f=new File("/storage/emulated/0/AppProjects/FAQ/Server/bin/release/dex/classes.dex");
			BufferedInputStream is=new BufferedInputStream(new FileInputStream(f));
			writeInt(b,(int)f.length());
			writeStr(b,FileService.byte2hex(FileService.getFileSha1(f.getAbsolutePath())));
			writeStr(b,"/yzr的app/FAQ_server/server.dat");
			b.flush();
			int res=in.read();
			if(res==1)
			{
				System.out.println("秒传");
			}
			if(res==2)
			{
				System.out.println("准备传输");
				byte[] by=new byte[10240];
				int i=0;
				while((i=is.read(by))!=-1)
				{
					b.write(by,0,i);
				}
				b.flush();
				int result=in.read();
				if(result==1)
					System.out.println("上传完毕");
				else throw new Exception("传输出错");
			}
			else throw new Exception("拒绝传输");
			os.write(C.EXE);
			writeStr(os,"reload");
			os.flush();
			so2.close();
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
