package com.yzrilyzr.FAQ.Data;
import java.io.*;

import java.util.concurrent.CopyOnWriteArrayList;

public class ToStrList<T extends Object> extends CopyOnWriteArrayList implements Serializable
{
	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException{
		clear();
		int i=s.readInt(),o=0;
		while(o++<i)add(s.readObject());
	}
	private void writeObject(ObjectOutputStream s) throws IOException{
		s.writeInt(size());
		for(T t:this)s.writeObject(t);
	}
	
}
