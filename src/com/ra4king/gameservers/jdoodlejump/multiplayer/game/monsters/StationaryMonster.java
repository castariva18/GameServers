package com.ra4king.gameservers.jdoodlejump.multiplayer.game.monsters;

import java.awt.geom.Rectangle2D;

import com.ra4king.gameservers.jdoodlejump.multiplayer.game.Doodle;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.bars.Bar;

public class StationaryMonster extends Monster {
	private Bar bar;
	
	public StationaryMonster(int num, int hitsTotal) {
		super(hitsTotal,num);
	}
	
	public int getID() {
		return 5;
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
		
		if(getHitsCount() >= getHitsTotal()) {
			bar.uninstallMonster();
			getParent().remove(this);
		}
	}
	
	public void fall() {
		if(getBar() != null)
			getBar().uninstallMonster();
		super.fall();
	}
	
	public boolean kill(Doodle doodle) {
		return false;
	}
	
	public void setBar(Bar bar) {
		this.bar = bar;
	}
	
	public Bar getBar() {
		return bar;
	}
	
	protected void finalize() {
		bar = null;
	}
}