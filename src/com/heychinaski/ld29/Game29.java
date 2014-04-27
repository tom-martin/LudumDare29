package com.heychinaski.ld29;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import com.heychinaski.engie.Game;
import com.heychinaski.engie.camera.Camera;
import com.heychinaski.engie.camera.EntityTrackingCamera;

public class Game29 extends Game {

	private static final long serialVersionUID = 1L;
	
	Camera camera = null;

	private World world;

	private Cart mainCart;

	private DebugDrawJava2D debug;

	private float time;
	float timeStep = 1.0f / 60.f;

	@Override
	public String[] images() {
		return new String[] {"wheel.png",
				             "minecart.png",
				             "bgTile.png",
				             "fgTile.png",
				             "sleeper.png",
				             "gem1.png",
				             "gem2.png",
				             "gem3.png",
				             "gem4.png"};
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
		Vec2 gravity = new Vec2(0.0f, -10.0f);
		world = new World(gravity);
		BufferedImage[] gems = new BufferedImage[] {imageManager.get("gem1.png"), imageManager.get("gem2.png"), imageManager.get("gem3.png"),imageManager.get("gem4.png")};
		Ground entity = new Ground(world, imageManager.get("fgTile.png"), imageManager.get("sleeper.png"));
		entities.add(entity);
	    
		int numCarts = 3;
		int x = -440 + (numCarts * 9);
		Image wheel = imageManager.get("wheel.png");
		Image minecartImg = imageManager.get("minecart.png");
		mainCart = new Cart(world, x, -1, null, wheel, minecartImg);
		entities.add(mainCart);
		
		Cart previousCart = mainCart;
		for(int i = 0; i < numCarts; i++) {
			x-= 9;
			Cart cart = new Cart(world, x, -i, previousCart.mainBody, wheel, minecartImg);
			
			if(i == (numCarts / 4)) {
				camera = new EntityTrackingCamera(cart, this);
			}
			
			int numRes = (int)(Math.random()*10)+5;
			for(int j = 0; j < numRes; j++) {
				Resource resource = new Resource(cart, gems[j % gems.length]);
				entities.add(resource);
			}
			entities.add(cart);
			
			previousCart = cart;
		}
		
		if(camera == null) {
			camera = new EntityTrackingCamera(mainCart, this);
		}
		
		
		debug = new DebugDrawJava2D(null);
		debug.setFlags(DebugDraw.e_shapeBit | DebugDraw.e_jointBit);
		world.setDebugDraw(debug);
		camera.zoom = 1;
	}
	
	
	@Override
	public void update(float tick) {
		time = (time * 0.9f) + (tick * 0.1f);
//		if(Math.random() > 0.9) System.out.println(1f/time);
		if(input.isKeyDown(KeyEvent.VK_ESCAPE)) {
			System.exit(0);
		}
		
		if(input.isKeyDown(KeyEvent.VK_A)) {
			mainCart.goLeft(tick*50);
		}
		
		if(input.isKeyDown(KeyEvent.VK_D)) {
			mainCart.goRight(tick*50);
		}
		
		if(!input.isKeyDown(KeyEvent.VK_A) && !input.isKeyDown(KeyEvent.VK_D)) {
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
		world.step(timeStep, 6, 3);
		super.update(timeStep);
	}
	
	@Override
	public void render(Graphics2D g) {
		super.render(g);
//		debug.setGraphics2D((Graphics2D) g.create());
//		world.drawDebugData();
	}
}
