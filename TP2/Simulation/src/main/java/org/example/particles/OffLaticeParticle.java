package org.example.particles;

import org.example.distance_methods.DistanceMethod;
import org.example.points.Point;
import org.example.points.Vector;

import java.math.BigInteger;

public interface OffLaticeParticle<P extends Point, V extends Vector> extends Particle<P> {
    BigInteger getId();
    double[] getCoordinates();
    P getPosition();
    double[] getVelocityCoordinates();
    V getVelocity();
    double distanceTo(Particle<P> p, DistanceMethod<P> distanceMethod);
    double getRadius();
}
