package com.yzrilyzr.FAQ.Data;
import java.util.*;

public class User extends ToStrObj
{
	public int faq;
	public String pwd,nick,sign,ip,email;
	public ArrayList<Integer> friends=new ArrayList<Integer>()
	,groups=new ArrayList<Integer>();
}
