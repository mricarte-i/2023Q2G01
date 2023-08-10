package org.example.particles;

import org.example.distance_methods.DistanceMethod;
import org.example.points.Point2D;
import org.example.points.SimplePoint2D;

import java.math.BigInteger;
import java.util.Arrays;

public class VirtualParticle2D implements Particle2D {

    private final Particle2D realParticle;
    private final Double deltaX;
    private final Double deltaY;

    public VirtualParticle2D(Particle2D p, Double deltaX, Double deltaY){
        this.realParticle = p;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    @Override
    public BigInteger getId() {
        return realParticle.getId();
    }

    @Override
    public Double getX() {
        return realParticle.getX() + deltaX;
    }

    @Override
    public Double getY() {
        return realParticle.getY() + deltaY;
    }

    @Override
    public Double[] getCoordinates() {
        Double[] virtualCoordinates = realParticle.getCoordinates().clone();
        virtualCoordinates[0] += deltaX;
        virtualCoordinates[1] += deltaY;
        return virtualCoordinates;
    }

    @Override
    public Point2D getPosition() {
        return new SimplePoint2D(this.getX(), this.getY());
    }

    @Override
    public Double distanceTo(Point2D p, DistanceMethod<Point2D> distanceMethod) {
        return p.distanceTo(this.getPosition(), distanceMethod) - this.getRadius();
    }

    @Override
    public Double distanceTo(Particle<Point2D> p, DistanceMethod<Point2D> distanceMethod) {
        return distanceMethod.calculateDistance(this.getPosition(), p.getPosition()) - this.getRadius() - p.getRadius();
    }

    @Override
    public Double getRadius() {
        return realParticle.getRadius();
    }

    @Override
    public String toString() {
        return "@{id=" + getId().toString() + ";r=" + getRadius().toString() + ";p=" + Arrays.toString(getPosition().getCoordinates()) + "}";
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Particle<?>))
            return false;
        return getId().equals(((Particle<?>) o).getId());
    }
}
