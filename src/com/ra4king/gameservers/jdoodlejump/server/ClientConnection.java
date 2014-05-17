package com.ra4king.gameservers.jdoodlejump.server;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Scanner;

import com.ra4king.gameutils.networking.Packet;
import com.ra4king.gameutils.networking.PacketIO;
import com.ra4king.gameutils.networking.SocketPacketIO;

public class ClientConnection implements Runnable {
	private PacketIO io;
	private SocketChannel channel;
	private static HighScores scores;
	private static Object versionObj = new Object();
	
	static {
		try {
			scores = new HighScores();
		}
		catch(Exception exc) {
			exc.printStackTrace();
			System.exit(0);
		}
	}
	
	public static void init() {}
	
	public ClientConnection(SocketChannel channel) throws Exception {
		this.channel = channel;
	}
	
	public PacketIO getPacketIO() {
		return io;
	}
	
	public void run() {
		try{
			String ip = ((InetSocketAddress)channel.socket().getRemoteSocketAddress()).getAddress().getHostAddress();
			System.out.println("NEW CONNECTION:" + ip);
			System.out.println("\nClient connected on " + new Date());
			System.out.println(ip);
			
			io = new SocketPacketIO(channel);
			
			new Timeout(io);
			
			String input = io.read().readString();
			
			System.out.println(input);
			
			if(!input.equals("DoodleJump game")) {
				System.out.println("Invalid command");
				
				Packet p = new Packet();
				p.writeInt(-1);
				io.write(p);
				
				return;
			}
			
			boolean isConnected = true;
			
			while(isConnected) {
				Packet packet = io.read();
				
				int id = packet.readInt();
				
				switch(id) {
					case 0:
						System.out.println("Requested version number.");
						sendVersion();
						break;
					case 1:
						String name = packet.readString();
						int score = packet.readInt();
						
						long duration = packet.readLong();
						
						if(duration <= 0) {
							System.out.println("INVALID DURATION: " + duration);
							continue;
						}
						
						boolean fromApplet = true;
						if(packet.hasMore())
							fromApplet = packet.readBoolean();
						
						System.out.println("DURATION OF " + duration/1000.0 + " seconds");
						System.out.println("Submitted through " + (fromApplet ? "applet." : "desktop app."));
						System.out.println("Submitted highscore, name: " + name + ", score: " + score);
						
						if(name != null && name.length() < 25 && !name.equals("") && score > 0)
							scores.addHighScore(name,score,duration,io.getSocketAddress().getAddress().getHostAddress(),fromApplet);
						
						break;
					case 2:
						System.out.println("Requested high scores list.");
						scores.sendScoreList(io);
						break;
					case 3:
						System.out.println("Clicked on Facebook like link!");
						break;
					case 4:
						System.out.println("Clicked on Download!");
						break;
					case -1:
						System.out.println("Quitting.");
						isConnected = false;
						break;
					default:
						System.out.println("Invalid command.");
						Packet p = new Packet();
						p.writeString("Invalid command");
						io.write(p);
						
						isConnected = false;
						break;
				}
			}
		}
		catch(Exception exc) {
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
			
			System.out.println("Client disconnected.");
		}
	}
	
	private void sendVersion() throws Exception {
		synchronized(versionObj) {
			try {
				Scanner scanner = new Scanner(new File(JDoodleJumpServer.codeBase + "version.txt"),"UTF-8");
				Packet packet = new Packet();
				packet.writeDouble(scanner.nextDouble());
				io.write(packet);
				scanner.close();
			}
			catch(Exception exc) {
				Packet packet = new Packet();
				packet.writeDouble(1.0);
				io.write(packet);
			}
		}
	}
	
	private static class Timeout extends Thread {
		private PacketIO io;
		
		public Timeout(PacketIO io) {
			this.io = io;
			start();
		}
		
		public void run() {
			try{
				long time = System.currentTimeMillis();
				while(System.currentTimeMillis()-time <= 5000 && io.isConnected())
					Thread.sleep(500);
			}
			catch(Exception exc) {}
			finally {
				while(io.isConnected()) {
					try {
						io.close();
					}
					catch(Exception exc) {}
				}
			}
		}
	}
}