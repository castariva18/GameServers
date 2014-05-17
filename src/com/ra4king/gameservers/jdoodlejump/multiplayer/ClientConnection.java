package com.ra4king.gameservers.jdoodlejump.multiplayer;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.SocketChannel;
import java.util.Date;

import com.ra4king.gameservers.jdoodlejump.multiplayer.Users.User;
import com.ra4king.gameutils.networking.Packet;
import com.ra4king.gameutils.networking.SocketPacketIO;

public class ClientConnection implements Runnable {
	private static Lobby lobby;
	private static Users users;
	
	private SocketPacketIO io;
	private Match match;
	private User user;
	private boolean skipLogin;
	private boolean waiting;
	
	public static void init() {}
	
	static {
		try {
			lobby = new Lobby();
			users = new Users(JDoodleJumpMultiplayerServer.codeBase + "users.dat"); 
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	public ClientConnection(SocketChannel channel) throws Exception {
		String ip = ((InetSocketAddress)channel.socket().getRemoteSocketAddress()).getAddress().getHostAddress();
		System.out.println("\nNEW CONNECTION:" + ip);
		System.out.println("Client connected on " + new Date());
		System.out.println(ip);
		
		io = new SocketPacketIO(channel,80*1024);
	}
	
	public SocketPacketIO getPacketIO() {
		return io;
	}
	
	public Match getMatch() {
		return match;
	}
	
	public User getUser() {
		return user;
	}
	
	public synchronized void run() {
		try{
			Packet packet = io.read();
			
			if(skipLogin)
				skipLogin = false;
			else {
				if(packet.readString().equals("DoodleJump game"))
					System.out.println("DoodleJump game");
				else {
					System.out.println("Invalid command.");
						return;
				}
				
				login: while(true) {
					packet = io.read();
					
					int id = packet.readInt();
					
					String name = packet.readString();
					String pass = packet.readString();
					
					if(id == 0) {
						switch(users.login(name, pass)) {
							case LOGGED_IN:
								System.out.println(name + ": Logged in.");
								user = users.get(name);
								Packet p = new Packet();
								p.writeInt(0);
								io.write(p);
								break login;
							case INCORRECT_PASS:
								p = new Packet();
								p.writeInt(-1);
								io.write(p);
								break;
							case UNKNOWN_USER:
								p = new Packet();
								p.writeInt(2);
								io.write(p);
								break;
						}
					}
					else if(id == 1) {
						System.out.print("Registering: " + name + "...");
						if(users.register(name, pass)) {
							System.out.println("Success!");
							Packet p = new Packet();
							p.writeInt(0);
							io.write(p);
							break login;
						}
						else {
							System.out.println("Name taken.");
							Packet p = new Packet();
							p.writeInt(-1);
							io.write(p);
						}
					}
					else {
						System.out.println("Invalid ID.");
						return;
					}
				}
			}
			
			while(true) {
				packet = io.read();
				
//				if(packet.peek() instanceof Byte) {
//					Packet p = new Packet();
//					p.writeInt(-1);
//					io.write(p);
//					continue;
//				}
				
				int id = packet.readInt();
				
				switch(id) {
					case 0:
						System.out.println(user.name + ": Searching for match!");
						ClientConnection c = lobby.getNext(this);
						if(c != null) {
							System.out.println(user.name + ": Setting up match against " + c.user.name + ".");
							Packet p = new Packet();
							p.writeInt(1);
							p.writeInt(2);
							io.write(p);
							
							p = new Packet();
							p.writeInt(1);
							p.writeInt(2);
							c.io.write(p);
							
							c.match = match = new Match(c.user.name,c.io,user.name,io);
							
							Thread t = new Thread(match);
							t.start();
							t.join();
							
							c.match = match = null;
							c.waiting = false;
							
							io.setBlocking(true);
						}
						else {
							System.out.println(user.name + ": Waiting for opponent...");
							Packet p = new Packet();
							p.writeInt(0);
							io.write(p);
							waiting = true;
							while(waiting) {
								try {
									Thread.sleep(100);
								}
								catch(Exception exc) {}
							}
							io.setBlocking(true);
						}
						break;
					case -1:
						System.out.println(user.name + ": Logging out.");
						if(match != null)
							System.out.println(user.name + ": WTF?!");
						return;
					default:
						throw new Exception("INVALID ID: " + id);
				}
			}
		}
		catch(AsynchronousCloseException exc) {
			System.out.println(user.name + ": New match!");
			while(match != null)
				try{
					wait();
				}
				catch(Exception exc2) {}
			skipLogin = true;
			run();
		}
		catch(Exception exc) {
			exc.printStackTrace();
			try{
				Packet p = new Packet();
				p.writeInt(-1);
				io.write(p);
			}
			catch(Exception exc2) {}
		}
		finally {
			try {
				io.close();
			}
			catch(Exception exc) {}
			System.out.println((user == null ? "" : user.name + ": " + "Disconnecting."));
		}
	}
}