package com.ra4king.gameservers.jwords.server.games;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.ra4king.gameservers.jwords.server.JWordsServer;

public class Games {
	private ArrayList<Game> games = new ArrayList<Game>();
	
	public Games() throws Exception {
		try{
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(JWordsServer.codeBase + "games.dat"));
			while(in.available() > 0)
				games.add((Game)in.readObject());
			in.close();
		}
		catch(Exception exc) {}
	}
	
	public synchronized int addGame(Game game) throws Exception {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(JWordsServer.codeBase + "games.dat"));
		out.writeObject(game);
		out.close();
		
		games.add(game);
		
		return game.getID();
	}
	
	public synchronized int addGame(int pid1, int pid2) throws Exception {
		return addGame(new Game(pid1,pid2,games.size()));
	}
	
	public synchronized Game getGame(int id) {
		if(id < 0 || id >= games.size())
			return null;
		
		return games.get(id);
	}
}