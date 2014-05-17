package com.ra4king.gameservers.jwords.server.games;

import java.util.ArrayList;

import com.ra4king.gameutils.networking.Packet;

public class Board {
	private Tile tiles[][] = new Tile[15][15];
	private ArrayList<Character> pile = new ArrayList<Character>(104);
	private Letter p1rack[] = new Letter[7], p2rack[] = new Letter[7];
	
	public Board() {
		for(int a = 0; a < tiles.length; a++)
			for(int b = 0; b < tiles[a].length; b++)
				tiles[a][b] = new Tile(0);
		
		int coords[][][] = {
			{{3,0},{11,0},{0,3},{14,3},{0,11},{14,11},{3,14},{11,14}},
			{{5,1},{9,1},{7,3},{1,5},{13,5},{3,7},{11,7},{1,9},{13,9},{7,11},{5,13},{9,13}},
			{{6,0},{8,0},{3,3},{11,3},{5,5},{9,5},{0,6},{14,6},{0,8},{14,8},{5,9},{9,9},{3,11},{11,11},{6,14},{8,14}},
			{{2,1},{12,1},{1,2},{4,2},{10,2},{13,2},{2,4},{6,4},{8,4},{12,4},{4,6},{10,6},{4,8},
			 {10,8},{2,10},{6,10},{8,10},{12,10},{1,12},{4,12},{10,12},{13,12},{2,13},{12,13}}
		};
		
		for(int coord[] : coords[0])
			tiles[coord[0]][coord[1]].setBonus(Tile.TW);
		for(int coord[] : coords[1])
			tiles[coord[0]][coord[1]].setBonus(Tile.DW);
		for(int coord[] : coords[2])
			tiles[coord[0]][coord[1]].setBonus(Tile.TL);
		for(int coord[] : coords[3])
			tiles[coord[0]][coord[1]].setBonus(Tile.DL);
		
		int letterCount[] = {9,2,2,5,13,2,3,4,8,1,1,4,2,5,8,2,1,6,5,7,4,2,2,1,2,1,2};
		char letters[] = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','*'};
		for(int a = 0; a < letters.length && a < letterCount.length; a++) {
			while(letterCount[a] > 0) {
				pile.add(letters[a]);
				letterCount[a]--;
			}
		}
		
		fillRack(p1rack);
		fillRack(p2rack);
	}
	
	public Board(Packet p) {
		for(int a = 0; a < p1rack.length; a++)
			p1rack[a] = Letter.valueOf(p.readString());
		
		for(int a = 0; a < p2rack.length; a++)
			p2rack[a] = Letter.valueOf(p.readString());
		
		pile = new ArrayList<Character>();
		int size = p.readInt();
		while(size-- > 0)
			pile.add(p.readChar());
		
		for(int a = 0; a < tiles.length; a++)
			for(int b = 0; b < tiles[a].length; b++)
				tiles[a][b] = new Tile(p);
	}
	
	public void fillRack(Letter rack[]) {
		for(int a = 0; a < rack.length; a++) {
			if(rack[a] == null) {
				while(a < rack.length) {
					int l = (int)Math.round(Math.random()*pile.size());
					rack[a] = Letter.valueOf("" + pile.remove(l));
				}
			}
		}
	}
	
	public int place(String word, int x, int y, boolean horiz, boolean isP1) {
		Letter rack[];
		if(isP1)
			rack = p1rack;
		else
			rack = p2rack;
		
		int score = 0;
		if(horiz) {
			for(int a = x; a < word.length(); a++) {
				Tile tile = tiles[a][y];
				
				Letter letter = Letter.valueOf(word.charAt(a) + "");
				
				if(!remove(rack,letter))
					throw new IllegalArgumentException("Letter '" + letter + "' not in player's rack.");
				
				tile.setLetter(letter);
				
				score += tile.getLetterValue();
				if(tile.getBonus() == Tile.TL || tile.getBonus() == Tile.DL)
					score = tile.applyBonus(score);
			}
			
			for(int a = x; a < word.length(); a++)
				score = tiles[a][y].applyBonus(score);
		}
		if(horiz) {
			for(int a = y; a < word.length(); a++) {
				Tile tile = tiles[x][a];
				
				Letter letter = Letter.valueOf(word.charAt(a) + "");
				
				if(!remove(rack,letter))
					throw new IllegalArgumentException("Letter '" + letter + "' not in player's rack.");
				
				tile.setLetter(letter);
				
				score += tile.getLetterValue();
				if(tile.getBonus() == Tile.TL || tile.getBonus() == Tile.DL)
					score = tile.applyBonus(score);
			}
			
			for(int a = y; a < word.length(); a++)
				score = tiles[x][a].applyBonus(score);
		}
		
		return score;
	}
	
	private boolean remove(Letter rack[], Letter letter) {
		boolean found = false;
		
		for(int a = 0; a < rack.length; a++) {
			if(rack[a] == letter) {
				rack[a] = null;
				found = true;
				
				for(int b = a+1; b < rack.length; a++,b++) {
					rack[a] = rack[b];
					rack[b] = null;
				}
			}
		}
		
		return found;
	}
	
	public void toPacket(Packet packet) {
		for(Letter a : p1rack)
			packet.writeString(a.toString());
		
		for(Letter a : p2rack)
			packet.writeString(a.toString());
		
		packet.writeInt(pile.size());
		for(char c : pile)
			packet.writeChar(c);
		
		for(Tile a[] : tiles)
			for(Tile b : a)
				b.toPacket(packet);
	}
	
	private class Tile {
		public static final int DL = 2, TL = 3, DW = 4, TW = 9;
		private Letter letter;
		private int bonus;
		private boolean bonusUsed;
		
		public Tile(int bonus) {
			this.bonus = bonus;
		}
		
		public Tile(Packet p) {
			letter = Letter.valueOf(p.readString());
			bonus = p.readInt();
			bonusUsed = p.readBoolean();
		}
		
		/*
		public Letter getLetter() {
			return letter;
		}
		*/
		
		public void setLetter(Letter letter) {
			this.letter = letter;
		}
		
		public int getLetterValue() {
			return letter.getValue();
		}
		
		public int applyBonus(int score) {
			if(bonusUsed) {
				return score;
			}
			else {
				bonusUsed = true;
				switch(bonus) {
					case DL: return score+getLetterValue();
					case TL: return score+getLetterValue();
					case TW: return score*2;
					case DW: return score*3;
					default: return score;
				}
			}
		}
		
		public int getBonus() {
			return bonus;
		}
		
		public void setBonus(int bonus) {
			this.bonus = bonus;
		}
		
		public void toPacket(Packet packet) {
			packet.writeString(letter.name());
			packet.writeInt(bonus);
			packet.writeBoolean(bonusUsed);
		}
	}
}