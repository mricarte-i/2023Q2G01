package org.example.particles;

import org.example.distance_methods.DistanceMethod;
import org.example.points.Point2D;
import org.example.points.SimplePoint2D;

import java.math.BigInteger;

public class SimpleParticle2D extends SimpleParticle<Point2D> implements Point2D {

    public SimpleParticle2D(BigInteger id, Point2D position, Double radius) {
        super(id, position, radius);
    }

    public SimpleParticle2D(BigInteger id, Double positionX, Double positionY, Double radius) {
        super(id, new SimplePoint2D(positionX, positionY), radius);
    }

    @Override
    public BigInteger getId() {
        return super.getId();
    }

    @Override
    public Double getX() {
        return position.getX();
    }

    @Override
    public Double getY() {
        return position.getY();
    }

    @Override
    public Double[] getCoordinates() {
        return super.getCoordinates();
    }

    @Override
    public Point2D getPosition() {
        return super.getPosition();
    }

    @Override
    public Double distanceTo(Point2D p, DistanceMethod<Point2D> distanceMethod) {
        return distanceMethod.calculateDistance(this, p);
    }

    @Override
    public Double distanceTo(Particle<Point2D> p, DistanceMethod<Particle<Point2D>> distanceMethod) {
        return super.distanceTo(p, distanceMethod);
    }

    @Override
    public Double getRadius() {
        return super.getRadius();
    }
}
