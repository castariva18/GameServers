package com.ra4king.gameservers.jdoodlejump.multiplayer;

import java.util.ArrayList;

public class Lobby {
	private ArrayList<ClientConnection> waiting;
	
	public Lobby() {
		waiting = new ArrayList<ClientConnection>();
	}
	
	public synchronized void add(ClientConnection c) {
		waiting.add(c);
	}
	
	public synchronized ClientConnection getNext(ClientConnection c) {
		if(waiting.size() > 0)
			return waiting.remove(0);
		
		if(c != null)
			add(c);
		
		return null;
	}
	
	public synchronized boolean remove(ClientConnection c) {
		return waiting.remove(c);
	}
	
	public synchronized boolean contains(ClientConnection c) {
		return waiting.contains(c);
	}
}
