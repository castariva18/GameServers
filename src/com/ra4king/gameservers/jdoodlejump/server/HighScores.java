package com.ra4king.gameservers.jdoodlejump.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;

import com.ra4king.gameutils.networking.Packet;
import com.ra4king.gameutils.networking.PacketIO;
import com.ra4king.gameutils.networking.SocketPacketIO;

public class HighScores {
	private enum TimeInterval {
		TODAY(100), WEEK(500), MONTH(1000), ALLTIME(2000);
		
		ArrayList<Score> list;
		final int count;
		
		TimeInterval(int num) {
			this.count = num;
			list = new ArrayList<>();
		}
		
		boolean isValid(long timeStamp) {
			Calendar now = Calendar.getInstance();
			now.setFirstDayOfWeek(Calendar.MONDAY);
			Calendar specd = Calendar.getInstance();
			specd.setFirstDayOfWeek(Calendar.MONDAY);
			specd.setTimeInMillis(timeStamp);
			
			switch(this) {
				case TODAY:
					return specd.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
						   specd.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR);
				case WEEK:
					return specd.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
						   specd.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR);
				case MONTH:
					return specd.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
						   specd.get(Calendar.MONTH) == now.get(Calendar.MONTH);
				case ALLTIME: return true;
				default: throw new IllegalAccessError("Should never be called.");
			}
		}
		
		void checkTime() {
			if(list.size() == 0)
				return;
			
			long time = list.get(list.size()-1).getTime();
			
			if(!isValid(time)) {
				list.clear();
				System.out.println("\n" + name() + " has been cleared.\n");
			}
		}
	}
	
	private HashSet<String> bannedIPs = new HashSet<String>();
	
	public HighScores() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(JDoodleJumpServer.codeBase + "bannedips.txt")),"UTF-8"));
			
			String s;
			while((s = reader.readLine()) != null)
				bannedIPs.add(s);
		}
		catch(Exception exc) {
		}
		finally {
			try {
				reader.close();
			}
			catch(Exception exc) {}
		}
		
		ArrayList<String> lines = new ArrayList<String>();
		
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(JDoodleJumpServer.codeBase + "highscores.txt")),"UTF-8"));
			
			String s;
			while((s = reader.readLine()) != null)
				lines.add(s);
		}
		catch(FileNotFoundException exc) {
			System.out.println("RESTORING HIGHSCORES FILE");
			try{
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(JDoodleJumpServer.codeBase + "highscoresALL.txt")),"UTF-8"));
				
				String s;
				while((s = reader.readLine()) != null)
					lines.add(s);
			}
			catch(Exception exc2) {
				exc2.printStackTrace();
			}
			finally {
				try {
					reader.close();
				}
				catch(Exception exc2) {}
			}
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
		finally {
			try {
				reader.close();
			}
			catch(Exception exc) {}
		}
		
		System.out.println("Parsing " + lines.size() + " entries!");
		System.out.println("^");
		int count = 0, percent = 0;
		
		long begin = System.currentTimeMillis();
		
		for(String s : lines) {
			try {
				String words[] = s.split("<:>");
				
				String name = new String(words[0]);
				int score = Integer.parseInt(words[1]);
				
				long time = 0;
				if(words.length >= 3)
					time = Long.parseLong(words[2]);
				
				String ip = "";
				if(words.length >= 4)
					ip = new String(words[3]);
				
				long gameDuration = 0;
				if(words.length >= 5)
					gameDuration = Long.parseLong(words[4]);
				
				boolean fromApplet = true;
				if(words.length >= 6)
					fromApplet = Boolean.parseBoolean(words[5]);
				
				Score entry = new Score(name,score,time,ip,gameDuration,fromApplet);
				
				TimeInterval.ALLTIME.list.add(entry);
				
				if(TimeInterval.MONTH.isValid(time))
					TimeInterval.MONTH.list.add(entry);
				
				if(TimeInterval.WEEK.isValid(time)) {
					TimeInterval.WEEK.list.add(entry);
					
					if(TimeInterval.TODAY.isValid(time))
						TimeInterval.TODAY.list.add(entry);
				}
				
				if(count%5000 == 0) {
					for(TimeInterval interval : TimeInterval.values()) {
						Collections.sort(interval.list);
						fix(interval);
					}
				}
			}
			catch(Exception exc) {
				System.out.println(s);
				throw exc;
			}
			
			count++;
			if(count%(lines.size()/100) == 0) {
				percent++;
				
				if(percent%6 == 0)
					System.out.println("| " + (percent < 10 ? "0" : "") + (percent <= 100 ? percent : 100) + "%");
			}
		}
		
		for(TimeInterval interval : TimeInterval.values()) {
			Collections.sort(interval.list);
			fix(interval);
		}
		
		System.out.println("v");
		
		System.out.println("Sorting the scores took " + (System.currentTimeMillis()-begin)/1000.0 + " seconds.");
		
		updateFile();
		
		System.gc();
	}
	
	public synchronized void sendScoreList(PacketIO io) throws Exception {
		((SocketPacketIO)io).setBufferSize(80*1024);
		
		sendScoreList(io,TimeInterval.TODAY);
		sendScoreList(io,TimeInterval.WEEK);
		sendScoreList(io,TimeInterval.MONTH);
		sendScoreList(io,TimeInterval.ALLTIME);
	}
	
	public synchronized void sendScoreList(PacketIO io, TimeInterval interval) throws Exception {
		ArrayList<Score> list = interval.list;
		int num = interval.count;
		
		Packet packet = new Packet();
		
		int empty = 0;
		
		for(int a = num-1; a >= 0; a--) {
			try{
				packet.writeString(list.get(a).getName() + "<:>" + list.get(a).getScore());
			}
			catch(Exception exc) {
				empty++;
			}
		}
		
		for(int a = 0; a < empty; a++)
			packet.writeString("--<:>------");
		
		io.write(packet);
	}
	
	public synchronized void addHighScore(String name, int score, long duration, String ip, boolean fromApplet) {
		if(bannedIPs.contains(ip)) {
			System.err.println("BANNED IP!");
			return;
		}
		
		Score s = new Score(name,score,System.currentTimeMillis(),ip,duration,fromApplet);
		
		if(add(TimeInterval.TODAY,s) | add(TimeInterval.WEEK,s) | add(TimeInterval.MONTH,s) | add(TimeInterval.ALLTIME,s))
			updateFile();
		
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(JDoodleJumpServer.codeBase + "highscoresALL.txt",true),"UTF-8"),true);
			
			writer.println(s);
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
		finally {
			try {
				writer.close();
			}
			catch(Exception exc) {}
		}
	}
	
	private boolean add(TimeInterval interval, Score s) {
		interval.checkTime();
		
		int idx = Collections.binarySearch(interval.list,s);
		
		if(idx < 0) {
			if(interval.list.size() == interval.count && -idx-1 == 0)
				return false;
			
			interval.list.add(-idx-1,s);
		}
		else {
			if(interval.list.size() == interval.count && idx == 0)
				return false;
			
			interval.list.add(idx,s);
		}
		
		fix(interval);
		
		return true;
	}
	
	private void fix(TimeInterval interval) {
		int num = interval.count;
		
		ArrayList<Score> fixed = new ArrayList<Score>();
		
		for(int a = (interval.list.size()-num > 0 ? interval.list.size()-num : 0); a < interval.list.size(); a++)
			fixed.add(interval.list.get(a));
		
		interval.list.clear();
		interval.list.addAll(fixed);
	}
	
	private void updateFile() {
		System.err.println("UPDATING FILE!");
		
		ArrayList<Score> all = new ArrayList<Score>();
		
		all.addAll(TimeInterval.ALLTIME.list);
		addFixDups(all,TimeInterval.TODAY.list);
		addFixDups(all,TimeInterval.WEEK.list);
		addFixDups(all,TimeInterval.MONTH.list);
		
		PrintWriter writer = null;
		try{
			writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(JDoodleJumpServer.codeBase + "highscores.txt"),"UTF-8"),true);
			
			for(Score s : all)
				writer.println(s);
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
		finally {
			try {
				writer.close();
			}
			catch(Exception exc) {}
		}
	}
	
	private void addFixDups(ArrayList<Score> list, ArrayList<Score> toAdd) {
		for(Score s : toAdd)
			if(!list.contains(s))
				list.add(s);
	}
	
	private static class Score implements Comparable<Score> {
		private String name;
		private int score;
		private long time, gameDuration;
		private String ip;
		private boolean fromApplet;
		
		public Score(String name, int score, long time, String ip, long gameDuration, boolean fromApplet) {
			this.name = name;
			this.score = score;
			this.time = time;
			this.gameDuration = gameDuration;
			this.ip = ip;
			this.fromApplet = fromApplet;
		}
		
		public String getName() {
			return name;
		}
		
		public int getScore() {
			return score;
		}
		
		public long getTime() {
			return time;
		}
		
		public boolean equals(Object o) {
			if(o instanceof Score) {
				Score s = (Score)o;
				return name.equals(s.name) && score == s.score && time == s.time && gameDuration == s.gameDuration && fromApplet == s.fromApplet;
			}
			
			return false;
		}
		
		public int compareTo(Score s) {
			return score-s.score;
		}
		
		public String toString() {
			return name + "<:>" + score + "<:>" + time + "<:>" + ip + "<:>" + gameDuration + "<:>" + fromApplet;
		}
	}
}