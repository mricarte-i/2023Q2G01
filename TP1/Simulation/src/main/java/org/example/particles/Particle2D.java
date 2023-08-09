package org.example.particles;

import org.example.distance_methods.DistanceMethod;
import org.example.points.Point2D;

import java.math.BigInteger;

public interface Particle2D extends Particle<Point2D>, Point2D {
    BigInteger getId();
    Double getX();
    Double getY();
    Double[] getCoordinates();
    Point2D getPosition();
    Double distanceTo(Point2D p, DistanceMethod<Point2D> distanceMethod);
    Double distanceTo(Particle<Point2D> p, DistanceMethod<Point2D> distanceMethod);
    Double getRadius();
}
