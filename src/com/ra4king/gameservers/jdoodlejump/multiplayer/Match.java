package com.ra4king.gameservers.jdoodlejump.multiplayer;

import java.awt.geom.Point2D;

import com.ra4king.gameservers.jdoodlejump.multiplayer.game.Bullet;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.Doodle;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.GameComponent;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.GameWorld;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.PlatformGenerator;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.bars.MovingBar;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.monsters.Monster;
import com.ra4king.gameutils.networking.Packet;
import com.ra4king.gameutils.networking.SocketPacketIO;

public strictfp class Match implements Runnable {
	private String s1, s2;
	private SocketPacketIO p1, p2;
	
	private final double doodleSpeed = 400;
	
	static {
		new Thread() {
			{
				setDaemon(true);
				start();
			}
			
			public void run() {
				while(true) {
					try {
						Thread.sleep(Long.MAX_VALUE);
					}
					catch(Exception exc) {}
				}
			}
		};
	}
	
	public Match(String s1, SocketPacketIO p1, String s2, SocketPacketIO p2) {
		this.s1 = s1;
		this.p1 = p1;
		
		this.s2 = s2;
		this.p2 = p2;
	}
	
	public void run() {
		try {
			p1.setBlocking(false);
			p2.setBlocking(false);
			
			GameWorld gameWorld1 = new GameWorld();
			GameWorld gameWorld2 = new GameWorld();
			
			PlatformGenerator gen = new PlatformGenerator(gameWorld1,gameWorld2);
			for(int a = 0; a < 4; a++)
				gen.loadWorld(a, 3000);
			
			gameWorld1.init();
			gameWorld1.show();
			
			gameWorld2.init();
			gameWorld2.show();
			
			Doodle d1 = new Doodle();
			Doodle d2 = new Doodle();
			gameWorld1.add(d1);
			gameWorld2.add(d2);
			d1.setLocation(150,450);
			d2.setLocation(350,450);
			
			Packet pt1 = new Packet();
			pt1.writeDouble(d1.getX(),d1.getY(),d2.getX(),d2.getY());
			sendGameWorld(gameWorld1,pt1);
			
			Packet pt2 = new Packet();
			pt2.writeDouble(d2.getX(),d2.getY(),d1.getX(),d1.getY());
			sendGameWorld(gameWorld2,pt2);
			
			p1.write(pt1);
			p2.write(pt2);
			
			System.out.println("Game starting!");
			System.out.println("Player 1: " + d1.getX() + " " + d1.getY() + " at " + d1.getVelocityY());
			System.out.println("Player 2: " + d2.getX() + " " + d2.getY() + " at " + d2.getVelocityY());
			
			Bullet lastBullet = null;
			
			while(true) {
				Packet p;
				
				if((p = p1.read()) != null) {
					byte b = p.readByte();
					
					if(b == (byte)-1) {
						gameOver(-1);
						return;
					}
					
					if(b == 0) {
						long deltaTime = p.readLong();
						
						int dir = p.readInt();
						
						if(dir == -1) {
							d1.setX(d1.getX()-(doodleSpeed*(deltaTime/1e9)));
							d1.setFacingRight(false);
						}
						else if(dir == 1) {
							d1.setX(d1.getX()+(doodleSpeed*(deltaTime/1e9)));
							d1.setFacingRight(true);
						}
						
						if(d1.getX()+d1.getWidth()/2 > 500)
							d1.setX(-d1.getWidth()/2);
						else if(d1.getX()+d1.getWidth()/2 < 0)
							d1.setX(500-d1.getWidth()/2);
						
						Bullet toSend = lastBullet;
						lastBullet = null;
						
						if(p.readInt() == 1) {
							double dx = p.readDouble();
							double dy = p.readDouble();
							double mx = p.readDouble();
							double my = p.readDouble();
							lastBullet = (Bullet)gameWorld1.add(new Bullet(new Point2D.Double(dx,dy),new Point2D.Double(mx,my)));
							gameWorld2.add(new Bullet(new Point2D.Double(dx,dy),new Point2D.Double(mx,my)));
						}
						
						//if(deltaTime > (long)(1e9/60))
						//	JOptionPane.showMessageDialog(null, "DELTATIME > 1e9/60!");
						
						gameWorld1.update(deltaTime);
						
						Packet packet = new Packet();
						packet.writeInt(0);
						packet.writeDouble(d2.getX(),d2.getY());
						packet.writeBoolean(d2.isFacingRight());
						
						if(toSend != null) {
							packet.writeInt(1);
							Point2D.Double d = toSend.getDoodlePoint();
							packet.writeDouble(d.x);
							packet.writeDouble(d.y);
							Point2D.Double m = toSend.getMousePoint();
							packet.writeDouble(m.x);
							packet.writeDouble(m.y);
							
							lastBullet = null;
						}
						else
							packet.writeInt(0);
						
						p1.write(packet);
					}
					else
						throw new RuntimeException("Value from client1: " + p.get(0));
				}
				
				if((p = p2.read()) != null) {
					byte b = p.readByte();
					
					if(b == (byte)-1) {
						gameOver(-1);
						return;
					}
					
					if(b == 0) {
						long deltaTime = p.readLong();
						
						int dir = p.readInt();
						
						if(dir == -1) {
							d2.setX(d2.getX()-(doodleSpeed*(deltaTime/1e9)));
							d2.setFacingRight(false);
						}
						else if(dir == 1) {
							d2.setX(d2.getX()+(doodleSpeed*(deltaTime/1e9)));
							d2.setFacingRight(true);
						}
						
						if(d2.getX()+d2.getWidth()/2 > 500)
							d2.setX(-d2.getWidth()/2);
						else if(d2.getX()+d2.getWidth()/2 < 0)
							d2.setX(500-d2.getWidth()/2);
						
						Bullet toSend = lastBullet;
						lastBullet = null;
						
						if(p.readInt() == 1) {
							double dx = p.readDouble();
							double dy = p.readDouble();
							double mx = p.readDouble();
							double my = p.readDouble();
							lastBullet = (Bullet)gameWorld2.add(new Bullet(new Point2D.Double(dx,dy),new Point2D.Double(mx,my)));
							gameWorld1.add(new Bullet(new Point2D.Double(dx,dy),new Point2D.Double(mx,my)));
						}
						
						//if(deltaTime > (long)(1e9/60))
						//	JOptionPane.showMessageDialog(null, "DELTATIME > 1e9/60!");
						
						gameWorld2.update(deltaTime);
						
						Packet packet = new Packet();
						packet.writeInt(0);
						packet.writeDouble(0,d1.getX(),d1.getY());
						packet.writeBoolean(d1.isFacingRight());
						
						if(toSend != null) {
							packet.writeInt(1);
							Point2D.Double d = toSend.getDoodlePoint();
							packet.writeDouble(d.x);
							packet.writeDouble(d.y);
							Point2D.Double m = toSend.getMousePoint();
							packet.writeDouble(m.x);
							packet.writeDouble(m.y);
							
							lastBullet = null;
						}
						else
							packet.writeInt(0);
						
						p2.write(packet);
					}
					else
						throw new RuntimeException("Value from client2: " + p.get(0));
				}
				
				if(d1.getY() < -11500) {
					gameOver(1);
					return;
				}
				else if(d2.getY() < -11500) {
					gameOver(2);
					return;
				}
				
				try {
					//Thread.sleep(5);
				}
				catch(Exception exc) {}
			}
		}
		catch(Exception exc) {
			exc.printStackTrace();
			gameOver(-1);
			return;
		}
		finally {
			System.out.println(s1 + " v " + s2 + ": Terminated");
		}
	}
	
	public void gameOver(int status) {
		Packet p = new Packet();
		p.writeInt(-1);
		
		if(status == 1) {
			System.out.println(s1 + " v " + s2 + ": " + s1 + " won!");
			p.writeInt(1);
		}
		else if(status == 2) {
			System.out.println(s1 + " v " + s2 + ": " + s2 + " won!");
			p.writeInt(2);
		}
		else
			System.out.println("No one won!");
		
		try {
			p1.write(p);
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
		
		p.reset();
		
		try {
			p2.write(p);
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	private void sendGameWorld(GameWorld gameWorld, Packet p) {
		for(byte a = 0; a < gameWorld.getZIndexSize(); a++) {
			for(GameComponent c : gameWorld.getEntitiesAt(a)) {
				if(c instanceof Doodle)
					continue;
				p.writeByte(a);
				p.writeByte((byte)c.getID());
				
				if(c instanceof Monster) {
					p.writeByte((byte)((Monster)c).getMonsterNum());
					p.writeByte((byte)((Monster)c).getHitsTotal());
				}
				
				p.writeDouble(c.getX());
				p.writeDouble(c.getY());
				
				if(c instanceof MovingBar) {
					p.writeDouble(((MovingBar)c).startingDistance());
					p.writeBoolean(((MovingBar)c).isHoriz());
				}
			}
		}
	}
}
