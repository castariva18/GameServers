package com.ra4king.gameservers.jdoodlejump.multiplayer.game.bars;

import com.ra4king.gameservers.jdoodlejump.multiplayer.game.Doodle;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.GameComponent;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.GameWorld;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.monsters.StationaryMonster;

public abstract class Bar extends GameComponent {
	private StationaryMonster monster;
	
	public void init(GameWorld screen) {
		super.init(screen);
		
		setWidth(50);
		setHeight(13);
	}
	
	public void installMonster(StationaryMonster m) {
		m.setX(getX()+(getWidth()-m.getWidth())/2);
		m.setY(getY()-m.getHeight()-5);
		m.setBar(this);
		
		monster = m;
	}
	
	public boolean isMonsterInstalled() {
		return monster == null ? false : true;
	}
	
	public void uninstallMonster() {
		monster.setBar(null);
		monster = null;
	}
	
	protected void finalize() {
		monster.setBar(null);
		monster = null;
	}
	
	public void jump(Doodle doodle) {
		doodle.setVelocityY(doodle.getMaxVelocityY());
		doodle.setY(getY()-doodle.getHeight());
	}
	
	public boolean playsDefaultSound() {
		return true;
	}
	
	public void update(long deltaTime) {
		if(getY() > getParent().getHeight())
			getParent().remove(this);
	}
}