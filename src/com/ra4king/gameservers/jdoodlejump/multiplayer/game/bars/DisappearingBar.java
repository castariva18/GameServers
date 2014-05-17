package com.ra4king.gameservers.jdoodlejump.multiplayer.game.bars;

import com.ra4king.gameservers.jdoodlejump.multiplayer.game.Doodle;

public class DisappearingBar extends Bar {
	public DisappearingBar(double x, double y) {
		setX(x);
		setY(y);
	}
	
	public int getID() {
		return 3;
	}
	
	public void jump(Doodle doodle) {
		super.jump(doodle);
		
		getParent().remove(this);
	}
	
	public boolean playsDefaultSound() {
		return false;
	}
}