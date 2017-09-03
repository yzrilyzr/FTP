package com.yzrilyzr.FAQ.Data;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import android.util.Base64;
import android.graphics.Bitmap;

public class User extends ToStrObj
{
	public int faq;
	public String pwd,nick,sign,ip,email;
	public int[] friends=new int[0],groups=new int[0];
}
