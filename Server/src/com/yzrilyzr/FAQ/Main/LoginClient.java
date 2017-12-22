package com.yzrilyzr.FAQ.Main;
import com.yzrilyzr.FAQ.Data.User;
import java.net.SocketAddress;

public class LoginClient
{
	public String deckey,verifycode;
	public User user;
	public SocketAddress address;
	public LoginClient(String deckey, User user, SocketAddress address)
	{
		this.deckey = deckey;
		this.user = user;
		this.address = address;
	}
}
