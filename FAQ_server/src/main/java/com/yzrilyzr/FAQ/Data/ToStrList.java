package com.yzrilyzr.FAQ.Data;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
public class ToStrList<TT extends Object> extends CopyOnWriteArrayList implements Serializable
{
	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException{
		clear();
		int i=s.readInt(),o=0;
		while(o++<i)add(s.readObject());
	}
	private void writeObject(ObjectOutputStream s) throws IOException{
		s.writeInt(size());
		for(TT t:this)s.writeObject(t);
	}
	
}
