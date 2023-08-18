package org.example.particles;

import org.example.distance_methods.DistanceMethod;
import org.example.points.Point2D;
import org.example.points.SimplePoint2D;

import java.math.BigInteger;

public class SimpleParticle2D extends SimpleParticle<Point2D> implements Particle2D {

    public SimpleParticle2D(BigInteger id, Point2D position, double radius) {
        super(id, position, radius);
    }

    public SimpleParticle2D(BigInteger id, Double positionX, double positionY, double radius) {
        super(id, new SimplePoint2D(positionX, positionY), radius);
    }

    @Override
    public BigInteger getId() {
        return super.getId();
    }

    @Override
    public double getX() {
        return position.getX();
    }

    @Override
    public double getY() {
        return position.getY();
    }

    @Override
    public double[] getCoordinates() {
        return super.getCoordinates();
    }

    @Override
    public Point2D getPosition() {
        return super.getPosition();
    }

    @Override
    public double distanceTo(Particle<Point2D> p, DistanceMethod<Point2D> distanceMethod) {
        return super.distanceTo(p, distanceMethod);
    }

    @Override
    public double distanceTo(Particle2D p, DistanceMethod<Point2D> distanceMethod) {
        return this.distanceTo((Particle<Point2D>) p, distanceMethod);
    }

    @Override
    public double getRadius() {
        return super.getRadius();
    }
}
