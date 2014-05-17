package com.ra4king.gameservers.jdoodlejump.server.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

public class AverageScore {
	public static void main(String args[]) throws Exception {
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("E:/Roi Atalla/GameServers/serverdata/jdoodlejump/server/highscoresALL.txt")),"UTF-8"))) {
			long total = 0, count = 0;
			
			String l;
			while((l = reader.readLine()) != null) {
				String s[] = l.split("<:>");
				total += Integer.parseInt(s[1]);
				count++;
			}
			
			JOptionPane.showMessageDialog(null, "<html>Average score of " + ((double)total/count) + " out a total of " + count + " entries.<br>The sum of all the scores was " + total);
		}
	}
}
