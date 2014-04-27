package com.heychinaski.ld29;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.BufferedImage;

import com.heychinaski.engie.Entity;
import com.heychinaski.engie.Game;

public class Resource extends Entity {
	Cart cart;
	private int xOffset;
	
	Color color = new Color((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
	private int yOffset;
	private float fallMomentumY = -1;
	private float fallMomentumX = -1;
	
	private float fallRotation = -1;
	private double rotOff;
	private BufferedImage img;

	public Resource(Cart cart, BufferedImage img) {
		super();
		this.cart = cart;
		this.img = img;
		
		xOffset = (int)(Math.random()*60)-30;
		yOffset = (int)(Math.random()*5)-45;
		rotOff = (Math.random()*Math.PI) - Math.PI/2;
	}

	@Override
	public void update(float tick, Game game) {
		if(!cart.flipped) {
			nextX = cart.x;
			nextY = cart.y;
		} else {
			if(fallMomentumY == -1) {
				fallMomentumX = (float) ((cart.mainBody.getLinearVelocity().x*Cart.SCALE)+(Math.random()*70)-35);
				fallMomentumY = (float) ((cart.mainBody.getLinearVelocity().y*-Cart.SCALE)+(Math.random()*500));
				fallRotation = cart.mainBody.getAngle();
			}
			fallMomentumY = Math.min(fallMomentumY+(tick*60), 1000);
			nextY = y+(fallMomentumY*tick);
			nextX = x +(fallMomentumX*tick);
			rotOff += (rotOff*tick);
		}
	}

	@Override
	public void render(Graphics2D g, Game game) {
		g.setColor(color);
		g.translate(x, y);
		
		float rot = cart.flipped ? fallRotation : cart.mainBody.getAngle();
		g.rotate(-rot);
		g.translate(xOffset, yOffset);
		g.rotate(-rotOff);
//		g.fillRect(-10, -10, 20, 20);
		g.drawImage(img, -10, -10, 20, 20, null);
		g.rotate(rotOff);
		g.translate(-xOffset, -yOffset);
		g.rotate(rot);
		g.translate(-x, -y);
	}

	@Override
	public void collided(Entity with, float tick, Game game, Float bounds,
			Float nextBounds, Float withBounds) {
		// TODO Auto-generated method stub
		
	}

}
