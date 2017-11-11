package com.yzrilyzr.FAQ.Data;

public class User extends ToStrObj
{
	public int faq;
	public String pwd,nick,sign,ip,email;
	public ToStrList<Integer> friends=new ToStrList<Integer>()
	,groups=new ToStrList<Integer>();
}
