package org.example;

public class Particle {
    // TODO: implement
    private int id;
    private double radius, mass, x, y, vx, vy;

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
        return 0;
    }

    public double collidesY() {
        return 0;
    }

    // detects particle collisions
    public double collides(Particle b) {
        return 0;
    }

    // applies velocity changes to this particle (vs walls or vs other particle)
    public void bounceX() {
    }

    public void bounceY() {
    }

    public void bounce(Particle b) {
    }

    public int getCollisionCount() {
        return 0;
    }

}
