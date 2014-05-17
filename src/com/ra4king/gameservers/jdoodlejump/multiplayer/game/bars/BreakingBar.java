package com.ra4king.gameservers.jdoodlejump.multiplayer.game.bars;

import java.awt.geom.Rectangle2D;

import com.ra4king.gameservers.jdoodlejump.multiplayer.game.Doodle;

public class BreakingBar extends Bar {
	private boolean broken;
	
	public BreakingBar(double x, double y) {
		setX(x);
		setY(y);
	}
	
	public int getID() {
		return 4;
	}
	
	public void jump(Doodle doodle) {
		broken = true;
	}
	
	public boolean playsDefaultSound() {
		return false;
	}
	
	public Rectangle2D.Double getBounds() {
		Rectangle2D.Double bounds = super.getBounds();
		
		if(broken)
			bounds.setFrame(0,0,0,0);
		
		return bounds;
	}
	
	public void update(long deltaTime) {
		super.update(deltaTime);
		
		if(broken)
			setY(getY()+(420*(deltaTime/1000000000.0)));
	}
}