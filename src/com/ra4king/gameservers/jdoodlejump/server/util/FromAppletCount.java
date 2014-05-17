package com.ra4king.gameservers.jdoodlejump.server.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

public class FromAppletCount {
	public static void main(String[] args) throws Exception {
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("E:/Roi Atalla/GameServers/serverdata/jdoodlejump/server/highscoresALL.txt")),"UTF-8"))) {
			long totalCount = 0, fromAppletCount = 0;
			
			String l;
			while((l = reader.readLine()) != null) {
				String s[] = l.split("<:>");
				if(s.length >= 6) {
					totalCount++;
					if(Boolean.parseBoolean(s[5]))
						fromAppletCount++;
				}
			}
			
			JOptionPane.showMessageDialog(null, "<html>Scores submitted through applet: " + fromAppletCount + ".<br>Scores submitted through desktop app: " + (totalCount-fromAppletCount));
		}
	}
}
