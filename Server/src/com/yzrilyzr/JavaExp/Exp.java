package com.yzrilyzr.JavaExp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
public class Exp
{
	ConcurrentHashMap<String,ConcurrentHashMap<String,Object>> gvar;
	//组名,变量名
	private String nowGroup;
	public Exp()
	{
		gvar=new ConcurrentHashMap<String,ConcurrentHashMap<String,Object>>();
	}
	public void newGroup(String name)
	{
		gvar.put(name,new ConcurrentHashMap<String,Object>());
	}
	public void setNowGroup(String name)
	{
		nowGroup=name;
	}
	public String getNowGroup(){
		return nowGroup;
	}
	public void parse(String code) throws Exception
	{
		parse(nowGroup,code);
	}
	public void setValue(String n,Object v)
	{
		gvar.get(nowGroup).put(n,v);
	}
	public void setValue(String g,String n,Object v)
	{
		gvar.get(g).put(n,v);
	}
	public void parse(String group,String code) throws Exception
	{
		String[] statement=code.split(";");
		for(String s:statement)
		{
			Object rv=null;
			String ls=null,rs=null;
			if(s.contains("="))
			{
				ls=s.substring(0,s.indexOf("="));
				rs=s.substring(s.indexOf("=")+1);
			}
			else rs=s;
			rv=getRightValue(rs);
			if(ls!=null)gvar.get(nowGroup).put(ls,rv);
		}
	}

	private Object getRightValue(String s)throws Exception
	{
		ArrayList<Integer> l=new ArrayList<Integer>();
		int k=-1;
		while((k=s.indexOf(">>",++k))!=-1)l.add(k);
		k=-1;
		while((k=s.indexOf("->",++k))!=-1)l.add(k);
		k=-1;
		while((k=s.indexOf("<<",++k))!=-1)l.add(k);
		Collections.sort(l,new Comparator<Integer>(){
			@Override
			public int compare(Integer p1, Integer p2)
			{
				return p1>p2?1:0;
			}
		});
		if(l.size()==0)l.add(0);
		Object o=null;
		for(int i=0;i<l.size();i++)
		{
			String p=null;
			if(i+1<l.size())p=s.substring(l.get(i),l.get(i+1));
			else p=s.substring(l.get(i),s.length());
			if(p==null||"".equals(p)||"null".equals(p))return null;
			else if(p.startsWith(">>"))o=Class.forName(p.substring(2));
			else if(p.startsWith("->")&&!p.contains("("))
			{
				Class cls=null;
				if(o instanceof Class)cls=(Class)o;
				else cls=o.getClass();
				o=cls.getField(p.substring(2)).get(o);
			}
			else if(p.startsWith("<<"))o=newInst(p.substring(2));
			else if(p.startsWith("->"))o=invokeMethod(o,p.substring(2));
			else if(Pattern.matches("[0-9]*",p))o=Integer.parseInt(p);
			else if(Pattern.matches("[0-9]*\\.[0-9]*",p))o=Float.parseFloat(p);
			else if(Pattern.matches("\".*\"",s))o=p.substring(1,p.length()-1);
			else o=gvar.get(nowGroup).get(p);
		}
		return o;
	}
	private Object newInst(String par)throws Exception
	{
		String mn=par.substring(0,par.indexOf("("));
		String[] par2=par.substring(par.indexOf("(")+1,par.indexOf(")")).split(",");
		if(par2[0].equals(""))
		{
			return Class.forName(mn).getConstructor().newInstance();
		}
		else
		{
			Object[] obj=new Object[par2.length];
			Class[] clss=new Class[par2.length];
			for(int i=0;i<par2.length;i++)
			{
				obj[i]=getRightValue(par2[i]);
				clss[i]=getClass(obj[i]);
			}
			return Class.forName(mn).getConstructor(clss).newInstance(obj);
		}
	}
	private Object invokeMethod(Object o,String par)throws Exception
	{
		String mn=par.substring(0,par.indexOf("("));
		String[] par2=par.substring(par.indexOf("(")+1,par.indexOf(")")).split(",");
		if(par2[0].equals(""))
		{
			Class cls=null;
			if(o instanceof Class)cls=(Class)o;
			else cls=o.getClass();
			return cls.getMethod(mn).invoke(o);
		}
		else
		{
			Object[] obj=new Object[par2.length];
			Class[] clss=new Class[par2.length];
			for(int i=0;i<par2.length;i++)
			{
				obj[i]=getRightValue(par2[i]);
				clss[i]=getClass(obj[i]);
			}
			Class cls=null;
			if(o instanceof Class)cls=(Class)o;
			else cls=o.getClass();
			return cls.getMethod(mn,clss).invoke(o,obj);
		}
	}
	private Class<?> getClass(Object s)
	{
		if(s==null)return null;
		else if(Pattern.matches("[0-9]*",s+""))return int.class;
		else if(Pattern.matches("[0-9]*\\.[0-9]*",s+""))return float.class;
		//else if(Pattern.matches("\".*\"",s+""))return String.class;
		else return s.getClass();
	}
}
