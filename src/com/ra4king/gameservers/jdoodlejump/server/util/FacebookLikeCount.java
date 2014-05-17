package com.ra4king.gameservers.jdoodlejump.server.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.JOptionPane;

public class FacebookLikeCount {
	public static void main(String args[]) throws Exception {
		int count = 0;
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("E:/Roi Atalla/GameServers/serverdata/jdoodlejump/server/log.txt")),"UTF-8"))) {
			System.out.println("Counting in log0.txt");
			String s;
			while((s = reader.readLine()) != null)
				if(s.contains("Clicked on Facebook like link!"))
					count++;
		}
		
//		try(ZipFile file = new ZipFile(new File("E:/Roi Atalla/GameServers/serverdata/jdoodlejump/server/logs.zip"))) {
//			Enumeration<? extends ZipEntry> e = file.entries();
//			while(e.hasMoreElements()) {
//				ZipEntry ze = e.nextElement();
//				try(BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(ze)))) {
//					System.out.println("Counting in " + ze.getName());
//					String s;
//					while((s = reader.readLine()) != null)
//						if(s.contains("Clicked on Facebook like link!"))
//							count++;
//				}
//			}
//		}
		
		JOptionPane.showMessageDialog(null, "Facebook like link clicked " + count + " times!");
	}
}
