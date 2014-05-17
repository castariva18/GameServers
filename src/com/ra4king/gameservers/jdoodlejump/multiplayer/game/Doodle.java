package com.ra4king.gameservers.jdoodlejump.multiplayer.game;


import java.awt.geom.Rectangle2D;

import com.ra4king.gameservers.jdoodlejump.multiplayer.game.bars.Bar;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.monsters.Monster;


public strictfp class Doodle extends GameComponent {
	private Rectangle2D.Double feetBounds;
	private int intersectionDist = 1;
	private final double defaultMaxVY = 600;
	private double maxVY = defaultMaxVY;
	private double vy;
	private double g = 1000;
	private boolean canShoot = true;
	private boolean isDying;
	private boolean isHit;
	private boolean isShooting;
	private boolean isInvincible;
	private boolean isInvisible;
	private boolean isFacingRight = true;
	
	private double yOffset;
	
	public Doodle() {
		setWidth(30);
		setHeight(42);
		vy = maxVY;
	}
	
	public int getID() {
		return 0;
	}
	
	public void setFacingRight(boolean isFacingRight) {
		this.isFacingRight = isFacingRight;
	}
	
	public boolean isFacingRight() {
		return isFacingRight;
	}
	
	public double getMaxVelocityY() {
		return maxVY;
	}
	
	public void setMaxVelocityY(double dVY) {
		this.maxVY = dVY;
	}
	
	public void resetMaxVelocityY() {
		maxVY = defaultMaxVY;
	}
	
	public double getDefaultMaxVelocityY() {
		return defaultMaxVY;
	}
	
	public double getVelocityY() {
		return vy;
	}
	
	public void setVelocityY(double vy) {
		this.vy = vy;
	}
	
	public void setDying(boolean isDying) {
		this.isDying = isDying;
	}
	
	public boolean isDying() {
		return isDying;
	}
	
	public void setHit(boolean isHit) {
		this.isHit = isHit;
	}
	
	public boolean isHit() {
		return isHit;
	}
	
	public void setShooting(boolean shooting) {
		isShooting = shooting;
	}
	
	public boolean isShooting() {
		return isShooting;
	}
	
	public void setAbleToShoot(boolean shoot) {
		canShoot = shoot;
	}
	
	public boolean isAbleToShoot() {
		return canShoot && !isDying;
	}
	
	public void setInvincible(boolean inv) {
		isInvincible = inv;
	}
	
	public boolean isInvincible() {
		return isInvincible;
	}
	
	public void setInvisible(boolean invisible) {
		isInvisible = invisible;
	}
	
	public boolean isInvisible() {
		return isInvisible;
	}
	
	public void setIntersection(int dist) {
		intersectionDist = dist;
	}
	
	public int getIntersection() {
		return intersectionDist;
	}
	
	public Rectangle2D.Double getBounds() {
		Rectangle2D.Double bounds = super.getBounds();
		bounds.setFrame(getX()+5,getY()+8,getWidth()-10,getHeight()-15);
		return bounds;
	}
	
	private Rectangle2D.Double getFeetBounds() {
		if(feetBounds == null)
			feetBounds = new Rectangle2D.Double();
		feetBounds.setFrame(getX()+5,getY()+getHeight()-intersectionDist-1,getWidth()-10,intersectionDist);
		return feetBounds;
	}
	
	public void update(long deltaTime) {
		try {
			if(!isDying) {
				//if(getY()+getHeight()/2+yOffset > 500) {
				//	isDying = true;
				//	return;
				//}
				
				boolean hasJumped = false;
				
				for(GameComponent e : getParent().getEntities()) {
					if(e == null)
						continue;
					
					if(!(e instanceof Doodle) && e.getBounds().intersects(getFeetBounds())) {
						if(e instanceof Bar && vy < 10) {
							((Bar)e).jump(this);
							
							hasJumped = true;
						}
						else if(e instanceof Monster && ((Monster)e).isJumpable() && ((Monster)e).getJumpBounds().intersects(getFeetBounds())) {
							if(vy < maxVY+300) {
								vy = maxVY+300;
								setY(e.getY()-getHeight());
								
								hasJumped = true;
							}
							else if(isInvincible()) {
								getParent().remove(e);
								hasJumped = true;
							}
						}
					}
					
					if(e instanceof Monster && getBounds().intersects(((Monster)e).getHitBounds()) && !hasJumped && !isInvincible) {
						if(!((Monster)e).kill(this)) {
							isDying = true;
							vy = 0;
							return;
						}
					}
				}
			}
		}
		finally {
			vy -= (g*(deltaTime/1000000000.0));
			
			if(vy < -maxVY) vy = -maxVY;
			
			setY(getY()-(vy*(deltaTime/1000000000.0)));
			
			if(getY()+yOffset < 250)
				yOffset = 250-getY();
		}
	}
}