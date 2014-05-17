package com.ra4king.gameservers.jdoodlejump.server.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;

import javax.swing.JOptionPane;

public class AverageDuration {
	public static void main(String[] args) throws Exception {
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("E:/Roi Atalla/GameServers/serverdata/jdoodlejump/server/highscoresALL.txt")),"UTF-8"))) {
			long count = 0;
			
			BigInteger total = new BigInteger("0"), largest = new BigInteger("0");
			
			String l;
			while((l = reader.readLine()) != null) {
				String s[] = l.split("<:>");
				if(s.length >= 5 && !s[4].trim().startsWith("-")) {
					BigInteger i = new BigInteger(s[4]);
					
					if(i.subtract(largest).intValue() > 0)
						largest = i;
					
					total = total.add(i);
					count++;
				}
			}
			
			JOptionPane.showMessageDialog(null, "<html>Average duration of " + total.divide(new BigInteger(String.valueOf(count))) + " out a total of " + count + " entries." +
												"<br>The total duration was " + total + " ms." +
												"<br>Which is " + subdivide(total) +
												"<br>" +
												"<br>The largest duration was " + largest +
												"<br>Which is " + subdivide(largest));
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	private static String subdivide(BigInteger i) {
		return i.divide(new BigInteger(String.valueOf(1000L*60L*60L*24L*365L))) + " years, " +
			   i.divide(new BigInteger(String.valueOf(1000L*60L*60L*24L))).mod(new BigInteger("365")) + " days, " +
			   i.divide(new BigInteger(String.valueOf(1000L*60L*60L))).mod(new BigInteger("24")) + " hours, " +
			   i.divide(new BigInteger(String.valueOf(1000L*60L))).mod(new BigInteger("60")) + " minutes, " +
			   i.divide(new BigInteger(String.valueOf(1000L))).mod(new BigInteger("60")) + " seconds, and " +
			   i.mod(new BigInteger("1000")) + " ms.";
	}
}
