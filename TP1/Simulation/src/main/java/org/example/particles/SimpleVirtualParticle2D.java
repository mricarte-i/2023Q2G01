package org.example.particles;

import org.example.distance_methods.DistanceMethod;
import org.example.points.Point2D;
import org.example.points.SimplePoint2D;

import java.math.BigInteger;
import java.util.Arrays;

public class SimpleVirtualParticle2D<P extends Particle2D> implements VirtualParticle2D<P> {

    private final P realParticle;
    private final Double deltaX;
    private final Double deltaY;

    public SimpleVirtualParticle2D(P p, Double deltaX, Double deltaY){
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
    public Double distanceTo(Particle<Point2D> p, DistanceMethod<Point2D> distanceMethod) {
        return distanceMethod.calculateDistance(this.getPosition(), p.getPosition()) - this.getRadius() - p.getRadius();
    }


    @Override
    public Double distanceTo(Particle2D p, DistanceMethod<Point2D> distanceMethod) {
        return this.distanceTo((Particle<Point2D>) p, distanceMethod);
    }

    @Override
    public Double getRadius() {
        return realParticle.getRadius();
    }

    @Override
    public P getRealParticle() {
        return realParticle;
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
