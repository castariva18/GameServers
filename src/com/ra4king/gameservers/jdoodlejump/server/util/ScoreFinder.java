package com.ra4king.gameservers.jdoodlejump.server.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JOptionPane;

public class ScoreFinder {
	public static void main(String[] args) throws Exception {
		String name = JOptionPane.showInputDialog("What is the name:");
		String IP = null;
		if(name == null || name.equals("")) {
			IP = JOptionPane.showInputDialog("What is the IP:");
			
			if(IP == null || IP.equals(""))
				return;
			
			name = null;
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
			if(name != null) {
				if(entry[0].equals(name)) {
					long time = 0;
					if(entry.length >= 3)
						time = Long.parseLong(entry[2]);
					
					String ip = "";
					if(entry.length >= 4)
						ip = entry[3];
					
					long duration = 0;
					if(entry.length >= 5)
						duration = Long.parseLong(entry[4]);
					
					boolean fromApplet = true;
					if(entry.length >= 6)
						fromApplet = Boolean.parseBoolean(entry[5]);
					
					scores.add(new Entry(entry[0],Integer.parseInt(entry[1]),time,ip,duration,fromApplet));
				}
			}
			else if(entry.length >= 4) {
				if(entry[3].equals(IP))
					scores.add(new Entry(entry[0],Integer.parseInt(entry[1]),Long.parseLong(entry[2]),entry[3], entry.length >= 5 ? Long.parseLong(entry[4]) : 0, entry.length >= 6 ? Boolean.parseBoolean(entry[5]) : true));
			}
		}
		
		if(scores.size() > 0) {
			Entry max = scores.get(0);
			System.out.println("All scores:\n" + max);
			for(int a = 1; a < scores.size(); a++) {
				System.out.println(scores.get(a));
				if(scores.get(a).score > max.score)
					max = scores.get(a);
			}
			System.out.println("Biggest score: " + max);
		}
		else {
			System.out.println("No scores found for " + (name == null ? IP : name) + ".");
		}
	}
	
	private static class Entry {
		String name;
		int score;
		long time;
		String ip;
		long duration;
		boolean fromApplet;
		
		Entry(String name, int score, long time, String ip, long duration, boolean fromApplet) {
			this.name = name;
			this.score = score;
			this.time = time;
			this.ip = ip;
			this.duration = duration;
			this.fromApplet = fromApplet;
		}
		
		public String toString() {
			return name + "\t" + score + "\t" + new Date(time) + "\t" + ip + "\t" + duration + "\t" + fromApplet;
		}
	}
}
