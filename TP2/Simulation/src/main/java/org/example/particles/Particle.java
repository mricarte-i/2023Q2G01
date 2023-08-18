package org.example.particles;

import org.example.distance_methods.DistanceMethod;
import org.example.points.Point;
import org.example.points.Point2D;

import java.math.BigInteger;

public interface Particle<P extends Point> {
    BigInteger getId();
    Double[] getCoordinates();
    P getPosition();
    Double distanceTo(Particle<P> p, DistanceMethod<P> distanceMethod);
    Double getRadius();
}
