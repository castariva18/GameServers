package com.ra4king.gameservers.jdoodlejump.multiplayer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Users {
	private HashMap<String,User> users;
	
	public enum LoginState {
		LOGGED_IN, INCORRECT_PASS, UNKNOWN_USER
	}
	
	public Users(String file) throws Exception {
		users = new HashMap<String,User>();
		
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"))) {
			String s;
			while((s = reader.readLine()) != null) {
				String[] line = s.split("<:>");
				users.put(line[0], new User(line[0],line[1],Long.parseLong(line[2])));
			}
		}
	}
	
	public synchronized boolean register(String name, String pass) {
		if(get(name) != null)
			return false;
		users.put(name, new User(name,pass,System.currentTimeMillis()));
		return true;
	}
	
	public synchronized LoginState login(String name, String pass) {
		User u = users.get(name);
		if(u != null) {
			if(u.pass.equals(pass)) {
				u.lastLoggedIn = System.currentTimeMillis();
				return LoginState.LOGGED_IN;
			}
			return LoginState.INCORRECT_PASS;
		}
		return LoginState.UNKNOWN_USER;
	}
	
	public synchronized User get(String name) {
		return users.get(name);
	}
	
	public synchronized LoginState changePassword(String name, String oldPass, String newPass) {
		User u = users.get(name);
		if(u != null) {
			if(u.pass.equals(oldPass)) {
				u.pass = newPass;
				return LoginState.LOGGED_IN;
			}
			return LoginState.INCORRECT_PASS;
		}
		return LoginState.UNKNOWN_USER;
	}
	
	public static class User {
		public final String name;
		public String pass;
		public long lastLoggedIn;
		
		public User(String name, String pass, long lastLoggedIn) {
			this.name = name;
			this.pass = pass;
			this.lastLoggedIn = lastLoggedIn;
		}
	}
}
