package org.example.particles;

import org.example.distance_methods.DistanceMethod;
import org.example.points.Point;

import java.math.BigInteger;

public interface VirtualParticle<T extends Point, P extends Particle<T>> extends Particle<T> {
    BigInteger getId();
    Double[] getCoordinates();
    T getPosition();
    Double distanceTo(Particle<T> p, DistanceMethod<T> distanceMethod);
    P getRealParticle();
}
