package com.ra4king.gameservers.jdoodlejump.server.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class DurationFinder {
	public static void main(String[] args) throws Exception {
		String i;
		while("".equals(i = JOptionPane.showInputDialog("Type minimum score to search:")));
		
		if(i == null)
			return;
		
		int score = Integer.parseInt(i);
		
		ArrayList<String> lines = new ArrayList<String>();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("E:/Roi Atalla/GameServers/serverdata/jdoodlejump/server/highscoresALL.txt")),"UTF-8"))) {
			String s;
			while((s = reader.readLine()) != null)
				lines.add(s);
		}
		
		long total = 0, num = 0, highest = -1, lowest = -1;
		
		for(String line : lines) {
			String parts[] = line.split("<:>");
			if(parts.length >= 5) {
				int scr = Integer.parseInt(parts[1]);
				if(scr >= score && scr < score+10000) {
					long dur = Long.parseLong(parts[4]);
					if(dur > 0) {
						total += dur;
						num++;
						
						if(dur > highest)
							highest = dur;
						
						if(lowest == -1 || dur < lowest)
							lowest = dur;
					}
				}
			}
		}
		
		JOptionPane.showMessageDialog(null, "Average duration for range " + score + " - " + (score+200000) + " is " + (total/(double)num) + ". Total: " + num + ". Highest: " + highest + ". Lowest: " + lowest + ".");
	}
}
