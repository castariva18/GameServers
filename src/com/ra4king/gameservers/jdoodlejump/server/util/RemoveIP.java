package com.ra4king.gameservers.jdoodlejump.server.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class RemoveIP {
	public static void main(String args[]) throws Exception {
		String input = JOptionPane.showInputDialog("Type IP:").trim();
		
		if(input == null || input.equals(""))
			return;
		
		String[] ips = input.replace(" ", ",").split(",");
		
		for(String ip : ips) {
			ip = ip.trim();
			
			JOptionPane.showMessageDialog(null, "Now removing " + ip);
			
			if(JOptionPane.showConfirmDialog(null,"Backup the scores?", "Backup?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				try(PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream("E:/Roi Atalla/GameServers/serverdata/jdoodlejump/server/backup.txt",true),"UTF-8"),true)) {
					class Entry {
						String name;
						int score;
						long time, gameDuration;
						String ip;
						boolean fromApplet;
						
						Entry(String name, int score, long time, String ip, long gameDuration, boolean fromApplet) {
							this.name = name;
							this.score = score;
							this.time = time;
							this.ip = ip;
							this.gameDuration = gameDuration;
							this.fromApplet = fromApplet;
						}
						
						public String toString() {
							return name + "<:>" + score + "<:>" + time + "<:>" + ip + "<:>" + gameDuration + "<:>" + fromApplet;
						}
					}
					
					ArrayList<String> lines = new ArrayList<String>();
					try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("E:/Roi Atalla/GameServers/serverdata/jdoodlejump/server/highscoresALL.txt")),"UTF-8"))) {
						String s;
						while((s = reader.readLine()) != null)
							lines.add(s);
					}
					
					ArrayList<Entry> scores = new ArrayList<Entry>();
					
					for(String s : lines) {
						String[] entry = s.split("<:>");
						if(entry.length >= 4 && entry[3].trim().equals(ip))
							scores.add(new Entry(entry[0],Integer.parseInt(entry[1]),Long.parseLong(entry[2]),entry[3], entry.length >= 5 ? Long.parseLong(entry[4]) : 0, entry.length >= 6 ? Boolean.parseBoolean(entry[5]) : true));
					}
					
					for(Entry e : scores)
						writer.println(e.toString());
				}
			}
			
			int count = removeIP("E:/Roi Atalla/GameServers/serverdata/jdoodlejump/server/highscoresALL.txt",ip);
			JOptionPane.showMessageDialog(null, "Removed " + count + " instances of " + ip + " in highscoresAll.txt!");
			try{
				int count2 = removeIP("E:/Roi Atalla/GameServers/serverdata/jdoodlejump/server/highscores.txt",ip);
				JOptionPane.showMessageDialog(null, "Removed " + count2 + " instances of " + ip + " in highscores.txt!");
				
				File file = new File("E:/Roi Atalla/GameServers/serverdata/jdoodlejump/server/highscores.txt");
				file.delete();
				JOptionPane.showMessageDialog(null, "highscores.txt - Deleted.");
			}
			catch(Exception exc) {
				JOptionPane.showMessageDialog(null, "Highscores.txt file not found.");
			}
			
			if(JOptionPane.showConfirmDialog(null, "Ban?", "Ban?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				addToBanList(ip);
		}
	}
	
	private static int removeIP(String file, String ip) throws Exception {
		ArrayList<String> lines = new ArrayList<String>();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"))) {
			String s;
			while((s = reader.readLine()) != null)
				lines.add(s);
		}
		
		int count = 0;
		
		for(int a = 0; a < lines.size(); a++) {
			String[] entry = lines.get(a).split("<:>");
			if(entry.length >= 4 && entry[3].trim().equals(ip)) {
				lines.remove(a);
				a--;
				count++;
			}
		}
		
		PrintWriter writer = new PrintWriter(new File(file),"UTF-8");
		
		for(String s : lines)
			writer.println(s);
		
		writer.flush();
		writer.close();
		
		return count;
	}
	
	private static void addToBanList(String ip) throws Exception {
		PrintWriter writer = new PrintWriter(new FileOutputStream("E:/Roi Atalla/GameServers/serverdata/jdoodlejump/server/bannedips.txt",true));
		writer.println(ip);
		writer.flush();
		writer.close();
	}
}
