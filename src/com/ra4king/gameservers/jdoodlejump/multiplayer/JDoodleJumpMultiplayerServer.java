package com.ra4king.gameservers.jdoodlejump.multiplayer;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

public class JDoodleJumpMultiplayerServer {
	public static String codeBase = "E:/Roi Atalla/Documents/Programming Files/Java Files/Personal Projects/GameServers/serverdata/jdoodlejump/multiplayer/";
	
	public static void main(String args[]) {
		//codeBase = args[0];
		
		ClientConnection.init();
		
		try{
			ServerSocketChannel server = ServerSocketChannel.open();
			server.socket().bind(new InetSocketAddress(5052));
			
			System.out.println(server);
			
			while(true) {
				try{
					new Thread(new ClientConnection(server.accept())).start();
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
