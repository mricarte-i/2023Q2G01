package org.example.distance_methods;

import org.example.points.Point;

public interface DistanceMethod<P extends Point> {
    Double calculateDistance(P a, P b);
}
