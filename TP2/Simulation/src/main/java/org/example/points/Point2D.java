package org.example.points;

import org.example.distance_methods.DistanceMethod;

public interface Point2D extends Point {
    Double getX();
    Double getY();
    Double[] getCoordinates();
    Double distanceTo(Point2D p, DistanceMethod<Point2D> distanceMethod);
}
