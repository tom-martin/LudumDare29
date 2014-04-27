package com.heychinaski.ld29;

import java.awt.Color;
import java.awt.Graphics2D;
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

	final Body wheel1;
	final Body wheel2;
	final Body mainBody;
	private RevoluteJoint joint1;
	private RevoluteJoint joint2;
	
	public Body initialiseWheel(World world, float x, int groupIndex) {
		BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.position.set(x, 10);

        CircleShape cs = new CircleShape();
        cs.m_radius = 1.2f;
        
        FixtureDef fd = new FixtureDef();
        fd.shape = cs;
        fd.density = 0.9f;
        fd.friction = 0.5f;        
        fd.restitution = 0.1f;
        fd.filter.groupIndex = groupIndex;
        Body b= world.createBody(bd);
        b.createFixture(fd);
        return b;
	}
	
	public Body initialiseMain(World world, float x, int groupIndex) {
		BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.position.set(x, 10);

        PolygonShape ps = new PolygonShape();
        ps.setAsBox(3f, 0.5f);
        
        FixtureDef fd = new FixtureDef();
        fd.shape = ps;
        fd.density = 2.6f;
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
        wheelJointDef.maxMotorTorque = 200.0f;
        wheelJointDef.enableMotor = false;
        return (RevoluteJoint) world.createJoint(wheelJointDef);
	}

	public Cart(World world, int x, int groupIndex, Body leader) {
		super();

        wheel1 = initialiseWheel(world, x-2f, groupIndex);
        wheel2 = initialiseWheel(world, x+2f, groupIndex);
        mainBody = initialiseMain(world, x, groupIndex);
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
        	djd.collideConnected = true;
        	djd.dampingRatio = 1.0f;
        	djd.frequencyHz = 7;
        	world.createJoint(djd);
        	
        	djd = new DistanceJointDef();
        	pos1 = leader.getPosition().clone();
//        	pos1.x -= 1.25f;
        	pos2 = mainBody.getPosition().clone();
        	pos2.x += 1.25f;
        	pos2.y -= .25f;
        	djd.initialize(leader, mainBody, pos1, pos2);
        	djd.collideConnected = true;
        	djd.dampingRatio = 1.0f;
        	djd.frequencyHz = 7;
        	world.createJoint(djd);
        }
        
	}

	@Override
	public void update(float tick, Game game) {
		this.nextX = mainBody.getPosition().x*10;
		this.nextY = mainBody.getPosition().y*-10;
	}
	
	public void drawWheel(Graphics2D g, Body wheel) {
		Vec2 position = wheel.getPosition();
		g.translate(position.x*10, position.y*-10);
		g.rotate(-wheel.getAngle());
		g.setColor(Color.green);
		g.fillOval(-10, -10, 20, 20);
		g.setColor(Color.blue);
		g.fillOval(-10, -10, 5, 5);
		g.rotate(wheel.getAngle());
		g.translate(-position.x*10, position.y*10);
	}

	@Override
	public void render(Graphics2D g, Game game) {
		drawWheel(g, wheel1);
		drawWheel(g, wheel2);
		
		Vec2 position = mainBody.getPosition();
		g.translate(position.x*10, position.y*-10);
		g.rotate(-mainBody.getAngle());
		g.setColor(Color.red);
		g.fillRect(-30, -40, 60, 45);
		g.setColor(Color.blue);
		g.fillRect(-30, -5, 5, 5);
		g.rotate(mainBody.getAngle());
		g.translate(-position.x*10, position.y*10);
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
		joint1.setMotorSpeed(20);
		joint2.enableMotor(true);
		joint2.setMotorSpeed(20);
	}
	
	public void goRight(float amount) {
		joint1.enableMotor(true);
		joint1.setMotorSpeed(-20);
		joint2.enableMotor(true);
		joint2.setMotorSpeed(-20);
	}
}
