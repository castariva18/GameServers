package com.ra4king.gameservers.snake.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import com.ra4king.gameutils.networking.Packet;
import com.ra4king.gameutils.networking.PacketIO;

public class ClientConnection implements Runnable {
	private PacketIO io;
	private static Object versionObj = new Object();
	
	public ClientConnection(PacketIO io) {
		this.io = io;
	}
	
	public void run() {
		try{
			boolean isConnected = true;
			
			while(isConnected) {
				Packet packet = io.read();
				
				int id = packet.readInt();
				
				switch(id) {
					case 0:
						sendVersion();
						System.out.println("Requested version number.");
						break;
					case 1:
						String name = packet.readString();
						int score = packet.readInt();
						String level = packet.readString();
						
						if(name != null && name.length() < 25 && !name.equals("") && score > 0)
							HighScores.addHighScore(name,score,level);
						
						System.out.println("Name: " + name + ", Score: " + score + ", Level: " + level);
						break;
					case 2:
						level = packet.readString();
						HighScores.sendScoreList(io,level);
						System.out.println("Requested high scores list for level " + level + ".");
						break;
					case -1:
						isConnected = false;
						System.out.println("Quitting.");
						break;
					default:
						packet = new Packet();
						packet.writeString("Invalid command");
						io.write(packet);
						isConnected = false;
						System.out.println("Invalid command.");
						break;
				}
			}
		}
		catch(Exception exc) {
			System.out.println("ERROR: " + exc);
			exc.printStackTrace();
			try{
				Packet packet = new Packet();
				packet.writeInt(-2);
				io.write(packet);
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
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(SnakeServer.codeBase + "version.txt"))));
			Packet packet = new Packet();
			packet.writeDouble(Double.parseDouble(reader.readLine()));
			io.write(packet);
			reader.close();
		}
	}
}