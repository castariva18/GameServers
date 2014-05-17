package com.ra4king.gameservers.jdoodlejump.multiplayer.game.monsters;

import java.awt.geom.Rectangle2D;

import com.ra4king.gameservers.jdoodlejump.multiplayer.game.Doodle;

public class BlackHoleMonster extends Monster {
	private Doodle doodle;
	private long time;
	
	public BlackHoleMonster(double x, double y) {
		super(1,8);
		
		setX(x);
		setY(y);
	}
	
	public int getID() {
		return 8;
	}
	
	public Rectangle2D.Double getBounds() {
		Rectangle2D.Double bounds = super.getBounds();
		bounds.setFrame(getX()+15,getY()+15,getWidth()-30,getHeight()-30);
		return bounds;
	}
	
	public Rectangle2D.Double getHitBounds() {
		return getBounds();
	}
	
	public boolean isJumpable() {
		return false;
	}
	
	public boolean isHittable() {
		return false;
	}
	
	public void hit() {}
	
	public boolean kill(Doodle doodle) {
		this.doodle = doodle;
		return true;
	}
	
	public void update(long deltaTime) {
		super.update(deltaTime);
		
		if(doodle != null) {
			doodle.setVelocityY(0);
			doodle.setX(getX()+(getWidth()-doodle.getWidth())/2);
			doodle.setY(getY()+(getHeight()-doodle.getHeight())/2);
			time += deltaTime;
			
			if(time >= 1e9)
				doodle.setHit(true);
		}
	}
}