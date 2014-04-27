package com.heychinaski.ld29;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;

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
	private Cart cart2;
	private Cart cart3;

	private DebugDrawJava2D debug;

	@Override
	public String[] images() {
		return new String[] {};
	}

	@Override
	public Camera camera() {
		return camera;
	}

	@Override
	public Image bgTileImage() {
		return null;
	}

	@Override
	public void init() {

		
		Vec2 gravity = new Vec2(0.0f, -10.0f);
		world = new World(gravity);
		
		Ground entity = new Ground(world);
		entities.add(entity);	
	    
		int numCarts = 7;
		int x = -90 + (numCarts * 7);
		mainCart = new Cart(world, x, -1, null);
		entities.add(mainCart);
		
		Cart previousCart = mainCart;
		for(int i = 0; i < numCarts; i++) {
			x-= 7;
			Cart cart = new Cart(world, x, -2, previousCart.mainBody);
			entities.add(cart);
			
			if(i == (numCarts / 4)) {
				camera = new EntityTrackingCamera(cart, this);
			}
		}
		
		
		
		debug = new DebugDrawJava2D(null);
		debug.setFlags(DebugDraw.e_shapeBit | DebugDraw.e_jointBit);
		world.setDebugDraw(debug);
	}
	
	
	@Override
	public void update(float tick) {
		if(input.isKeyDown(KeyEvent.VK_ESCAPE)) {
			System.exit(0);
		}
		
		if(input.isKeyDown(KeyEvent.VK_A)) {
			mainCart.goLeft(tick*50);
		}
		
		if(input.isKeyDown(KeyEvent.VK_D)) {
			mainCart.goRight(tick*50);
		}
		
		if(!input.isKeyDown(KeyEvent.VK_D) && !input.isKeyDown(KeyEvent.VK_D)) {
			mainCart.dontGo();
		}
		
		float timeStep = 1.0f / 60.f;
		world.step(timeStep, 6, 3);
		super.update(tick);
		
		try {
			Thread.sleep((int)(timeStep - tick) * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void render(Graphics2D g) {
		super.render(g);
//		debug.setGraphics2D((Graphics2D) g.create());
//		world.drawDebugData();
	}

}
