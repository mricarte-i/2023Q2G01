package org.example.particles;

import org.example.distance_methods.DistanceMethod;
import org.example.points.Point2D;
import org.example.points.SimpleVector2D;
import org.example.points.Vector2D;

import java.math.BigInteger;

public class SimpleOffLaticeParticle2D implements OffLaticeParticle2D {
    private final Particle2D particle;
    private final Vector2D velocity;

    public SimpleOffLaticeParticle2D(BigInteger id, Point2D position, Vector2D velocity, double radius) {
        this.particle = new SimpleParticle2D(id, position, radius);
        this.velocity = velocity;
    }

    public SimpleOffLaticeParticle2D(BigInteger id, double positionX, double positionY, double velocityMagnitude, double velocityAngle, double radius) {
        this.particle = new SimpleParticle2D(id, positionX, positionY, radius);
        this.velocity = new SimpleVector2D(velocityMagnitude, velocityAngle);
    }

    @Override
    public BigInteger getId() {
        return particle.getId();
    }

    @Override
    public double getX() {
        return particle.getX();
    }

    @Override
    public double getY() {
        return particle.getY();
    }

    @Override
    public double[] getCoordinates() {
        return particle.getCoordinates();
    }

    @Override
    public Point2D getPosition() {
        return particle.getPosition();
    }

    @Override
    public double getVelocityMagnitude() {
        return velocity.getMagnitude();
    }

    @Override
    public double getVelocityAngle() {
        return velocity.getAngle();
    }

    @Override
    public double xVelocityAngle() {
        return velocity.xAxisProjectionAngle();
    }

    @Override
    public double yVelocityAngle() {
        return velocity.yAxisProjectionAngle();
    }

    @Override
    public double xVelocity() {
        return velocity.xAxisProjection();
    }

    @Override
    public double yVelocity() {
        return velocity.yAxisProjection();
    }

    @Override
    public double[] getVelocityCoordinates() {
        return new double[] { xVelocity(), yVelocity() };
    }

    @Override
    public Vector2D getVelocity() {
        return velocity;
    }

    @Override
    public double distanceTo(Particle<Point2D> p, DistanceMethod<Point2D> distanceMethod) {
        return particle.distanceTo(p, distanceMethod);
    }

    @Override
    public double distanceTo(Particle2D p, DistanceMethod<Point2D> distanceMethod) {
        return particle.distanceTo(p, distanceMethod);
    }

    @Override
    public double getRadius() {
        return particle.getRadius();
    }
}
