package org.example.particles;

import org.example.distance_methods.DistanceMethod;
import org.example.points.Point;
import org.example.points.Point2D;

import java.math.BigInteger;

public interface Particle<P extends Point> {
    BigInteger getId();
    double[] getCoordinates();
    P getPosition();
    double distanceTo(Particle<P> p, DistanceMethod<P> distanceMethod);
    double getRadius();
}
