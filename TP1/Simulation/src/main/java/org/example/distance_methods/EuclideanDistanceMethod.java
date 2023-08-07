package org.example.distance_methods;

import org.example.points.Point;

import java.util.Arrays;

public abstract class EuclideanDistanceMethod<P extends Point> implements DistanceMethod<P> {
    @Override
    public Double calculateDistance(P a, P b) {
        Double[] aCoordinates = a.getCoordinates();
        Double[] bCoordinates = b.getCoordinates();
        int spaceSize         = Math.max(aCoordinates.length, bCoordinates.length);
        double[] difference   = new double[spaceSize];
        for(int i = 0; i < spaceSize; i++) {
            difference[i] = aCoordinates[i] - bCoordinates[i];
        }
        return Math.sqrt(Arrays.stream(difference).reduce(0, (sqSum, curr) -> sqSum + Math.pow(curr, 2)));
    }
}
