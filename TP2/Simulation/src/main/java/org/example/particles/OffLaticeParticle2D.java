package org.example.particles;

import org.example.distance_methods.DistanceMethod;
import org.example.points.Point2D;
import org.example.points.Vector2D;

import java.math.BigInteger;

public interface OffLaticeParticle2D extends Particle2D, OffLaticeParticle<Point2D, Vector2D> {
    BigInteger getId();
    double getX();
    double getY();
    double[] getCoordinates();
    Point2D getPosition();
    double getVelocityMagnitude();
    double getVelocityAngle();
    double xVelocityAngle();
    double yVelocityAngle();
    double xVelocity();
    double yVelocity();
    double[] getVelocityCoordinates();
    Vector2D getVelocity();
    double distanceTo(Particle<Point2D> p, DistanceMethod<Point2D> distanceMethod);
    double distanceTo(Particle2D p, DistanceMethod<Point2D> distanceMethod);
    double getRadius();
}
