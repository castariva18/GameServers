package com.ra4king.gameservers.jdoodlejump.multiplayer.game;

import java.awt.geom.Point2D;

import com.ra4king.gameservers.jdoodlejump.multiplayer.game.monsters.Monster;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.util.FastMath;

public class Bullet extends GameComponent {
	private Point2D.Double doodle, mouse;
	private double vx, vy;
	
	public Bullet(Point2D.Double doodle, Point2D.Double mouse) {
		setWidth(15);
		setHeight(15);
		
		this.doodle = doodle;
		this.mouse = mouse;
		
		double x = mouse.x-doodle.x;
		double y = Math.abs(-mouse.y+doodle.y);
		double angle = FastMath.atan2(y,x);
		
		vx = 1500 * FastMath.cos(angle);
		vy = 1500 * FastMath.sin(angle);
		
		setX(doodle.x-(getWidth()/2));
		setY(doodle.y);
	}
	
	public int getID() {
		return 13;
	}
	
	public Point2D.Double getDoodlePoint() {
		 return doodle;
	}
	
	public Point2D.Double getMousePoint() {
		return mouse;
	}
	
	public void update(long deltaTime) {
		if(getY()+getHeight() < 0 || getY() > getParent().getHeight()) {
			getParent().remove(this);
			return;
		}
		
		for(GameComponent e : getParent().getEntities()) {
			if(e == null) continue;
			
			if(e instanceof Monster && e.getBounds().intersects(getBounds()) && ((Monster)e).isHittable()) {
				((Monster)e).hit();
				
				getParent().remove(this);
				
				return;
			}
		}
		
		setX(getX()+(vx*(deltaTime/1000000000.0)));
		setY(getY()-(vy*(deltaTime/1000000000.0)));
	}
}