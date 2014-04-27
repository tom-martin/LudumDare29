package com.heychinaski.ld29;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D.Float;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import org.jbox2d.dynamics.joints.WheelJointDef;

import com.heychinaski.engie.Entity;
import com.heychinaski.engie.Game;

public class Cart extends Entity {
	int SCALE = 10;

	final Body wheel1;
	final Body wheel2;
	final Body mainBody;
	private RevoluteJoint joint1;
	private RevoluteJoint joint2;
	
	boolean flipped = false;
	private Image wheelImage;

	private Image minecartImg;
	
	public Body initialiseWheel(World world, float x, int groupIndex, float density) {
		BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.position.set(x, 10);

        CircleShape cs = new CircleShape();
        cs.m_radius = 1.2f;
        
        FixtureDef fd = new FixtureDef();
        fd.shape = cs;
        fd.density = density;
        fd.friction = 100f;        
        fd.restitution = 0.1f;
        fd.filter.groupIndex = groupIndex;
        Body b= world.createBody(bd);
        b.createFixture(fd);
        return b;
	}
	
	public Body initialiseMain(World world, float x, int groupIndex, float density) {
		BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.position.set(x, 10);

        PolygonShape ps = new PolygonShape();
        ps.setAsBox(4f, 0.5f);
        
        FixtureDef fd = new FixtureDef();
        fd.shape = ps;
        fd.density = density;
        fd.friction = 0.1f;        
        fd.restitution = 0.1f;
        fd.filter.groupIndex = groupIndex;
        Body b= world.createBody(bd);
        b.createFixture(fd);
        return b;
	}
	
	public RevoluteJoint addJoint(World world, Body wheel) {
		RevoluteJointDef wheelJointDef = new RevoluteJointDef();
        wheelJointDef.initialize(mainBody, wheel, wheel.getPosition());
        wheelJointDef.motorSpeed = 0.0f;
        wheelJointDef.maxMotorTorque = 300.0f;
        wheelJointDef.enableMotor = false;
        return (RevoluteJoint) world.createJoint(wheelJointDef);
	}

	public Cart(World world, int x, int groupIndex, Body leader, Image wheelImage, Image minecartImg) {
		super();
		this.wheelImage = wheelImage;
		this.minecartImg = minecartImg;

        wheel1 = initialiseWheel(world, x-3f, groupIndex, leader == null ? .5f : 0.2f);
        wheel2 = initialiseWheel(world, x+3f, groupIndex, leader == null ? 1f : 0.3f);
        mainBody = initialiseMain(world, x, groupIndex, leader == null ? 6.0f : .4f);
        joint1 = addJoint(world, wheel1);
        joint2 = addJoint(world, wheel2);
        
        if(leader != null) {
        	DistanceJointDef djd = new DistanceJointDef();
        	Vec2 pos1 = leader.getPosition().clone();
//        	pos1.x -= 1.25f;
        	Vec2 pos2 = mainBody.getPosition().clone();
        	pos2.x += 1.25f;
        	pos2.y += .25f;
        	djd.initialize(leader, mainBody, pos1, pos2);
        	djd.collideConnected = false;
        	djd.dampingRatio = .9f;
        	djd.frequencyHz = 15;
        	world.createJoint(djd);
        	
        	djd = new DistanceJointDef();
        	pos1 = leader.getPosition().clone();
//        	pos1.x -= 1.25f;
        	pos2 = mainBody.getPosition().clone();
        	pos2.x += 1.25f;
        	pos2.y -= .25f;
        	djd.initialize(leader, mainBody, pos1, pos2);
        	djd.collideConnected = false;
        	djd.dampingRatio = .5f;
        	djd.frequencyHz = 5;
        	world.createJoint(djd);
        }
        
	}

	@Override
	public void update(float tick, Game game) {
		this.nextX = mainBody.getPosition().x*SCALE;
		this.nextY = mainBody.getPosition().y*-SCALE;
		
		if(wheel2.getPosition().x < wheel1.getPosition().x) {
			flipped = true;
		}
	}
	
	public void drawWheel(Graphics2D g, Body wheel) {
		Vec2 position = wheel.getPosition();
		g.translate(position.x*SCALE, position.y*-SCALE);
		g.rotate(-wheel.getAngle());
//		g.setColor(Color.green);
//		g.fillOval(-12, -12, 24, 24);
//		g.setColor(Color.blue);
////		g.fillOval(-12, -12, 5, 5);
		
		g.drawImage(wheelImage, -12, -12, 24, 24, null);
		g.rotate(wheel.getAngle());
		g.translate(-position.x*SCALE, position.y*SCALE);
	}

	@Override
	public void render(Graphics2D g, Game game) {
		Vec2 position = mainBody.getPosition();
		g.translate(position.x*SCALE, position.y*-SCALE);
		g.rotate(-mainBody.getAngle());
//		g.setColor(flipped ? Color.darkGray : Color.red);
//		g.fillRect(-40, -40, 80, 45);
//		g.setColor(Color.blue);
//		g.fillRect(-40, -5, 5, 5);
		g.drawImage(minecartImg, -50, -40, 100, 44, null);
		g.rotate(mainBody.getAngle());
		g.translate(-position.x*SCALE, position.y*SCALE);
		
		drawWheel(g, wheel1);
		drawWheel(g, wheel2);
	}

	@Override
	public void collided(Entity with, float tick, Game game, Float bounds,
			Float nextBounds, Float withBounds) {
		
	}
	
	public void dontGo() {
		joint1.enableMotor(false);
		joint2.enableMotor(false);
		joint1.setMotorSpeed(0);
		joint2.setMotorSpeed(0);
	}

	public void goLeft(float amount) {
		joint1.enableMotor(true);
		joint1.setMotorSpeed(200);
		joint2.enableMotor(true);
		joint2.setMotorSpeed(200);
	}
	
	public void goRight(float amount) {
		joint1.enableMotor(true);
		joint1.setMotorSpeed(-200);
		joint2.enableMotor(true);
		joint2.setMotorSpeed(-200);
	}
}
