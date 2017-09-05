package com.yzrilyzr.FAQ.Data;
import android.util.Base64;
import com.yzrilyzr.FAQ.Main.ClientService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class User extends ToStrObj
{
	public int faq;
	public String pwd,nick,sign,ip,email;
	public ToStrList<Integer> friends=new ToStrList<Integer>()
	,groups=new ToStrList<Integer>();
}
