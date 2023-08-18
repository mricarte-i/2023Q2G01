package org.example.particles;

import org.example.distance_methods.DistanceMethod;
import org.example.points.Point;
import org.example.points.SimplePoint2D;

import java.math.BigInteger;
import java.util.Arrays;

public class SimpleParticle<P extends Point> implements Particle<P> {
    protected final BigInteger id;
    protected final P position;
    protected final double radius;

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
    public double[] getCoordinates() {
        return position.getCoordinates().clone();
    }

    @Override
    public P getPosition() {
        return position;
    }

    @Override
    public double getRadius() {
        return radius;
    }

    @Override
    public double distanceTo(Particle<P> p, DistanceMethod<P> distanceMethod) {
        return distanceMethod.calculateDistance(position, p.getPosition()) - radius - p.getRadius();
    }

    @Override
    public String toString() {
        return "@{id=" + id.toString() + ";r=" + Double.toString(radius) + ";p=" + Arrays.toString(position.getCoordinates()) + "}";
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Particle<?>))
            return false;
        return id.equals(((Particle<?>) o).getId());
    }
}
