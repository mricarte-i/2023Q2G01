package org.example.distance_methods;

import org.example.points.Point;

public interface DistanceMethod<P extends Point> {
    double calculateDistance(P a, P b);
}
