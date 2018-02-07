import java.io.*;

import com.yzrilyzr.FAQ.Main.C;
import com.yzrilyzr.FAQ.Main.FileService;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.net.SocketAddress;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import com.yzrilyzr.FAQ.Server.Server;

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
			DatagramSocket so2=new DatagramSocket();
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
			else if(res==2)
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

			ByteArrayOutputStream os;
			os=new ByteArrayOutputStream();
			os.write(C.CON);
			os.flush();
			os.close();
			byte[] bb=os.toByteArray();
			so2.send(new DatagramPacket(bb,bb.length,InetAddress.getByName(ip),20000));
			System.out.println("连接成功");

			os=new ByteArrayOutputStream();
			os.write(C.LGN);
			os.flush();
			os.close();
			bb=os.toByteArray();
			so2.send(new DatagramPacket(bb,bb.length,InetAddress.getByName(ip),20000));
			System.out.println("登录成功");

			os=new ByteArrayOutputStream();
			os.write(C.EXE);
			os.write("reload".getBytes());
			os.flush();
			os.close();
			bb=os.toByteArray();
			so2.send(new DatagramPacket(bb,bb.length,InetAddress.getByName(ip),20000));
			System.out.println("重载完毕");

			URL u=new URL("https://yzrilyzr.wodemo.net/entry/458017");
			URLConnection c=u.openConnection();
			InputStream id=c.getInputStream();
			os=new ByteArrayOutputStream();
			bb=new byte[2048];
			int i=0;
			while((i=id.read(bb))!=-1)os.write(bb,0,i);
			id.close();
			String uip=os.toString();
			uip=uip.substring(uip.indexOf("THIS")+4,uip.indexOf("ENDL"));
			String strIP="";
			URL ur= new URL("http://m.tool.chinaz.com/ipsel"); 
			URLConnection conn = ur.openConnection(); 
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); 
			String line = null; 
			StringBuffer result = new StringBuffer(); 
			while((line = reader.readLine()) != null)result.append(line);
			reader.close();
			strIP=result.toString();
			strIP=strIP.substring(strIP.indexOf("您的IP地址")+30);
			strIP=strIP.substring(0,strIP.indexOf("</b>"));
			if(!strIP.equals(uip))
			{
				System.out.println("ip不匹配,请修改为:");
				System.out.println(strIP);
			}
			else
			{
				Thread.sleep(1000);
				System.exit(0);
			}
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
