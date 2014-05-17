package com.ra4king.gameservers.jdoodlejump.multiplayer.game.monsters;

import java.awt.geom.Rectangle2D;

import com.ra4king.gameservers.jdoodlejump.multiplayer.game.Doodle;

public class AlienMonster extends Monster {
	private Doodle doodle;
	private long time;
	
	public AlienMonster(double x, double y) {
		super(1,7);
		
		setX(x);
		setY(y);
	}
	
	public int getID() {
		return 7;
	}
	
	public Rectangle2D.Double getJumpBounds() {
		Rectangle2D.Double bounds = getBounds();
		bounds.setFrame(getX(),getY(),getWidth(),30);
		return bounds;
	}
	
	public Rectangle2D.Double getHitBounds() {
		Rectangle2D.Double bounds = getBounds();
		bounds.setFrame(getX(),getY()+50,getWidth(),getHeight()-50);
		return bounds;
	}
	
	public boolean isJumpable() {
		return true;
	}
	
	public boolean isHittable() {
		return true;
	}
	
	public void hit() {
		getParent().remove(this);
	}
	
	public boolean kill(Doodle doodle) {
		this.doodle = doodle;
		doodle.setDying(true);
		return true;
	}
	
	public void update(long deltaTime) {
		super.update(deltaTime);
		
		if(doodle != null) {
			doodle.setVelocityY(0);
			doodle.setX(getX()+(getWidth()-doodle.getWidth())/2);
			doodle.setY(getY()+getHeight()-doodle.getHeight()-((time/2e7)));
			time += deltaTime;
			
			if(time >= 1e9)
				doodle.setHit(true);
		}
	}
}