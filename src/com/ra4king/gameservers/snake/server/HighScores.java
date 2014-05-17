package com.ra4king.gameservers.snake.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import com.ra4king.gameutils.networking.Packet;
import com.ra4king.gameutils.networking.PacketIO;

public class HighScores {
	public static void sendScoreList(PacketIO io, String level) throws Exception {
		Scanner scan;
		if(level.equalsIgnoreCase("slug"))
			scan = new Scanner(new File(SnakeServer.codeBase + "SlugHighscores.txt"),"UTF-8");
		else if(level.equalsIgnoreCase("worm"))
			scan = new Scanner(new File(SnakeServer.codeBase + "WormHighscores.txt"),"UTF-8");
		else if(level.equalsIgnoreCase("python"))
			scan = new Scanner(new File(SnakeServer.codeBase + "PythonHighscores.txt"),"UTF-8");
		else
			return;
		
		ArrayList<String> lines = new ArrayList<String>();
		while(scan.hasNextLine()) {
			lines.add(scan.nextLine().trim());
		}
		
		scan.close();
		
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<Integer> scores = new ArrayList<Integer>();
		
		for(String line : lines) {
			String words[] = line.split("<:>");
			names.add(words[0]);
			try{
				scores.add(Integer.parseInt(words[1].trim()));
			}
			catch(Exception exc) {
				System.out.println("ERROR AT NAME " + words[0].trim());
				exc.printStackTrace();
			}
		}
		
		sort(scores,names,0,scores.size()-1);
		
		Packet packet = new Packet();
		
		for(int a = 0; a < 10; a++) {
			packet.writeString(names.get(a) + "<:>" + scores.get(a));
		}
		
		io.write(packet);
	}
	
	private static void sort(ArrayList<Integer> scores, ArrayList<String> names, int left, int right) {
		int i = left, j = right, c = scores.get((left+right)/2);
		
		do {
			while(scores.get(i) > c && i < right) i++;
			while(c > scores.get(j) && j > left) j--;
			
			if(i <= j) {
				int temp = scores.set(i,scores.get(j));
				scores.set(j,temp);
				
				String temp2 = names.set(i,names.get(j));
				names.set(j,temp2);
				
				i++;
				j--;
			}
		}while(i <= j);
		
		if(left < j) sort(scores,names,left,j);
		if(i < right) sort(scores,names,i,right);
	}
	
	public synchronized static void addHighScore(String name, int score, String level) throws Exception {
		PrintWriter writer;
		if(level.equals("slug"))
			writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(SnakeServer.codeBase + "SlugHighscores.txt",true),"UTF-8"));
		else if(level.equals("worm"))
			writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(SnakeServer.codeBase + "WormHighscores.txt",true),"UTF-8"));
		else if(level.equals("python"))
			writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(SnakeServer.codeBase + "PythonHighscores.txt",true),"UTF-8"));
		else
			return;
		
		writer.append(name + "<:>" + score + "<:>" + System.currentTimeMillis() + "\n");
		writer.flush();
		
		writer.close();
	}
}