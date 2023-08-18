package org.example.particles;

import org.example.distance_methods.DistanceMethod;
import org.example.points.Point2D;

import java.math.BigInteger;

public interface Particle2D extends Particle<Point2D> {
    BigInteger getId();
    double getX();
    double getY();
    double[] getCoordinates();
    Point2D getPosition();
    double distanceTo(Particle<Point2D> p, DistanceMethod<Point2D> distanceMethod);
    double distanceTo(Particle2D p, DistanceMethod<Point2D> distanceMethod);
    double getRadius();
}
