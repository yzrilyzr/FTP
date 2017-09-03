package com.yzrilyzr.FAQ.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.ObjectInputStream;

public class ArrayObj<T extends Object> extends CopyOnWriteArrayList implements Serializable
{
	public void writeObject(ObjectOutputStream s)throws IOException{
		s.defaultWriteObject();
		s.writeInt(size());
		for(int i=0;i<size();i++)s.writeObject(get(i));
	}
	public void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException{
		s.defaultReadObject();
		int ii=s.readInt();
		clear();
		for(int i=0;i<ii;i++)add(s.readObject());
	}
}
