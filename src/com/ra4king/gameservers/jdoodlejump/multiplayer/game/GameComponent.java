package com.ra4king.gameservers.jdoodlejump.multiplayer.game;

import java.awt.geom.Rectangle2D;

/**
 * All entities that are added to GameWorld must extend this class.
 * @author Roi Atalla
 */
public abstract class GameComponent {
	private GameWorld parent;
	private Rectangle2D.Double bounds;
	private double x, y, width, height;
	
	/**
	 * Sets the X, Y, width, and height to 0.
	 */
	public GameComponent() {
		this(0,0,0,0);
	}
	
	/**
	 * Sets the X, Y, width, and height.
	 * @param x The X position.
	 * @param y The Y position.
	 * @param w The width.
	 * @param h The height.
	 */
	public GameComponent(double x, double y, double w, double h) {
		bounds = new Rectangle2D.Double(x,y,w,h);
		
		this.x = x;
		this.y = y;
		width = w;
		height = h;
	}
	
	public abstract int getID();
	
	public void init(GameWorld screen) {
		parent = screen;
	}
	
	/**
	 * @return The parent of this game component.
	 */
	public GameWorld getParent() {
		return parent;
	}
	
	/**
	 * @return The leftmost X position.
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * @return The nearest integer of the leftmost X position.
	 */
	public int getIntX() {
		return (int)Math.round(getX());
	}
	
	/**
	 * Sets the leftmost X position.
	 * @param x The new X value.
	 */
	public void setX(double x) {
		this.x = x;
	}
	
	/**
	 * @return The topmost Y position.
	 */
	public double getY() {
		return y;
	}
	
	/**
	 * @return The nearest integer of the topmost Y position.
	 */
	public int getIntY() {
		return (int)Math.round(getY());
	}
	
	/**
	 * Sets the topmost Y position.
	 * @param y The new Y value.
	 */
	public void setY(double y) {
		this.y = y;
	}
	
	/**
	 * @return The width of this game component.
	 */
	public double getWidth() {
		return width;
	}
	
	public int getIntWidth() {
		return (int)Math.round(getWidth());
	}
	
	/**
	 * Sets the width of this game component
	 * @param width The new width.
	 */
	public void setWidth(double width) {
		this.width = width;
	}
	
	/**
	 * @return The height of this game component.
	 */
	public double getHeight() {
		return height;
	}
	
	public int getIntHeight() {
		return (int)Math.round(getHeight());
	}
	
	/**
	 * Sets the height of this game component.
	 * @param height The new height.
	 */
	public void setHeight(double height) {
		this.height = height;
	}
	
	/**
	 * Returns the center X position.
	 * @return The center X position.
	 */
	public double getCenterX() {
		return getX()+getWidth()/2;
	}
	
	/**
	 * Returns the nearest integer of the center X position.
	 * @return The nearest integer of the center X position.
	 */
	public int getIntCenterX() {
		return (int)Math.round(getCenterX());
	}
	
	/**
	 * Returns the center Y position.
	 * @return The center Y position.
	 */
	public double getCenterY() {
		return getY()+getHeight()/2;
	}
	
	/**
	 * Returns the nearest integer of the center Y position.
	 * @return The nearest integer of the center Y position.
	 */
	public int getIntCenterY() {
		return (int)Math.round(getCenterY());
	}
	
	/**
	 * @return The bounds of this game component.
	 */
	public Rectangle2D.Double getBounds() {
		bounds.setFrame(x,y,width,height);
		return bounds;
	}
	
	public void setBounds(double x, double y, double width, double height) {
		setLocation(x,y);
		setSize(width,height);
	}
	
	public void setLocation(double x, double y) {
		setX(x);
		setY(y);
	}
	
	public void setSize(double width, double height) {
		setWidth(width);
		setHeight(height);
	}
	
	public void translate(double x, double y) {
		setX(getX()+x);
		setY(getY()+y);
	}
	
	public boolean contains(double x, double y) {
		return getBounds().contains(x,y);
	}
	
	public boolean intersects(Rectangle2D.Double r) {
		return getBounds().intersects(r);
	}
	
	public boolean intersects(double x, double y, double width, double height) {
		return getBounds().intersects(x, y, width, height);
	}
	
	public void show() {}
	
	public void hide() {}
	
	public void paused() {}
	
	public void resumed() {}
	
	/**
	 * Called by the parent a set number of times a second.
	 * @param deltaTime The time passed since the last call to it.
	 */
	public abstract void update(long deltaTime);
}