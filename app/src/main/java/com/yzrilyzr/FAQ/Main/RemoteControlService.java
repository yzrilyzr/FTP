package com.yzrilyzr.FAQ.Main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class RemoteControlService extends RU
{
	public Socket socket;
	public BufferedOutputStream Writer;
	public boolean running=false;
	private Listener listener;
	private DatagramSocket so;
	public void setListener(Listener listener)
	{
		this.listener = listener;
	}
	public Listener getListener()
	{
		return listener;
	}
	public interface Listener
	{
		public abstract void rev(byte[] data);
	}
	public void get()
	{
		try{
		DatagramPacket p=new DatagramPacket(new byte[]{1},1,new InetSocketAddress(ClientService.hostIp,20002));
		so.send(p);
		}catch(Throwable e){
			
		}
	}
	public void startService()
	{
		try
		{
			so=new DatagramSocket();
		}
		catch (SocketException e)
		{}
		new Thread(new Runnable(){
				@Override
				public void run()
				{
					long time=0;
					try
					{
						while(true)
						{
							byte[] bb=new byte[1024*64];
							DatagramPacket pa=new DatagramPacket(bb,bb.length);
							so.receive(pa);
							try
							{
								//ByteArrayInputStream is=new ByteArrayInputStream(pa.getData());
								//int t=is.read();
								//if(t>time)
								{
									//time=t;
									//bb=new byte[is.available()];
									//is.read(bb);
									//is.close();
									listener.rev(pa.getData());
									bb=null;
								}
							}
							catch(Throwable e)
							{}
						}
						}catch(Throwable e)
						{
						}
					}
				}).start();
			}
	}
