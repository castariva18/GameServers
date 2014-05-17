package com.ra4king.gameservers.jdoodlejump.server.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

public class DownloadCount {
	public static void main(String[] args) throws Exception {
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("E:/Roi Atalla/GameServers/serverdata/jdoodlejump/server/log.txt"),"UTF-8"))) {
			int count = 0;
			String s;
			while((s = reader.readLine()) != null)
				if(s.contains("Clicked on Download!"))
					count++;
			
			JOptionPane.showMessageDialog(null, "Download link clicked " + count + " times!");
		}
	}
}
