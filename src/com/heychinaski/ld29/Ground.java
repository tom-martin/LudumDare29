package com.heychinaski.ld29;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import com.heychinaski.engie.Entity;
import com.heychinaski.engie.Game;

public class Ground extends Entity {
	
	public static final int HALF_WIDTH = 10000;

	Point2D.Float[] points = new Point2D.Float[0];
	private PolynomialSplineFunction splineFunc;
	private ArrayList<Polygon> polygons;
	{
		w = HALF_WIDTH;
		h = HALF_WIDTH;
		
		Random r = new Random();
		
		List<Point2D.Float> ps = new ArrayList<Point2D.Float>();
		int nextGap = (Math.round(r.nextFloat()*150))+150;
		for(int x = -HALF_WIDTH; x <= HALF_WIDTH; x += nextGap) {
			float yVariance = (nextGap*0.75f);
			ps.add(new Point2D.Float(x,(r.nextFloat()*(yVariance))-(yVariance / 2)));
			nextGap = (Math.round(r.nextFloat()*75))+75;
		}
		
		ps.add(new Point2D.Float(HALF_WIDTH,(r.nextFloat()*100)-50));
		
		points = ps.toArray(points);
		
		double[] xs = new double[points.length];
		double[] ys = new double[points.length];
		for(int i = 0; i < points.length; i++) {
			xs[i] = points[i].x;
			ys[i] = points[i].y;
		}
		splineFunc = new SplineInterpolator().interpolate(xs, ys);
		
		polygons = new ArrayList<Polygon>();
		Polygon currentPolygon = null;
		float y = 0;
		float previousY = (float) splineFunc.value(-HALF_WIDTH);
		int previousStart = -HALF_WIDTH;
		Float previousDiff = null;
		int vCount = 0;
		for(int x = -HALF_WIDTH; x <= HALF_WIDTH; x+=10) {
			y = (float) splineFunc.value(x);
			
			if(currentPolygon != null) {
				currentPolygon.addPoint(Math.round(x), Math.round(y));
				vCount++;
			}
			
			float currentDiff = y - previousY;
			if(vCount >= 6 ||
				(previousDiff != null && 
		         previousDiff > currentDiff)) {
				currentPolygon.addPoint(x, 200);
				currentPolygon.addPoint(previousStart, 200);
				polygons.add(currentPolygon);
				currentPolygon = null;
				previousDiff = null;
			}
			
			if(currentPolygon == null) {
				currentPolygon = new Polygon();
				currentPolygon.addPoint(x, 200);
				currentPolygon.addPoint(x, Math.round(y));
				previousStart = x;
				vCount = 2;
			}
			
			previousDiff = currentDiff;
			previousY = y;
		}
		currentPolygon.addPoint(HALF_WIDTH, 200);
		currentPolygon.addPoint(previousStart, 200);
		polygons.add(currentPolygon);
	}
	
	World world;

	private BufferedImage cache;
	
	public Ground(World world) {
		super();
		this.world = world;
		
		for(int i = 0; i < polygons.size(); i++) {
		    Polygon p = polygons.get(i);
		    Vec2[] vs = new Vec2[p.npoints];
		    for(int j = 0; j < p.npoints; j++) {
		    	vs[j] = new Vec2(((float)p.xpoints[j]) * 0.1f, ((float)p.ypoints[j]) * -0.1f);
		    }
		    PolygonShape ps = new PolygonShape();
		    ps.set(vs, vs.length);
		         
		    FixtureDef fd = new FixtureDef();
		    fd.shape = ps;
		    fd.friction = 5.0f;      
		 
		    BodyDef bd = new BodyDef();
		    bd.position= new Vec2(0.0f,0.0f);
		    
		    Body body = world.createBody(bd);
		    body.createFixture(fd);
		}
	}

	@Override
	public void update(float tick, Game game) {
		
	}

	@Override
	public void render(Graphics2D g, Game game) {
		if(cache == null) {
			System.out.println("Rendering");
			cache = g.getDeviceConfiguration().createCompatibleImage(HALF_WIDTH * 2, 500);
			
			Graphics2D cacheG = (Graphics2D) cache.getGraphics();
			cacheG.translate(HALF_WIDTH, 250);
			
			cacheG.setColor(Color.YELLOW);
			for(int i = 0; i < polygons.size(); i+=2) {
				cacheG.fill(polygons.get(i));
			}
			cacheG.setColor(Color.WHITE);
			for(int i = 1; i < polygons.size(); i+=2) {
				cacheG.fill(polygons.get(i));
			}
			for(int i = 0; i < points.length; i++) {
				Point2D.Float p = points[i];
				cacheG.setColor(Color.GREEN);
				cacheG.fillRect(Math.round(p.x-5), Math.round(p.y-5), 10, 10);
			}
		}
		
		g.drawImage(cache, -HALF_WIDTH, -250, null);
	}

	@Override
	public void collided(Entity with, float tick, Game game, Rectangle2D.Float bounds,
		Rectangle2D.Float nextBounds, Rectangle2D.Float withBounds) {
		
	}

}
