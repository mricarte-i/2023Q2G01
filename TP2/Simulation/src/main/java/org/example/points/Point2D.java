package org.example.points;

import org.example.distance_methods.DistanceMethod;

public interface Point2D extends Point {
    double getX();
    double getY();
    double[] getCoordinates();
    double distanceTo(Point2D p, DistanceMethod<Point2D> distanceMethod);
}
