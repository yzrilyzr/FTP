
import com.yzrilyzr.FAQ.Main.AES;
import com.yzrilyzr.FAQ.Main.BO;
import com.yzrilyzr.FAQ.Main.BaseService;
import com.yzrilyzr.FAQ.Server.Server;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import com.yzrilyzr.FAQ.Main.C;
import java.io.BufferedOutputStream;
import com.yzrilyzr.FAQ.Main.RU;
import java.util.Scanner;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
public class remoteControl extends RU
{
	Socket socket;
	boolean running=false;
	BufferedOutputStream Writer;
	private boolean isActive;
	private String deckey=null;
	public static void main(String[] args)
	{
		try
		{
			Socket so=new Socket();
			so.setTcpNoDelay(true);
			so.setKeepAlive(true);
			so.setTrafficClass(0x04|0x10);
			so.setSoTimeout(10000);
			so.connect(new InetSocketAddress("192.168.0.3",20000));
			remoteControl r=new remoteControl(so);
			r.start();
			r.sendMsg(C.LGN);
			r.play();
			Scanner sc=new Scanner(System.in);
			while(true)r.sendMsg(C.EXE,sc.next());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public remoteControl(Socket s) throws IOException
	{
		super();
		socket=s;
		running=true;
		Writer=new BufferedOutputStream(s.getOutputStream());
	}
	@Override
	public void run()
	{
		// TODO: Implement this method
		super.run();
		try
		{
			System.out.println("已连接");
			BufferedInputStream buff=new BufferedInputStream(socket.getInputStream());
			while(running)
			{
				onRead(buff);
			}
			Writer.close();
			buff.close();
			socket.close();
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		finally
		{
			running=false;
			isActive=false;
			System.out.println("断开连接");
		}
	}
	public void onRead(BufferedInputStream buff)
	{
		String str=null;
		try
		{
			BO bo=new BO(){
				@Override
				public boolean g()
				{
					return running;
				}
			};
			block(buff,1,bo);
			if(running)
			{
				byte cmd=(byte)buff.read();
				int pwdlen=readIntFully(buff,bo);
				ByteArrayOutputStream str2=new ByteArrayOutputStream();
				int ind=0;
				while(ind<pwdlen)
				{
					byte[] bb=new byte[pwdlen-ind];
					int ii=buff.read(bb);
					ind+=ii;
					str2.write(bb,0,ii);
				}
				str2.close();
				str=str2.toString();
				str2=null;
				if(deckey!=null&&str!=null&&!"".equals(str))str=AES.decrypt(deckey,str);
				if(cmd==C.HBT)sendMsg(C.HBT);
				else if(cmd==C.LOG)
				{
					System.out.println(str);
				}
				else System.out.println("指令:"+cmd+",接收:"+str);
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
	}
	public void play(){
		sendMsg(C.EXE,"exec i");
		sendMsg(C.EXE,"exec n mediap");
		sendMsg(C.EXE,"exec s mediap");
		sendMsg(C.EXE,"exec g");
		sendMsg(C.EXE,"exec media=<<android.media.MediaPlayer()");
		sendMsg(C.EXE,"exec media->setDataResourse()");
		sendMsg(C.EXE,"exec media->prepare()");
		sendMsg(C.EXE,"exec media->play()");
	}
	public void sendMsg(byte cmd)
	{
		sendMsg(cmd,null);
	}
	public void sendMsg(byte CMD,String ss)
	{
		String s=ss;
		try
		{
			if(s!=null)
			{
				if(deckey!=null)s=AES.encrypt(deckey,s);
				Writer.write(CMD);
				writeStr(Writer,s);
			}
			else
			{
				Writer.write(CMD);
				writeStr(Writer,"");
			}
			Writer.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

