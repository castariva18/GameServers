package com.ra4king.gameservers.jdoodlejump.multiplayer.game;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import com.ra4king.gameservers.jdoodlejump.multiplayer.game.bars.Bar;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.bars.BreakingBar;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.bars.DisappearingBar;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.bars.MovingBar;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.bars.StationaryBar;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.monsters.AlienMonster;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.monsters.BlackHoleMonster;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.monsters.MonsterSize;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.monsters.MovingMonster;
import com.ra4king.gameservers.jdoodlejump.multiplayer.game.monsters.StationaryMonster;

public strictfp class PlatformGenerator {
	private ArrayList<GameWorld> worlds;
	private double highestBar;
	private int monsterNum = 6;
	
	public PlatformGenerator(GameWorld ... worlds) {
		this.worlds = new ArrayList<GameWorld>();
		for(GameWorld w : worlds)
			this.worlds.add(w);
	}
	
	public void reset() {
		highestBar = 0;
	}
	
	public void loadWorld(int level, int extent) {
		double prevHighestBar;
		if(highestBar == 0) {
			System.out.println("LOADING BARS!!");
			for(int a = 0; a < 10; a++) {
				for(GameWorld w : worlds)
					w.add(new StationaryBar(a * 50, 480));
			}
			highestBar = worlds.get(0).getHeight()-20;
			prevHighestBar = worlds.get(0).getHeight()-20;
		}
		else
			prevHighestBar = highestBar;
		
		long time = System.currentTimeMillis();
		int bar = 0;
		
		for(;highestBar > prevHighestBar-extent; bar++) {
			double x, y;
			
			int random = (int)Math.round(Math.random()*100);
			
//			if(level > 0 && a < 2 && random > 90 && doodle.getVelocityY() <= doodle.getDefaultMaxVelocityY()) {
//				PowerUp powerup = null;
//				
//				switch((int)(Math.random()*powerupNum)) {
//					default:
//						powerup = new CopterHatPowerUp();
//						break;
//					case 1:
//						powerup = new RocketPowerUp();
//						break;
//					case 2:
//						powerup = new SpringShoesPowerUp();
//						break;
//					case 3:
//						powerup = new ShieldPowerUp();
//				}
//				
//				x = Math.random()*(gameWorld.getWidth()-50);
//				y = highestBar - (Math.random()*(level*3) + 50);
//				
//				gameWorld.add(4,powerup);
//				((Bar)gameWorld.add(new StationaryBar(x,y))).installPowerUp(powerup);
//				
//				y -= powerup.getHeight();
//				
//				a++;
//			}
//			else
			if((level > 3 && random > 30 && random < 50) || (level > 0 && level%7 == 0 && level%8 == 0 && random > 50)) {
				x = (Math.random()*(worlds.get(0).getWidth()-50));
				y = highestBar - (Math.random()*(level*3) + 50);
				
				MovingBar b = new MovingBar(x,y,x,true);
				for(GameWorld w : worlds)
					w.add(b);
			}
			else {
				x = Math.random()*(worlds.get(0).getWidth()-50);
				y = highestBar - (Math.random()*(level*3) + 50);
				
				double random2 = Math.random()*100;
				if(random2 > 80 && level > 2) {
					DisappearingBar b = new DisappearingBar(x,y);
					for(GameWorld w : worlds)
						w.add(b);
				}
				else {
					StationaryBar b = new StationaryBar(x,y); 
					for(GameWorld w : worlds)
						w.add(b);
				}
			}
			
			highestBar = y;
		}
		
		long dif = System.currentTimeMillis()-time;
		System.out.println((bar--) + " linear bars\t" + dif + " milliseconds");
		
		time = System.currentTimeMillis();
		
		double length = -(highestBar-prevHighestBar);
		int tries = 5;
		bar = 0;
		
		for(int a = 0; a < 25-(level/2); a++) {
			double x, y;
			
			if(a%10 == 0 && level > 5 && Math.random() < 0.25) {// && doodle.getVelocityY() <= doodle.getDefaultMaxVelocityY()) {
				int b = Math.random()*100 > 50 ? 7 : 8;
				
				boolean tooclose;
				int count = 0;
				do{
					x = Math.random()*(worlds.get(0).getWidth()-MonsterSize.getWidth(b));
					y = prevHighestBar - (Math.random()*length);
					tooclose = false;
					
					count++;
					if(count >= tries)
						break;
					
					for(GameComponent e : worlds.get(0).getEntities()) {
						if(e.getBounds().intersects(new Rectangle2D.Double(x,y,MonsterSize.getWidth(b),MonsterSize.getHeight(b)))) {
							tooclose = true;
							break;
						}
					}
				}while(tooclose);
				
				if(count < tries) {
					bar++;
					if(b == 8) {
						BlackHoleMonster m = new BlackHoleMonster(x,y);
						for(GameWorld w : worlds)
							w.add(1,m);
					}
					else {
						AlienMonster m = new AlienMonster(x,y);
						for(GameWorld w : worlds)
							w.add(1,m);
					}
				}
			}
			else if(a%4 == 0 && level > 0) {// && doodle.getVelocityY() <= doodle.getDefaultMaxVelocityY()) {
				int b = (int)(Math.random()*monsterNum)+1;
				
				boolean tooclose;
				int count = 0;
				do{
					x = Math.random()*(worlds.get(0).getWidth()-50);
					y = prevHighestBar - (Math.random()*length);
					tooclose = false;
					
					count++;
					if(count >= tries)
						break;
					
					for(GameComponent e : worlds.get(0).getEntities()) {
						if(e.getBounds().intersects(new Rectangle2D.Double(x-20,y-10-MonsterSize.getWidth(b),90,30+MonsterSize.getHeight(b)))) {
							tooclose = true;
							break;
						}
					}
				}while(tooclose);
				
				if(count < tries) {
					bar++;
					int hitsTotal = (int)Math.round(Math.random()*(level/10)+1);
					
					StationaryMonster m = new StationaryMonster(b,hitsTotal);
					StationaryBar s = new StationaryBar(x,y);
					for(GameWorld w : worlds) {
						w.add(1,m);
						((Bar)w.add(s)).installMonster(m);
					}
				}
			}
			else if(((level > 5 && a%3 == 0) || (level > 0 && level%8 == 0 && level%9 == 0 && a%2 == 0))) {// && doodle.getVelocityY() <= doodle.getDefaultMaxVelocityY()) {
				int b = (int)(Math.random()*monsterNum)+1;
				
				boolean tooclose;
				int count = 0;
				do{
					x = Math.random()*(worlds.get(0).getWidth()-MonsterSize.getWidth(b));
					y = prevHighestBar - (Math.random()*length);
					tooclose = false;
					
					count++;
					if(count >= tries)
						break;
					
					for(GameComponent e : worlds.get(0).getEntities()) {
						if(e.getBounds().intersects(new Rectangle2D.Double(0,y,worlds.get(0).getWidth(),MonsterSize.getHeight(b)))) {
							tooclose = true;
							break;
						}
					}
				}while(tooclose);
				
				if(count < tries) {
					bar++;
					int hitsTotal = (int)Math.round(Math.random()*(level/10)+1);
					
					MovingMonster m = new MovingMonster(b,x,y,hitsTotal);
					for(GameWorld w : worlds)
						w.add(1,m);
				}
			}
			else if((level > 3 && (a%2 == 0 || a%5 == 0)) || (level > 0 && level%7 == 0 && level%8 == 0 && (a%2 == 0 || a%5 == 0))) {
				double startDist;
				
				boolean tooclose;
				int count = 0;
				do{
					x = Math.random()*(worlds.get(0).getWidth()-50);
					y = prevHighestBar - (Math.random()*(length-300));
					tooclose = false;
					
					startDist = Math.random()*400;
					
					count++;
					if(count >= tries)
						break;
					
					for(GameComponent e : worlds.get(0).getEntities()) {
						if(e.getBounds().intersects(new Rectangle2D.Double(x,y-startDist,50,410))) {
							tooclose = true;
							break;
						}
					}
				}while(tooclose);
				
				if(count < tries) {
					bar++;
					
					MovingBar b = new MovingBar(x,y,startDist,false);
					for(GameWorld w : worlds)
						w.add(b);
				}
			}
			else {
				boolean tooclose;
				int count = 0;
				do{
					x = Math.random()*(worlds.get(0).getWidth()-50);
					y = prevHighestBar - (Math.random()*length);
					tooclose = false;
					
					if(count >= tries)
						break;
					
					for(GameComponent e : worlds.get(0).getEntities()) {
						if(e.getBounds().intersects(new Rectangle2D.Double(x-20,y-20,90,50))) {
							tooclose = true;
							break;
						}
					}
				}while(tooclose);
				
				if(count < tries) {
					bar++;
					
					double random2 = Math.random()*100;
					if(random2 > 40) {
						StationaryBar b = new StationaryBar(x,y);
						for(GameWorld w : worlds)
							w.add(b);
					}
					else if(random2 > 30 && level > 2) {
						DisappearingBar b = new DisappearingBar(x,y);
						for(GameWorld w : worlds)
							w.add(b);
					}
					else {
						BreakingBar b = new BreakingBar(x,y);
						for(GameWorld w : worlds)
							w.add(b);
					}
				}
			}
		}
		
		dif = System.currentTimeMillis()-time;
		System.out.println(bar + " random bars          " + dif + " milliseconds");
	}
}
