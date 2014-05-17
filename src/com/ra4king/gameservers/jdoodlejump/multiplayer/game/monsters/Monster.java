package com.ra4king.gameservers.jdoodlejump.multiplayer.game.monsters;

import java.awt.geom.Rectangle2D;

import com.ra4king.gameservers.jdoodlejump.multiplayer.game.Doodle;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.GameComponent;

public abstract class Monster extends GameComponent {
	private int hitsCount;
	private final int hitsTotal;
	private int isFalling = -1;
	private int monsterNum;
	
	public Monster(int hitsTotal, int num) {
		this.hitsTotal = hitsTotal;
		setMonster(num);
	}
	
	public int getMonsterNum() {
		return monsterNum;
	}
	
	public void setMonster(int num) {
		monsterNum = num;
		setSize(MonsterSize.getWidth(num),MonsterSize.getHeight(num));
	}
	
	public abstract boolean isJumpable();
	
	public abstract boolean isHittable();
	
	public abstract void hit();
	
	public void fall() {
		if(isJumpable())
			isFalling = 0;
	}
	
	public abstract boolean kill(Doodle doodle);
	
	public Rectangle2D.Double getBounds() {
		Rectangle2D.Double bounds = super.getBounds();
		bounds.setFrame(bounds.x+5,bounds.y+5,bounds.width-10,bounds.height-10);
		return bounds;
	}
	
	public Rectangle2D.Double getJumpBounds() {
		return getBounds();
	}
	
	public Rectangle2D.Double getHitBounds() {
		return getBounds();
	}
	
	public int getHitsTotal() {
		return hitsTotal;
	}
	
	public int getHitsCount() {
		return hitsCount;
	}
	
	public void addHit() {
		hitsCount++;
	}
	
	public void update(long deltaTime) {
		if(getY() > getParent().getHeight())
			getParent().remove(this);
		
		if(isFalling > -1) {
			setY(getY()+(600*(deltaTime/1000000000.0)));
			isFalling += (600*(deltaTime/1000000000.0));
			
			if(isFalling >= 80) {
				isFalling = -1;
			}
		}
	}
}