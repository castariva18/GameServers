package com.ra4king.gameservers.jdoodlejump.multiplayer.game;

import java.util.ArrayList;

import com.ra4king.gameservers.jdoodlejump.multiplayer.game.util.Bag;

/**
 * A GameWorld is a container of Entities. It has a z-buffer that goes in back-to-front order, 0 being the back.
 * @author Roi Atalla
 */
public class GameWorld {
	private ArrayList<Bag<GameComponent>> entities;
	private ArrayList<Temp> temp;
	private boolean hasInited, hasShown;
	private volatile boolean isLooping;
	
	/**
	 * Initializes this object.
	 */
	public GameWorld() {
		entities = new ArrayList<Bag<GameComponent>>();
		entities.add(new Bag<GameComponent>());
		
		temp = new ArrayList<Temp>();
	}
	
	public void init() {
		preLoop();
		
		try{
			for(Bag<GameComponent> b : entities)
				for(GameComponent e : b)
					if(e != null)
						e.init(this);
		}
		finally {
			postLoop();
			hasInited = true;
		}
	}
	
	/**
	 * Calls each Entity's <code>show()</code> method in z-index order.
	 */
	public synchronized void show() {
		hasShown = true;
		
		preLoop();
		
		try{
			for(Bag<GameComponent> b : entities)
				for(GameComponent e : b)
					if(e != null)
						e.show();
		}
		finally {
			postLoop();
		}
	}
	
	/**
	 * Calls each Entity's <code>hide()</code> method in z-index order.
	 */
	public synchronized void hide() {
		hasShown = false;
		
		preLoop();
		
		try{
			for(Bag<GameComponent> b : entities)
				for(GameComponent e : b)
					if(e != null)
						e.hide();
		}
		finally {
			postLoop();
		}
	}
	
	public synchronized void paused() {
		preLoop();
		
		try{
			for(Bag<GameComponent> b : entities)
				for(GameComponent e : b)
					if(e != null)
						e.paused();
		}
		finally {
			postLoop();
		}
	}
	
	public synchronized void resumed() {
		preLoop();
		
		try{
			for(Bag<GameComponent> b : entities)
				for(GameComponent e : b)
					if(e != null)
						e.resumed();
		}
		finally {
			postLoop();
		}
	}
	
	public synchronized void resized(int width, int height) {}
	
	/**
	 * Calls each Entity's <code>update(long)</code> method in z-index order.
	 * @param deltaTime The time passed since the last call to it.
	 */
	public synchronized void update(long deltaTime) {
		preLoop();
		
		try {
			for(Bag<GameComponent> b : entities)
				for(GameComponent e : b)
					if(e != null)
						try{
							e.update(deltaTime);
						}
						catch(Exception exc) {
							exc.printStackTrace();
						}
		}
		finally {
			postLoop();
		}
	}
	
	/**
	 * Adds the Entity with a z-index of 0.
	 * @param e The Entity to be added.
	 * @return The Entity that was added.
	 */
	public synchronized GameComponent add(GameComponent e) {
		return add(0,e);
	}
	
	/**
	 * Adds the Entity with the specified z-index.
	 * @param e The Entity to be added.
	 * @param zindex The z-index of this Entity.
	 * @return The Entity that was added.
	 */
	public synchronized GameComponent add(int zindex, GameComponent e) {
		if(isLooping) {
			temp.add(new Temp(zindex,e));
		}
		else {
			while(zindex >= entities.size())
				entities.add(new Bag<GameComponent>());
			
			entities.get(zindex).add(e);
			
			if(hasInited)
				e.init(this);
			
			if(hasShown)
				e.show();
		}
		
		return e;
	}
	
	/**
	 * Returns true if this GameWorld contains this Entity.
	 * @param e The Entity to search for.
	 * @return True if this GameWorld contains this Entity, false otherwise.
	 */
	public boolean contains(GameComponent e) {
		if(isLooping && temp.contains(e))
			return true;
		return getEntities().contains(e);
	}
	
	public boolean replace(GameComponent old, GameComponent e) {
		if(isLooping) {
			int i = temp.indexOf(old);
			if(i >= 0) {
				temp.get(i).e = e;
				return true;
			}
		}
		
		int zindex = getZIndex(old);
		if(zindex < 0)
			return false;
		
		boolean isNew = getZIndex(e) < 0;
		
		remove(e);
		
		Bag<GameComponent> bag = entities.get(zindex);
		bag.set(bag.indexOf(old),e);
		
		if(isNew) {
			e.init(this);
			e.show();
		}
		
		return true;
	}
	
	/**
	 * Removes the Entity from the world.
	 * @param e The Entity to remove.
	 * @return True if the Entity was found and removed, false if the Entity was not found.
	 */
	public synchronized boolean remove(GameComponent e) {
		boolean removed = false;
		
		for(Bag<GameComponent> bag : entities)
			removed |= bag.remove(e);
		
		if(removed)
			e.hide();
		
		return removed;
	}
	
	/**
	 * Clears this game world.
	 */
	public synchronized void clear() {
		entities.clear();
		temp.clear();
		
		System.gc();
		
		entities.add(new Bag<GameComponent>());
	}
	
	/**
	 * Changes the z-index of the specified Entity.
	 * @param e The Entity whose z-index is changed.
	 * @param newZIndex The new z-index
	 * @return True if the Entity was found and updated, false otherwise.
	 */
	public synchronized boolean changeZIndex(GameComponent e, int newZIndex) {
		if(isLooping) {
			int i = temp.indexOf(e);
			if(i >= 0) {
				temp.get(i).zIndex = newZIndex;
				return true;
			}
		}
		
		if(!remove(e))
			return false;
		
		add(newZIndex,e);
		
		e.show();
		
		return true;
	}
	
	/**
	 * Returns the z-index of the specified Entity.
	 * @param e The Entity who's index is returned.
	 * @return The z-index of the specified Entity, or -1 if the Entity was not found.
	 */
	public synchronized int getZIndex(GameComponent e) {
		if(isLooping) {
			int i = temp.indexOf(e);
			if(i >= 0)
				return temp.get(i).zIndex;
		}
		
		for(int a = 0; a < entities.size(); a++)
			if(entities.get(a).indexOf(e) >= 0)
				return a;
		return -1;
	}
	
	/**
	 * Returns true if the specified z-index exists.
	 * @param zindex The z-index to check.
	 * @return True if the specified z-index exists, false otherwise.
	 */
	public synchronized boolean containsZIndex(int zindex) {
		return zindex < entities.size();
	}
	
	/**
	 * A list of all Entities at the specified z-index.
	 * @param zindex The z-index.
	 * @return A list of all Entities at the specified z-index.
	 */
	public synchronized ArrayList<GameComponent> getEntitiesAt(int zindex) {
		return entities.get(zindex);
	}
	
	/**
	 * A list of all Entities in this entire world.
	 * @return A list of all Entities in this world in z-index order.
	 */
	public synchronized ArrayList<GameComponent> getEntities() {
		ArrayList<GameComponent> allEntities = new ArrayList<GameComponent>();
		
		for(Bag<GameComponent> bag : entities)
			allEntities.addAll(bag);
		
		return allEntities;
	}
	
	public int getZIndexSize() {
		return entities.size();
	}
	
	/**
	 * @return The total number of Entities in this world.
	 */
	public synchronized int size() {
		return getEntities().size();
	}
	
	/**
	 * This calls the parent's getWidth() method.
	 * @return The width of this world.
	 */
	public int getWidth() {
		return 500;
	}
	
	/**
	 * This calls the parent's getHeight() method.
	 * @return The height of this world.
	 */
	public int getHeight() {
		return 500;
	}
	
	public void preLoop() {
		if(isLooping)
			return;
		
		temp.clear();
		isLooping = true;
	}
	
	public void postLoop() {
		if(!isLooping)
			return;
		
		isLooping = false;
		
		for(Temp p : temp)
			add(p.zIndex,p.e);
		
		temp.clear();
		
		for(Bag<GameComponent> bag : entities)
			bag.remove(null);
	}
	
	private class Temp {
		private GameComponent e;
		private int zIndex;
		
		Temp(int zIndex, GameComponent e) {
			this.zIndex = zIndex;
			this.e = e;
		}
	}
}