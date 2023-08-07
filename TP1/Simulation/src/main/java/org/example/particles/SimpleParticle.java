package org.example.particles;

import org.example.distance_methods.DistanceMethod;
import org.example.points.Point;
import org.example.points.SimplePoint2D;

import java.math.BigInteger;

public class SimpleParticle<P extends Point> implements Particle<P> {
    protected final BigInteger id;
    protected final P position;
    protected final Double radius;

    public SimpleParticle(BigInteger id, P position, Double radius){
        this.id = id;
        this.position = position;
        this.radius = radius;
    }

    @Override
    public BigInteger getId() {
        return id;
    }

    @Override
    public Double[] getCoordinates() {
        return position.getCoordinates();
    }

    @Override
    public P getPosition() {
        return position;
    }

    @Override
    public Double getRadius() {
        return radius;
    }

    @Override
    public Double distanceTo(Particle<P> p, DistanceMethod<Particle<P>> distanceMethod) {
        return distanceMethod.calculateDistance(this, p) - radius - p.getRadius();
    }
}
