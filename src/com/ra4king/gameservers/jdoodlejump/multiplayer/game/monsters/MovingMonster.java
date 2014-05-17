package com.ra4king.gameservers.jdoodlejump.multiplayer.game.monsters;

import java.awt.geom.Rectangle2D;

import com.ra4king.gameservers.jdoodlejump.multiplayer.game.Doodle;

public class MovingMonster extends Monster {
	private double vx = 100;
	private boolean hasUpdated = false;
	
	public MovingMonster(int num, double x, double y, int hitsTotal) {
		super(hitsTotal,num);
		
		setX(x);
		setY(y);
	}
	
	public int getID() {
		return 6;
	}
	
	public Rectangle2D.Double getBounds() {
		Rectangle2D.Double bounds = super.getBounds();
		
		if(!hasUpdated)
			bounds.setFrame(0,getY(),getParent().getWidth(),getHeight());
		
		return bounds;
	}
	
	public Rectangle2D.Double getHitBounds() {
		return getBounds();
	}
	
	public boolean isJumpable() {
		return true;
	}
	
	public boolean isHittable() {
		return true;
	}
	
	public void hit() {
		addHit();
		
		if(getHitsCount() >= getHitsTotal())
			getParent().remove(this);
	}
	
	public boolean kill(Doodle doodle) {
		return false;
	}
	
	public void update(long deltaTime) {
		super.update(deltaTime);
		
		if(!hasUpdated)
			hasUpdated = true;
		
		setX(getX()+vx*(deltaTime/1000000000.0));
		
		if(getX() >= getParent().getWidth()-getWidth())
			vx = -Math.abs(vx);
		else if(getX() <= 0)
			vx = Math.abs(vx);
	}
}