package com.yzrilyzr.FAQ.Main;
import java.io.File;

public class SafeFile extends File
{
	boolean publicmode;
	Data data;
	String file;
	public SafeFile(Data data,boolean publicmode,String file)
	{
		super(file);
		this.data=data;
		this.publicmode=publicmode;
		this.file=file;
		check();
	}

	private void check() throws SecurityException
	{
		if((!file.contains(data.rootFile)&&publicmode)||(!file.contains(data.datafile)&&!publicmode))
			throw new SecurityException("无法操作文件:"+file+"(无权限)");
	}

	@Override
	public boolean delete()
	{
		check();
		return super.delete();
	}

}
