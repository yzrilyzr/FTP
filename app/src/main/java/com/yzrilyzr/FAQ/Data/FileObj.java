package com.yzrilyzr.FAQ.Data;
import com.yzrilyzr.FAQ.Main.Data;
import java.io.File;

public class FileObj extends ToStrObj
{
	public String name,path,separator,parent;
	public long length,lastMod;
	public boolean execute,read,write,isFile,isDir;
	public FileObj(String localpath,File file)
	{
		name=file.getName();
		path=file.getAbsolutePath().substring(localpath.length());
		length=file.length();
		lastMod=file.lastModified();
		execute=file.canExecute();
		read=file.canRead();
		write=file.canWrite();
		isFile=file.isFile();
		separator=file.separator;
		parent=file.getParent();
		isDir=file.isDirectory();
	}
	public FileObj(){}
}
