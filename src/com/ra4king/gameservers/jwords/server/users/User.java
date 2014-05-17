package com.ra4king.gameservers.jwords.server.users;

import java.util.ArrayList;

import com.ra4king.gameutils.networking.Packet;

public class User implements Comparable<User> {
	private String nm;
	private String pw;
	private String em;
	private ArrayList<Integer> cg;
	private ArrayList<Integer> gh;
	private int id;
	
	public User(Packet p) {
		this.nm = p.readString();
		this.pw = p.readString();
		this.em = p.readString();
		
		cg = new ArrayList<Integer>();
		int size = p.readInt();
		while(size-- > 0)
			cg.add(p.readInt());
		
		gh = new ArrayList<Integer>();
		size = p.readInt();
		while(size-- > 0)
			gh.add(p.readInt());
		
		this.id = p.readInt();
	}
	
	public User(String nm, String pw, String em, int id) {
		this(nm,pw,em,new ArrayList<Integer>(),new ArrayList<Integer>(),id);
	}
	
	public User(String nm, String pw, String em, ArrayList<Integer> cg, ArrayList<Integer> gh, int id) {
		this.nm = nm;
		this.pw = pw;
		this.em = em;
		this.cg = cg;
		this.gh = gh;
		this.id = id;
	}
	
	public String getUserName() {
		return nm;
	}
	
	public String getPassword() {
		return pw;
	}
	
	public String getEmail() {
		return em;
	}
	
	public ArrayList<Integer> getCurrentGames() {
		return cg;
	}
	
	public ArrayList<Integer> getAllGames() {
		return gh;
	}
	
	public int getID() {
		return id;
	}
	
	public boolean equals(Object o) {
		if(!(o instanceof User))
			return false;
		
		return id == ((User)o).id;
	}
	
	public int compareTo(User o) {
		return id-o.id;
	}
	
	public Packet toPacket(Packet packet) {
		packet.writeString(nm);
		packet.writeString(pw);
		packet.writeString(em);
		packet.writeInt(cg.size());
		for(int i : cg)
			packet.writeInt(i);
		packet.writeInt(gh.size());
		for(int i : gh)
			packet.writeInt(i);
		packet.writeInt(id);
		return packet;
	}
	
	public String toString() {
		return "Users[name="+nm+",email="+em+",id="+id+"]";
	}
}