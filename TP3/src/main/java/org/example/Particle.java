package org.example;

import java.math.BigInteger;

public class Particle {
    // TODO: implement
    private int id;
    private double radius, mass, x, y, vx, vy;
    private int collisionCount = 0;

    // NOTE: initial speed is 0.01, random direction, vx vy are the components of v
    // initial positions should be within the first container
    public Particle(double rx, double ry, double vx, double vy, double mass, double radius) {

    }

    public Particle(int id, double rx, double ry, double vx, double vy, double mass, double radius) {
        this.id = id;
        this.x = rx;
        this.y = ry;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
        this.radius = radius;
    }

    // collidesX|Y detects wall collisions
    public double collidesX() {
        return Double.POSITIVE_INFINITY;
    }

    public double collidesY() {
        return Double.POSITIVE_INFINITY;
    }

    // detects particle collisions
    public double collides(Particle b) {
        return Double.POSITIVE_INFINITY;
    }

    // applies velocity changes to this particle (vs walls or vs other particle)
    public void bounceX() {
        this.vx = -this.vx;
    }

    public void bounceY() {
        this.vy = -this.vy;
    }

    public void bounce(Particle b) {
    }

    public int getCollisionCount() {
        return collisionCount;
    }

}
