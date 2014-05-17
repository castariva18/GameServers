package com.ra4king.gameservers.jwords.server.users;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.ra4king.gameservers.jwords.server.JWordsServer;

public class Users {
	private ArrayList<User> users = new ArrayList<User>();
	
	public Users() {
		ArrayList<User> u = new ArrayList<User>();
		
		try(ObjectInputStream in = new ObjectInputStream(new FileInputStream(JWordsServer.codeBase + "users.dat"))) {
			while(in.available() > 0)
				u.add((User)in.readObject());
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
		
		for(User user : u)
			users.add(user);
		
		Collections.sort(users);
	}
	
	/*
	private ArrayList<Integer> splitIDs(String input) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		
		if(input == null || input.equals(""))
			return ids;
		
		String sids[] = input.split(",");
		for(String s : sids)
			ids.add(Integer.parseInt(s));
		
		return ids;
	}
	*/
	
	public synchronized void addUser(String username, String pwrd, String email) throws Exception {
		User user = new User(username,pwrd,email,users.size());
		
		int idx = Collections.binarySearch(users,new User(username,"","",0),new Comparator<User>() {
			public int compare(User o1, User o2) {
				return o1.getUserName().compareTo(o2.getUserName());
			}
		});
		
		if(idx > 0)
			throw new IllegalArgumentException("Username already in use.");
		
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(JWordsServer.codeBase + "users.dat",true));
		out.writeObject(user);
		out.flush();
		out.close();
		
		users.add(user);
	}
	
	public synchronized User getUser(int id) {
		int idx = Collections.binarySearch(users,new User("","","",id));
		
		if(idx < 0) return null;
		
		return users.get(idx);
	}
	
	public synchronized User getUser(String username, String pwrd) {
		if((username == null || username.equals("")) || (pwrd == null || pwrd.equals("")))
			return null;
		
		int idx = Collections.binarySearch(users,new User(username,"","",0),new Comparator<User>() {
			public int compare(User o1, User o2) {
				return o1.getUserName().compareTo(o2.getUserName());
			}
		});
		
		if(idx < 0) return null;
		
		if(users.get(idx).getPassword().equals(pwrd))
			return users.get(idx);
		else
			return null;
	}
}