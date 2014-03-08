package com.gcong.io.packet;

import java.io.Serializable;

public interface Packet extends Serializable {
	public int getopcode();
	public void put(String key, Object value);
	
	public int getInt(String key);
	public String getString(String key);
	public Object getObject(String key);
	public Long getLong(String key);
	
	public boolean containsKey(String key);
	public String toString();
	
	
}
