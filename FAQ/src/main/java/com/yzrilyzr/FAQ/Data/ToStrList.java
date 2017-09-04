package com.yzrilyzr.FAQ.Data;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ToStrList<T extends Object> extends CopyOnWriteArrayList
{
	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException{
		clear();
		int i=s.readInt(),o=0;
		while(o++<i)add(s.readObject());
	}
	private void writeObject(ObjectOutputStream s) throws IOException, ClassNotFoundException{
		s.writeInt(size());
		for(T t:this)s.writeObject(t);
	}
	
}
