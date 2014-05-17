package com.ra4king.gameservers.snake.server;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.Date;

import com.ra4king.gameutils.networking.Packet;
import com.ra4king.gameutils.networking.SocketPacketIO;

public class SnakeServer {
	public static String codeBase;
	
	public static void main(String args[]) {
		codeBase = args[0];
		
		try{
			//ServerSocketChannel server = ServerSocketChannel.open();
			//server.socket().bind(new InetSocketAddress(5051));
			
			ServerSocketChannel server = ServerSocketChannel.open();
			server.bind(new InetSocketAddress(5051));
			
			System.out.println(server);
			
			while(true) {
				try{
					System.gc();
					
					SocketPacketIO io = new SocketPacketIO(server.accept());
					
					String ip = io.getSocketAddress().getAddress().getHostAddress();
					
					System.out.println("NEW CONNECTION:" + ip);
					
					System.out.println("\nClient connected on " + new Date());
					System.out.println(ip);
					
					String input = io.read().readString();
					
					System.out.println(input);
					
					if(input.equals("Snake game")) {
						new Thread(new ClientConnection(io)).start();
					}
					else {
						System.out.println("Invalid command");
						
						Packet p = new Packet();
						p.writeInt(-1);
						io.write(p);
						
						io.close();
					}
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
