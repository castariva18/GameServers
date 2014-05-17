package com.ra4king.gameservers.multiplayershooter.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;

import com.ra4king.gameutils.networking.DatagramPacketIO;
import com.ra4king.gameutils.networking.Packet;
import com.ra4king.gameutils.networking.SocketPacketIO;

public class ArenaServer implements Runnable {
	private Player[] players;
	private final ArrayList<SocketPacketIO> newPlayers;
	
	private DatagramPacketIO dio;
	
	private final int version = 18;
	
	public ArenaServer() {
		players = new Player[10];
		
		newPlayers = new ArrayList<>();
		
		try{
			DatagramChannel channel = DatagramChannel.open();
			channel.socket().bind(new InetSocketAddress(5053));
			
			dio = new DatagramPacketIO(channel,false);
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
		
		new Thread() {
			{
				setDaemon(true);
				start();
			}
			
			public void run() {
				while(true) {
					try {
						Thread.sleep(Long.MAX_VALUE);
					}
					catch(Exception exc) {}
				}
			}
		};
	}
	
	public void newPlayer(SocketPacketIO io) throws Exception {
		synchronized(newPlayers) {
			newPlayers.add(io);
			//io.setBlocking(false);
		}
	}
	
	public void removePlayer(int id) {
		Player player = players[id];
		
		players[id] = null;
		
		Packet p = new Packet();
		p.writeInt(1);
		p.writeInt(id);
		
		for(int i = 0; i < players.length; i++) {
			if(players[i] == null)
				continue;
			
			try{
				players[i].sio.write(p);
			}
			catch(Exception exc) {}
		}
		
		System.out.println("Player " + (id+1) + " (" + player.name + ") disconnected.");
	}
	
	public void run() {
		while(true) {
			long now = System.nanoTime();
			while(System.nanoTime()-now <= 3e6)
				try{
					Thread.sleep(1);
				}
				catch(Exception exc) {}
			
			if(newPlayers.size() > 0)
				synchronized(newPlayers) {
					for(SocketPacketIO sio : newPlayers) {
						String name;
						
						try{
							Packet packet = sio.read();
							
							if(packet == null)
								continue;
							
							if(packet.readInt() != version) {
								Packet p = new Packet();
								p.writeInt(-2);
								sio.write(p);
								sio.close();
								continue;
							}
							
							name = packet.readString();
							
							for(Player pl : players) {
								if(pl == null)
									continue;
								
								if(pl.name.equals(name)) {
									Packet p = new Packet();
									p.writeInt(-3);
									sio.write(p);
									sio.close();
									name = null;
									break;
								}
							}
							
							if(name == null)
								continue;
						}
						catch(Exception exc) {
							exc.printStackTrace();
							continue;
						}
						
						int id;
						for(id = 0; id < players.length; id++)
							if(players[id] == null)
								break;
						
						System.out.println("Player " + (id+1) + " (" + name + ") connected.");
						
						try {
							if(id == players.length) {
								Packet p = new Packet();
								p.writeInt(-1);
								sio.write(p);
								sio.close();
								continue;
							}
							else {
								Packet packet = new Packet();
								packet.writeInt(1);
								sio.write(packet);
							}
						}
						catch(Exception exc) {
							exc.printStackTrace();
							continue;
						}
						
						try {
							Player player = new Player(sio,name);
							
							Packet packet = new Packet();
							packet.writeInt(id);
							packet.writeString(name);
							player.write(packet);
							
							Packet toSend = new Packet();
							toSend.writeInt(0);
							toSend.writeInt(id);
							toSend.writeString(name);
							player.write(toSend);
							
							for(int i = 0; i < players.length; i++) {
								if(i == id || players[i] == null)
									continue;
								
								packet.writeInt(i);
								packet.writeString(players[i].name);
								players[i].write(packet);
								
								players[i].sio.write(toSend);
							}
							
							sio.write(packet);
							
							sio.setBlocking(false);
							
							players[id] = player;
						}
						catch(Exception exc) {
							exc.printStackTrace();
							removePlayer(id);
						}
					}
					
					newPlayers.clear();
				}
			
			int id = -1;
			try {
				Packet packet = dio.read();
				if(packet != null) {
					boolean sendData = false;
					long pingBack = -1;
					
					id = packet.readInt();
					Player player = players[id];
					
					if(player != null) {
						//if(player.address == null)
							player.address = packet.getAddress();
						//else if(!player.address.equals(packet.getAddress()))
							//throw new IllegalAccessException("ILLEGAL PLAYER!");
						
						int packetNum = packet.readInt();
						if(packetNum < player.lastPacketReceived)
							continue;
						
						player.lastPacketReceived = packetNum;
						
						while(packet.hasMore()) {
							int type = packet.readInt();
							
							if(type == 0) {
								sendData = true;
								
								player.read(packet);
							}
							else if(type == 1)
								pingBack = packet.readLong();
						}
						
						if(sendData) {
							packet = new Packet();
							
							packet.writeInt(player.packetCount++);
							
							for(int a = 0; a < players.length; a++) {
								if(a == id || players[a] == null)
									continue;
								
								packet.writeInt(0);
								packet.writeInt(a);
								players[a].write(packet);
							}
							
							if(pingBack != -1) {
								packet.writeInt(1);
								packet.writeLong(pingBack);
							}
							
							dio.write(packet,player.address);
							
							if(player.bulletsToSend.size() > 0 || player.messagesToSend.size() > 0) {
								packet = new Packet();
								
								for(Bullet b : player.bulletsToSend) {
									packet.writeInt(3);
									b.write(packet);
								}
								
								for(String s : player.messagesToSend) {
									packet.writeInt(5);
									packet.writeString(s);
								}
								
								player.bulletsToSend.clear();
								player.messagesToSend.clear();
								
								player.sio.write(packet);
							}
						}
					}
				}
			}
			catch(Exception exc) {
				if(!(exc instanceof IOException))
					exc.printStackTrace();
				
				if(id != -1)
					removePlayer(id);
			}
			
			id = -1;
			try {	
				for(id = 0; id < players.length; id++) {
					if(players[id] == null)
						continue;
					
					Packet packet;
					while((packet = players[id].sio.read()) != null) {
						while(packet.hasMore()) {
							int type = packet.readInt();
							
							if(type == 1) {
								int a = packet.readInt();
								players[a].messagesToSend.add("You killed " + players[id].name);
								players[a].score++;
								
								Packet p = new Packet();
								p.writeInt(2);
								players[a].sio.write(p);
							}
							else if(type == 2) {
								Bullet bullet = new Bullet(id, packet);
								
								for(int b = 0; b < players.length; b++) {
									if(b == id || players[b] == null)
										continue;
									
									players[b].bulletsToSend.add(bullet);
								}
							}
						}
					}
				}
			}
			catch(Exception exc) {
				if(!(exc instanceof IOException))
					exc.printStackTrace();
				
				if(id != -1)
					removePlayer(id);
			}
		}
	}
	
	private static class Player {
		private SocketPacketIO sio;
		private SocketAddress address;
		
		private String name;
		
		private int packetCount, lastPacketReceived;
		
		private double x = 200, y = 200, rot;
		private int health = 50, ping, score;
		
		private ArrayList<String> messagesToSend;
		private ArrayList<Bullet> bulletsToSend;
		
		public Player(SocketPacketIO sio, String name) {
			this.sio = sio;
			messagesToSend = new ArrayList<>();
			bulletsToSend = new ArrayList<>();
			this.name = name;
		}
		
		public void read(Packet p) {
			x = p.readDouble();
			y = p.readDouble();
			rot = p.readDouble();
			health = p.readInt();
			ping = p.readInt();
		}
		
		public void write(Packet p) {
			p.writeDouble(x,y,rot);
			p.writeInt(health,score,ping);
		}
	}
	
	private static class Bullet {
		private double x, y, speed, rot;
		private final int id;
		
		public Bullet(int id, Packet p) {
			this.id = id;
			x = p.readDouble();
			y = p.readDouble();
			speed = p.readDouble();
			rot = p.readDouble();
		}
		
		public void write(Packet p) {
			p.writeInt(id);
			p.writeDouble(x,y,speed,rot);
		}
	}
}
