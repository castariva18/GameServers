package com.ra4king.gameservers.jdoodlejump.multiplayer.game.monsters;

public class MonsterSize {
	public static int getWidth(int num) {
		switch(num) {
			case 1:
				return 50;
			case 2:
				return 40;
			case 3:
				return 30;
			case 4:
				return 50;
			case 5:
				return 40;
			case 6:
				return 50;
			case 7:
				return 80;
			case 8:
				return 80;
			default:
				return 0;
		}
	}
	
	public static int getHeight(int num) {
		switch(num) {
			case 1:
				return 28;
			case 2:
				return 30;
			case 3:
				return 40;
			case 4:
				return 32;
			case 5:
				return 36;
			case 6:
				return 70;
			case 7:
				return 120;
			case 8:
				return 80;
			default:
				return 0;
		}
	}
}
