package com.ra4king.gameservers.jdoodlejump.multiplayer.game.bars;

import java.awt.geom.Rectangle2D;

public class MovingBar extends Bar {
	private double vx, vy, curDist;
	private final double totalDist;
	private final boolean isHoriz;
	private boolean hasUpdated = false;
	
	public MovingBar(double x, double y, double startingDist, boolean isHoriz) {
		setX(x);
		setY(y);
		
		this.isHoriz = isHoriz;
		
		if(isHoriz) {
			vx = 100;
			totalDist = 450;
			curDist = startingDist;
		}
		else {
			vy = 100;
			totalDist = 400;
			curDist = startingDist;
		}
	}
	
	public int getID() {
		return 2;
	}
	
	public double startingDistance() {
		return curDist;
	}
	
	public boolean isHoriz() {
		return isHoriz;
	}
	
	public Rectangle2D.Double getBounds() {
		Rectangle2D.Double bounds = super.getBounds();;
		
		if(!hasUpdated) {
			if(isHoriz)
				bounds.setFrame(0,getY(),getParent().getWidth(),getHeight());
			else
				bounds.setFrame(getX(),getY()-curDist,getWidth(),totalDist);
		}
		
		return bounds;
	}
	
	public void update(long deltaTime) {
		super.update(deltaTime);
		
		if(!hasUpdated)
			hasUpdated = true;
		
		if(isHoriz) {
			setX(getX()+vx*(deltaTime/1000000000.0));
			
			if(getX() < 0.0) {
				setX(0);
				vx = -vx;
			}
			else if(getX()+getWidth() > getParent().getWidth()) {
				setX(getParent().getWidth()-getWidth());
				vx = -vx;
			}
		}
		else {
			double dist = getY();
			setY(getY()+vy*(deltaTime/1000000000.0));
			curDist += getY()-dist;
			
			if(curDist > totalDist) {
				curDist = totalDist;
				vy = -vy;
			}
			else if(curDist < 0) {
				curDist = 0;
				vy = -vy;
			}
		}
	}
}