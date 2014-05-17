package com.ra4king.gameservers.jwords.server.games;

import com.ra4king.gameutils.networking.Packet;

public class Game {
	private Board board;
	private int pid1, pid2, status;
	private int p1score, p2score;
	private int id;
	
	public Game(Packet p) {
		pid1 = p.readInt();
		pid2 = p.readInt();
		p1score = p.readInt();
		p2score = p.readInt();
		status = p.readInt();
		id = p.readInt();
		board = new Board(p);
	}
	
	public Game(int pid1, int pid2, int id) {
		this.pid1 = pid1;
		this.pid2 = pid2;
		this.id = id;
		board = new Board();
	}
	
	public void setWinner(int pid) {
		if(pid == pid1)
			status = 1;
		else if(pid == pid2)
			status = 2;
		else
			throw new IllegalArgumentException("Invalid Player ID");
	}
	
	public void resign(int pid) {
		if(pid == pid1)
			status = 3;
		else if(pid == pid2)
			status = 4;
		else
			throw new IllegalArgumentException("Invalid Player ID");
	}
	
	public Board getBoard() {
		return board;
	}
	
	public int play(String word, int x, int y, boolean isHoriz, int playerID) {
		int score = board.place(word,x,y,isHoriz,playerID == pid1);
		
		if(playerID == pid1)
			p1score += score;
		else
			p2score += score;
		
		return score;
	}
	
	public int getPlayer1ID() {
		return pid1;
	}
	
	public int getPlayer2ID() {
		return pid2;
	}
	
	public int getOpponent(int pid) {
		if(pid == pid1)
			return pid2;
		else if(pid == pid2)
			return pid1;
		else
			throw new IllegalArgumentException("Invalid Player ID");
	}
	
	public int getStatus() {
		return status;
	}
	
	public int getID() {
		return id;
	}
	
	public Packet toPacket(Packet packet) {
		packet.writeInt(pid1);
		packet.writeInt(pid2);
		packet.writeInt(p1score);
		packet.writeInt(p2score);
		packet.writeInt(status);
		packet.writeInt(id);
		board.toPacket(packet);
		return packet;
	}
}