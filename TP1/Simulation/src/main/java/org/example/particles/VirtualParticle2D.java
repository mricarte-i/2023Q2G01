package org.example.particles;

import org.example.distance_methods.DistanceMethod;
import org.example.points.Point2D;

import java.math.BigInteger;

public interface VirtualParticle2D extends VirtualParticle<Point2D, Particle2D>, Particle2D {
    BigInteger getId();
    Double getX();
    Double getY();
    Double[] getCoordinates();
    Point2D getPosition();
    Double distanceTo(Particle<Point2D> p, DistanceMethod<Point2D> distanceMethod);
    Double distanceTo(Particle2D p, DistanceMethod<Point2D> distanceMethod);
    Double getRadius();
    Particle2D getRealParticle();
}
