package com.heychinaski.ld29;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.image.BufferedImage;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import com.heychinaski.engie.Entity;
import com.heychinaski.engie.Game;

public class GemPile extends Entity {
	
	BufferedImage img;

	public GemPile(BufferedImage img, int x, int y, World world) {
		super();
		this.img = img;
		this.x = x;
		this.y = y;
		this.nextX = x;
		this.nextY = y;
		
		BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.position.set((x*0.1f)+30, 10);

        PolygonShape ps = new PolygonShape();
        ps.setAsBox(10f, 200f);
        
        FixtureDef fd = new FixtureDef();
        fd.shape = ps;
        fd.density = 1;
        fd.friction = 0.5f;        
        fd.restitution = 0.7f;
        Body b= world.createBody(bd);
        b.createFixture(fd);
	}

	@Override
	public void update(float tick, Game game) {
		
	}

	@Override
	public void render(Graphics2D g, Game game) {
		g.drawImage(img, Math.round(x-(img.getWidth()*2)), Math.round(y-(img.getHeight()*2)), img.getWidth()*4, img.getHeight()*4, null);
	}

	@Override
	public void collided(Entity with, float tick, Game game, Float bounds,
			Float nextBounds, Float withBounds) {
		
	}

}
