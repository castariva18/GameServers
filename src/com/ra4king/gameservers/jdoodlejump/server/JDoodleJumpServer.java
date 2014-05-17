package com.ra4king.gameservers.jdoodlejump.server;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

public class JDoodleJumpServer {
	public static String codeBase;
	
	public static void main(String args[]) {
		codeBase = args[0];
		
		ClientConnection.init();
		
		try{
			ServerSocketChannel server = ServerSocketChannel.open();
			server.bind(new InetSocketAddress(5050));
			
			System.out.println(server);
			
			int count = 0;
			while(true) {
				try{
					new Thread(new ClientConnection(server.accept())).start();
					count++;
					if(count == 50) {
						System.gc();
						count = 0;
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
