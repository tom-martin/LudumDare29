package com.heychinaski.ld29;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.IViewportTransform;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;

import static java.lang.Math.round;

public class DebugDrawJava2D extends DebugDraw {

	private Graphics2D g2d;

	public DebugDrawJava2D(IViewportTransform viewport) {
		super(viewport);
	}
	
	public void setGraphics2D(Graphics2D g2d) {
		this.g2d = g2d;
	}
	
	public Vec2 scale(Vec2 toScale) {
		Vec2 clone = toScale.clone();
		clone.x *= 10;
		clone.y *= -10;
		return clone;
	}

	@Override
	public void drawPoint(Vec2 argPoint, float argRadiusOnScreen,
			Color3f argColor) {
		applyColor(argColor);
		Vec2 scaled = scale(argPoint);
		g2d.drawOval(	round(scaled.x-argRadiusOnScreen), 
						round(scaled.y-argRadiusOnScreen), 
						round(argRadiusOnScreen), 
						round(argRadiusOnScreen));
	}

	private void applyColor(Color3f argColor) {
		g2d.setColor(new Color(argColor.x, argColor.y, argColor.z));
	}

	@Override
	public void drawSolidPolygon(Vec2[] vertices, int vertexCount, Color3f color) {
		applyColor(color);
		Polygon poly = new Polygon();
		for(int i = 0; i < vertexCount; i++) {
			Vec2 scaled = scale(vertices[i]);
			poly.addPoint(round(scaled.x), round(scaled.y));
		}
		
		g2d.fillPolygon(poly);
		
	}

	@Override
	public void drawCircle(Vec2 center, float radius, Color3f color) {
		applyColor(color);
		float newRadius = radius*10;
		Vec2 scaled = scale(center);
		g2d.drawOval(round(scaled.x-newRadius), round(scaled.y-newRadius), round(newRadius*2), round(newRadius*2));		
	}

	@Override
	public void drawSolidCircle(Vec2 center, float radius, Vec2 axis,
			Color3f color) {
		applyColor(color);
		float newRadius = radius*10;
		Vec2 scaled = scale(center);
		g2d.fillOval(round(scaled.x-newRadius), round(scaled.y-newRadius), round(newRadius*2), round(newRadius*2));
		
	}

	@Override
	public void drawSegment(Vec2 p1, Vec2 p2, Color3f color) {
		applyColor(color);
		Vec2 s1 = scale(p1);
		Vec2 s2 = scale(p2);
		g2d.drawLine(round(s1.x), round(s1.y), round(s2.x), round(s2.y));
		
	}

	@Override
	public void drawTransform(Transform xf) {
		System.out.println("Draw transfrom not supported");
	}

	@Override
	public void drawString(float x, float y, String s, Color3f color) {
		applyColor(color);
		g2d.drawString(s, x*10, y*-10);
		
	}

}
