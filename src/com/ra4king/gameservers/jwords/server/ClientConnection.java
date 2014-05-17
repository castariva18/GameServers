package com.ra4king.gameservers.jwords.server;

import com.ra4king.gameservers.jwords.server.games.Games;
import com.ra4king.gameservers.jwords.server.users.User;
import com.ra4king.gameservers.jwords.server.users.Users;
import com.ra4king.gameservers.jwords.server.words.Words;
import com.ra4king.gameutils.networking.Packet;
import com.ra4king.gameutils.networking.PacketIO;

public class ClientConnection implements Runnable {
	private static Games games;
	private static Users users;
	private static Words words;
	private PacketIO io;
	private boolean loggedIn;
	
	public static void init() {}
	
	static {
		users = new Users();
		
		try {
			words = new Words();
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
		
		try {
			games = new Games();
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public ClientConnection(PacketIO io) {
		this.io = io;
	}
	
	public void run() {
		try{
			User user = null;
			
			Packet packet = io.read();
			
			int id = packet.readInt();
			
			if(id == 0) {
				while(!loggedIn) {
					String name = packet.readString();
					String pwd = packet.readString();
					
					System.out.print("[JWordsServer] Logging in with username: " + name + " ... ");
					
					if((user = users.getUser(name,pwd)) != null) {
						loggedIn = true;
						System.out.println("OK");
						Packet p = new Packet();
						p.writeInt(0);
						io.write(p);
					}
					else {
						System.out.println("DENIED");
						Packet p = new Packet();
						p.writeInt(1);
						io.write(p);
					}
				}
			}
			else if(id == 1) {
				String name = packet.readString();
				String pwd = packet.readString();
				String email = packet.readString();
				
				System.out.print("[JWordsServer] Registering with name: " + name + " ... ");
				
				try{
					users.addUser(name,pwd,email);
					System.out.println("OK");
					Packet p = new Packet();
					p.writeInt(0);
					io.write(p);
				}
				catch(Exception exc) {
					System.out.println("name taken.");
					Packet p = new Packet();
					p.writeInt(1);
					io.write(p);
				}
			}
			else {
				System.out.println("Invalid command");
				Packet p = new Packet();
				p.writeString("Invalid command");
				io.write(p);
			}
			
			while(loggedIn) {
				packet = io.read();
				
				id = packet.readInt();
				
				switch(id) {
					case 0: {
						String word = packet.readString();
						boolean verify = words.verify(word);
						System.out.println("[JWordsServer] Verified " + word + ": " + verify);
						Packet p = new Packet();
						packet.writeInt(0);
						packet.writeBoolean(verify);
						io.write(p);
						break;
					}
					case 1: {
						int id2 = packet.readInt();
						switch(id2) {
							case 0: {
								int userID = packet.readInt();
								System.out.println("[JWordsServer] Requesting user id " + userID);
								Packet p = new Packet();
								packet.writeInt(0);
								io.write(users.getUser(userID).toPacket(p));
								break;
							}
							case 1: {
								//CHANGE PASSWORD
								break;
							}
							case 2: {
								//CHANGE EMAIL
								break;
							}
						}
					}
					case 2: {
						int id2 = packet.readInt();
						switch(id2) {
							case 0: {
								int gameID = packet.readInt();
								System.out.println("[JWordsServer] Requesting game id " + gameID);
								Packet p = new Packet();
								p.writeInt(0);
								io.write(games.getGame(gameID).toPacket(p));
								break;
							}
							case 1: {
								//START RANDOM GAME
								break;
							}
							case 2: {
								//START GAME WITH SPECIFIED USERNAME
								break;
							}
							case 3: {
								String word = packet.readString();
								System.out.println("[JWordsServer] Playing word: " + word);
								int gameID = packet.readInt();
								int x = packet.readInt();
								int y = packet.readInt();
								boolean isHoriz = packet.readBoolean();
								Packet p = new Packet();
								p.writeInt(games.getGame(gameID).play(word,x,y,isHoriz,user.getID()));
								io.write(p);
								break;
							}
							case 4: {
								System.out.println("[JWordsServer] " + user.getID() + " has won!");
								int gameID = packet.readInt();
								games.getGame(gameID).setWinner(user.getID());
								break;
							}
							case 5: {
								System.out.println("[JWordsServer] " + user.getID() + " has lost!");
								int gameID = packet.readInt();
								games.getGame(gameID).setWinner(games.getGame(gameID).getOpponent(user.getID()));
								break;
							}
							case 6: {
								System.out.println("[JWordsServer] " + user.getID() + " has resigned.");
								int gameID = packet.readInt();
								games.getGame(gameID).resign(user.getID());
								break;
							}
						}
					}
					case -1: {
						loggedIn = false;
						System.out.println("[JWordsServer] Logging out");
						break;
					}
					default: {
						System.out.println("[JWordsServer] Invalid command");
						Packet p = new Packet();
						p.writeString("Invalid command");
						io.write(p);
					}
				}
			}
		}
		catch(Exception exc) {
			System.out.println("ERROR: " + exc);
			exc.printStackTrace();
			try{
				Packet p = new Packet();
				p.writeInt(-2);
				io.write(p);
			}
			catch(Exception exc2) {}
		}
		finally {
			try{
				io.close();
			}
			catch(Exception exc) {}
			
			System.out.println("[JWordsServer] Client disconnected.");
		}
	}
}