package com.ra4king.gameservers.multiplayershooter.server;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.Date;

import com.ra4king.gameutils.networking.SocketPacketIO;

public class MultiplayerShooterServer {
	public static String codeBase;
	
	public static void main(String[] args) {
		codeBase = args[0];//System.getProperty("user.dir") + "/serverdata/multiplayershooter/server/";
		
		try{
			ServerSocketChannel server = ServerSocketChannel.open();
			server.socket().bind(new InetSocketAddress(5053));
			
			System.out.println(server);
			
			ArenaServer arena = new ArenaServer();
			new Thread(arena).start();
			
			while(true) {
				try{
					SocketPacketIO io = new SocketPacketIO(server.accept());
					
					String ip = io.getSocketAddress().getAddress().getHostAddress();
					
					System.out.println("NEW CONNECTION:" + ip);
					
					System.out.println("\nClient connected on " + new Date());
					System.out.println(ip);
					
					arena.newPlayer(io);
				}
				catch(Exception exc) {
					exc.printStackTrace();
				}
			}
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
	}
}
