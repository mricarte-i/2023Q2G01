package org.example.particles;

import org.example.distance_methods.DistanceMethod;
import org.example.points.Point;

import java.math.BigInteger;

public interface Particle<P extends Point> extends Point {
    BigInteger getId();
    Double[] getCoordinates();
    P getPosition();
    Double distanceTo(Particle<P> p, DistanceMethod<P> distanceMethod);
    Double getRadius();
}
