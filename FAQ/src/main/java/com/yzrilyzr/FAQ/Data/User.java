package com.yzrilyzr.FAQ.Data;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import android.util.Base64;
import android.graphics.Bitmap;
import java.util.ArrayList;

public class User extends ToStrObj
{
	public int faq;
	public String pwd,nick,sign,ip,email;
	public ArrayList<Integer> friends=new ArrayList<Integer>()
	,groups=new ArrayList<Integer>();
}