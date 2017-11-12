
import java.nio.channels.FileChannel;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;public class copyToLocal
{
	public static void main(String args[]) throws FileNotFoundException, IOException{
		FileChannel in=new FileInputStream("/storage/emulated/0/AppProjects/FAQ/Server/bin/release/dex/classes.dex").getChannel();
		FileChannel out=new FileOutputStream("/storage/emulated/0/yzr的app/FAQ_server/server.dat").getChannel();
		in.transferTo(0,in.size(),out);
		System.out.println("ok");
	}
}
