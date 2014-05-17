package com.ra4king.gameservers.jdoodlejump.multiplayer.game.util;

import java.util.ArrayList;

public class Bag<T> extends ArrayList<T> {
	private static final long serialVersionUID = 8096168672390044428L;
	
	public boolean add(T t) {
		checkIfNull(t);
		modCount--;
		return super.add(t);
	}
	
	public void add(int idx, T t) {
		checkIfNull(t);
		super.add(idx,t);
	}
	
	public T set(int idx, T t) {
		checkIfNull(t);
		return super.set(idx,t);
	}
	
	public T remove(int idx) {
		return super.set(idx,null);
	}
	
	public boolean remove(Object o) {
		if(o == null) {
			int idx = indexOf(o);
			if(idx == -1)
				return false;
			super.set(idx, get(size()-1));
			super.remove(size()-1);
			return remove(null);
		}
		
		int idx = indexOf(o);
		if(idx == -1)
			return false;
		
		remove(idx);
		
		return remove(o);
	}
	
	private void checkIfNull(Object o) {
		if(o == null)
			throw new NullPointerException("Object cannot be null.");
	}
}
