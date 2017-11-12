package com.yzrilyzr.FAQ.Main;
import java.io.File;

public class SafeFile extends File
{
	boolean publicmode;
	Data data;
	public SafeFile(Data data,boolean publicmode,String file){
		super(file);
		if((!file.contains(data.rootFile)&&!publicmode)||!file.contains(data.datafile))
			throw new SecurityException("无法操作文件:无权限");
			this.data=data;
			this.publicmode=publicmode;
	}

	@Override
	public boolean delete()
	{
		if((!getAbsolutePath().contains(data.rootFile)&&!publicmode)||!getAbsolutePath().contains(data.datafile))
			throw new SecurityException("无法操作文件:无权限");
		return super.delete();
	}
	
}
