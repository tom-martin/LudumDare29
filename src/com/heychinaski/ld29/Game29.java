package com.heychinaski.ld29;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import com.heychinaski.engie.Entity;
import com.heychinaski.engie.Game;
import com.heychinaski.engie.camera.Camera;
import com.heychinaski.engie.camera.EntityTrackingCamera;
import com.heychinaski.engie.render.BackgroundTile;

public class Game29 extends Game {

	private static final long serialVersionUID = 1L;
	
	Camera camera = null;

	private World world;

	private Cart mainCart;

	private DebugDrawJava2D debug;

	private float time;
	float timeStep = 1.0f / 60.f;

	private boolean startScreen = true;
	private boolean endzone;
	
	List<Cart> otherCarts;

	private int score;

	private int numCarts;
	private int quota;

	private int levelIndex;

	private long startTime;

	private int timeAllowed;

	private boolean timeout;

	private long timeRemaining;

	private int totalScore;
	

	@Override
	public String[] images() {
		return new String[] {"wheel.png",
				             "minecart.png",
				             "bgTile.png",
				             "bgTile2.png",
				             "bgTile3.png",
				             "fgTile.png",
				             "fgTile2.png",
				             "fgTile3.png",
				             "sleeper.png",
				             "gem1.png",
				             "gem2.png",
				             "gem3.png",
				             "gem4.png",
				             "gem5.png",
				             "driver.png",
				             "gempile.png",
				             "alphabet.png",
				             "title.png"};
	}

	@Override
	public Camera camera() {
		return camera;
	}

	@Override
	public Image bgTileImage() {
		return imageManager.get("bgTile.png");
	}
	
	@Override
	public int bgTileScale() {
		return 4;
	}
	
	@Override
	public void init() {
		startScreen = true;
		totalScore = 0;
		init(Level.levels[(int) (Math.random()*Level.levels.length)]);
	}

	public void init(Level level) {
		this.bgTile = new BackgroundTile(imageManager.get(level.bgTile), bgTileScale());
		entities = new ArrayList<Entity>();
		endzone = false;
		score = 0;
		timeAllowed = level.time;
		timeRemaining = 0;
		timeout = false;
		Vec2 gravity = new Vec2(0.0f, -10.0f);
		world = new World(gravity);
		BufferedImage[] gems = new BufferedImage[] {imageManager.get("gem1.png"), imageManager.get("gem2.png"), imageManager.get("gem3.png"),imageManager.get("gem2.png"), imageManager.get("gem4.png"),imageManager.get("gem2.png"),imageManager.get("gem5.png"),imageManager.get("gem2.png")};
		Ground entity = new Ground(world, imageManager.get(level.fgTile), imageManager.get("sleeper.png"), level.difficulty, level.seed);
		entities.add(entity);
		
		GemPile gemPile = new GemPile(imageManager.get("gempile.png"), 6800, -50, world);
		entities.add(gemPile);
	    
		numCarts = level.numCarts;
		quota = level.quota;
		int x = -640 + (numCarts * 9);
		Image wheel = imageManager.get("wheel.png");
		Image minecartImg = imageManager.get("minecart.png");
		Image driverImg = imageManager.get("driver.png");
		mainCart = new Cart(world, x, -1, null, wheel, minecartImg, driverImg);
		entities.add(mainCart);
		
		otherCarts = new ArrayList<Cart>();
		Cart previousCart = mainCart;
		for(int i = 0; i < numCarts; i++) {
			x-= 9;
			Cart cart = new Cart(world, x, -i, previousCart.mainBody, wheel, minecartImg, null);
//			
//			if(i == (numCarts / 4)) {
//				camera = new EntityTrackingCamera(cart, this);
//			}
			
			int numRes = (int)(Math.random()*10)+5;
			for(int j = 0; j < numRes; j++) {
				Resource resource = new Resource(cart, gems[j % gems.length]);
				entities.add(resource);
			}
			entities.add(cart);
			
			previousCart = cart;
			otherCarts.add(cart);
		}
		
		if(camera == null) {
			camera = new EntityTrackingCamera(mainCart, this);
		} else {
			((EntityTrackingCamera)camera).toTrack = mainCart;
			entities.add(camera);
		}
		
		
		debug = new DebugDrawJava2D(null);
		debug.setFlags(DebugDraw.e_shapeBit | DebugDraw.e_jointBit);
		world.setDebugDraw(debug);
		camera.zoom = 1.2f;
		((EntityTrackingCamera)camera).offsetY = -200;
		((EntityTrackingCamera)camera).offsetX = 50;
		float zoomInt = ((float)numCarts-2) / 4f ;
		camera.zoom -= (0.4 * zoomInt);
		((EntityTrackingCamera)camera).offsetX = 160-(zoomInt*130);
		startTime = System.currentTimeMillis()+5000;
	}
	
	
	@Override
	public void update(float tick) {
		time = (time * 0.9f) + (tick * 0.1f);
//		if(Math.random() > 0.9) System.out.println(1f/time);
		if(input.isKeyDown(KeyEvent.VK_ESCAPE)) {
			System.exit(0);
		}
		
		if(input.isKeyDown(KeyEvent.VK_A) && !startScreen) {
			mainCart.goLeft(tick*50);
		}else if(input.isKeyDown(KeyEvent.VK_D) || endzone || startScreen) {
			mainCart.goRight(tick*50);
		} else {
			mainCart.dontGo();
		}
		
		
		int sleep = (int)((timeStep - tick) * 1000);
		if(sleep > 0) {
			try {
//				System.out.println("Sleeping for "+sleep+" ("+(1f/time)+")");
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		world.step(timeStep, 7, 3);
		super.update(timeStep);
		
		long timeTaken = (System.currentTimeMillis() - startTime) /1000;
		timeRemaining = Math.max(0, timeAllowed - timeTaken);
		if(!endzone && !startScreen && timeRemaining <=0) {
			endzone = true;
			timeout = true;
		}
		
		if(!endzone && !startScreen) {
			score = 0;
			for(int i = 0; i < otherCarts.size(); i++) {
				if(!otherCarts.get(i).flipped) {
					score++;
				}
			}
		}
		
		if(mainCart.x > 6500 && !endzone && !startScreen) {
			endzone = true;
			totalScore += score;
		}
		
		if(score < quota && !startScreen) {
			endzone = true;
		}
		
		if(startScreen && input.isKeyDown(KeyEvent.VK_SPACE)) {
			startScreen = false;
			levelIndex = 0;
			init(Level.levels[levelIndex]);
		}
		
		if(input.isKeyDown(KeyEvent.VK_L)) {
			levelIndex++;
			if(levelIndex >= Level.levels.length) {
				init();
			} else {
				init(Level.levels[levelIndex]);
			}
		}
		
		if(endzone && input.isKeyDown(KeyEvent.VK_SPACE)) {
			if(score >= quota && !timeout) {
				levelIndex++;
				if(levelIndex >= Level.levels.length) {
					init();
				} else {
					init(Level.levels[levelIndex]);
				}
			} else {
				init(Level.levels[levelIndex]);
			}
		}
	}
	
	@Override
	public void render(Graphics2D g) {
		super.render(g);
//		debug.setGraphics2D((Graphics2D) g.create());
//		world.drawDebugData();
		if(System.currentTimeMillis() <= startTime) {
			camera.unlook(g);
			g.fillRect(0, 0, getWidth(), getHeight());
			camera.look(g);
		}
	}
	
	String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ 0123456789/.";

	@Override
	public void renderHud(Graphics2D g) {
		if(System.currentTimeMillis() <= startTime) {
			int ord = (int) ((System.currentTimeMillis() / 200) % 3);
			
			switch(ord) {
				case 0: {
					drawText(50, (getHeight() / 2)-24, "LOADING .", g);
					break;
				}
				
				case 1: {
					drawText(50, (getHeight() / 2)-24, "LOADING ..", g);
					break;
				}
				
				case 2: {
					drawText(50, (getHeight() / 2)-24, "LOADING ...", g);
					break;
				}
			}
		} else {
			if(startScreen) {
				BufferedImage title = imageManager.get("title.png");
				g.drawImage(title, 100, 100, title.getWidth() * 8, title.getHeight() * 8, null);
				drawText(100, (getHeight() / 2)-24, "PRESS D TO ACCELERATE", g);
				drawText(100, getHeight() / 2, "PRESS A TO SLOW DOWN/REVERSE", g);
				drawText(100, (getHeight() / 2)+24, "PRESS SPACE TO START", g);
			} else if(endzone) {
				if(!timeout) drawText(50, (getHeight() / 2)-24, "FULL CARTS: "+score+"/"+numCarts, g);
				if(!timeout && score >= quota) {
					if(levelIndex == Level.levels.length - 1) {
						drawText(10, getHeight() / 2 - 24, "GAME "+(levelIndex+1)+" COMPLETE. SCORE: "+totalScore, g);
						drawText(10, getHeight() / 2, "PRESS SPACE TO START OVER", g);
					} else {
						drawText(10, getHeight() / 2, "LEVEL "+(levelIndex+1)+" COMPLETE. PRESS SPACE FOR LEVEL "+(levelIndex+2), g);
					}
				} else if(timeout){
					drawText(50, getHeight() / 2, "TIMES UP. PRESS SPACE TO RETRY", g);
				} else {
					drawText(50, getHeight() / 2, "LEVEL FAILED. PRESS SPACE TO RETRY", g);
				}
			} else {
				drawText(10, 10, "TIME: "+timeRemaining+"  "+"SCORE: "+totalScore, g);
				drawText(10, getHeight() - 30, "LEVEL "+(levelIndex+1)+" FULL CARTS: "+score+"/"+numCarts+" QUOTA: "+quota, g);
			}
		}
	}
	
	public void drawText(int x, int y, String message, Graphics2D g) {
		g.setColor(Color.black);
		for(int i = 0; i < message.length(); i++) {
			char current = message.charAt(i);
			int index = letters.indexOf(current);
			g.fillRect(x+(i*24), y, 24, 24);
			g.drawImage(imageManager.get("alphabet.png"), x+(i*24), y, x+(i*24)+20, y+20, index*5, 0, (index*5)+5, 5, null);
		}
	}
}
