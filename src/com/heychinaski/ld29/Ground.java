package com.heychinaski.ld29;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.geometry.euclidean.twod.Line;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import com.heychinaski.engie.Entity;
import com.heychinaski.engie.Game;

public class Ground extends Entity {
	
	public static final int HALF_WIDTH = 7500;

	float difficulty = 0.2f;
	
	Point2D.Float[] points = new Point2D.Float[0];
	private PolynomialSplineFunction splineFunc;
	private ArrayList<Polygon> polygons;
	{
		w = HALF_WIDTH;
		h = HALF_WIDTH;
		
		
	}
	
	World world;

	private BufferedImage[] cache;

	private BufferedImage fgTile;

	private BufferedImage sleeper;
	
	public Ground(World world, BufferedImage fgTile, BufferedImage sleeper, float difficulty, long seed) {
		super();
		this.difficulty = difficulty;
		Random r = new Random(seed);
		
		List<Point2D.Float> ps = new ArrayList<Point2D.Float>();
		int nextGap = (Math.round(r.nextFloat()*225))+225;
		int x = 0;
		for(x = -HALF_WIDTH; x <= HALF_WIDTH; x += nextGap) {
			float yVariance = (nextGap*difficulty);
			if(x < -((float)HALF_WIDTH * 0.5) || x > ((float)HALF_WIDTH * 0.75)) {
				ps.add(new Point2D.Float(x,0));
			} else {
				ps.add(new Point2D.Float(x,(r.nextFloat()*(yVariance))-(yVariance / 2)));	
			}
			nextGap = (Math.round(r.nextFloat()*225))+225;
		}
		
		ps.add(new Point2D.Float(HALF_WIDTH,0));
		
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
		for(x = -HALF_WIDTH; x <= HALF_WIDTH; x+=10) {
			y = (float) splineFunc.value(x);
			
			if(currentPolygon != null) {
				currentPolygon.addPoint(Math.round(x), Math.round(y));
				vCount++;
			}
			
			float currentDiff = y - previousY;
			if(vCount >= 6 ||
				(previousDiff != null && 
		         previousDiff > currentDiff)) {
				currentPolygon.addPoint(x, 1024);
				currentPolygon.addPoint(previousStart, 1024);
				polygons.add(currentPolygon);
				currentPolygon = null;
				previousDiff = null;
			}
			
			if(currentPolygon == null) {
				currentPolygon = new Polygon();
				currentPolygon.addPoint(x, 1024);
				currentPolygon.addPoint(x, Math.round(y));
				previousStart = x;
				vCount = 2;
			}
			
			previousDiff = currentDiff;
			previousY = y;
		}
		currentPolygon.addPoint(HALF_WIDTH, 1024);
		currentPolygon.addPoint(previousStart, 1024);
		polygons.add(currentPolygon);
		
		this.world = world;
		this.fgTile = scaleImage(fgTile);
		this.sleeper = scaleImage(sleeper);
		
		for(int i = 0; i < polygons.size(); i++) {
		    Polygon p = polygons.get(i);
		    Vec2[] vs = new Vec2[p.npoints];
		    for(int j = 0; j < p.npoints; j++) {
		    	vs[j] = new Vec2(((float)p.xpoints[j]) * 0.1f, ((float)p.ypoints[j]) * -0.1f);
		    }
		    PolygonShape pShape = new PolygonShape();
		    pShape.set(vs, vs.length);
		         
		    FixtureDef fd = new FixtureDef();
		    fd.shape = pShape;
		    fd.friction = 100f;      
		 
		    BodyDef bd = new BodyDef();
		    bd.position= new Vec2(0.0f,0.0f);
		    
		    Body body = world.createBody(bd);
		    body.createFixture(fd);
		}
	}

	private BufferedImage scaleImage(BufferedImage i) {
		GraphicsConfiguration gc = GraphicsEnvironment.
		  		getLocalGraphicsEnvironment().getDefaultScreenDevice().
		  		getDefaultConfiguration();
		BufferedImage si  = gc.createCompatibleImage(i.getWidth()*4, i.getHeight()*4, BufferedImage.BITMASK);
		Graphics2D g = (Graphics2D)si.getGraphics();
		g.drawImage(i, 0, 0, i.getWidth()*4, i.getHeight()*4, null);
		g.dispose();
		return si;
	}

	@Override
	public void update(float tick, Game game) {
		
	}

	@Override
	public void render(Graphics2D g, Game game) {
		if(cache == null) {
			List<BufferedImage> cacheList = new ArrayList<BufferedImage>();
			for(int x = -HALF_WIDTH; x < HALF_WIDTH; x+=1024) {
				BufferedImage newCache = g.getDeviceConfiguration().createCompatibleImage(1024, 1536, BufferedImage.BITMASK);
				Graphics2D cacheG = (Graphics2D) newCache.getGraphics();
				cacheG.translate(x, 256);
				
				cacheG.setPaint(new TexturePaint(fgTile, new Rectangle(0, 0, 256, 256)));
				
				for(int i = 0; i < polygons.size(); i++) {
					Polygon p = polygons.get(i);
					
					cacheG.fill(p);
				}
				
				for(int sx = -HALF_WIDTH; sx < HALF_WIDTH; sx+= sleeper.getWidth()*2) {
					int x1 = sx - sleeper.getWidth()/2;
					int x2 = sx + sleeper.getWidth()/2;
					
					if(x1 > -HALF_WIDTH && x2 < HALF_WIDTH) {
					
						int y1 = (int) Math.round(splineFunc.value(x1));
						int y2 = (int) Math.round(splineFunc.value(x2));
						
						Vector2D l1 = new Vector2D(x1, y1);
						Vector2D l2 = new Vector2D(x2, y2);
						Vector2D mid = new Vector2D(l1.getX() + (l2.getX() - l1.getX()), l1.getY() + (l2.getY() - l1.getY()));
						Line line = new Line(l1, l2);
						cacheG.translate(mid.getX(), mid.getY());
						cacheG.rotate(line.getAngle());
						cacheG.drawImage(sleeper, -sleeper.getWidth()/2, -(sleeper.getHeight()/2), null);
						cacheG.rotate(-line.getAngle());
						cacheG.translate(-mid.getX(), -mid.getY());
					}
				}

//				for(int i = 0; i < points.length; i++) {
//					Point2D.Float p = points[i];
//					cacheG.setColor(Color.GREEN);
//					cacheG.fillRect(Math.round(p.x-5), Math.round(p.y-5), 10, 10);
//				}
				cacheG.dispose();
				cacheList.add(newCache);
			}
			
			
			
			cache = cacheList.toArray(new BufferedImage[0]);
		}
		
		int i = 0;
		for(int x = -HALF_WIDTH; x < HALF_WIDTH; x+=1024) {
			g.drawImage(cache[i], -x, -256, null);
			i++;
		}
	}

	@Override
	public void collided(Entity with, float tick, Game game, Rectangle2D.Float bounds,
		Rectangle2D.Float nextBounds, Rectangle2D.Float withBounds) {
		
	}

}
