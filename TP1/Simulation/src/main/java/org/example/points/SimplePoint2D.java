package org.example.points;

import org.example.distance_methods.DistanceMethod;

public class SimplePoint2D implements Point2D {

    private final Double positionX;
    private final Double positionY;

    public SimplePoint2D(Double positionX, Double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public SimplePoint2D(SimplePoint2D p){
        this.positionX = p.positionX;
        this.positionY = p.positionY;
    }

    @Override
    public Double getX() {
        return positionX;
    }

    @Override
    public Double getY() {
        return positionY;
    }

    @Override
    public Double[] getCoordinates() {
        return new Double[] { positionX, positionY };
    }

    @Override
    public Double distanceTo(Point2D p, DistanceMethod<Point2D> distanceMethod) {
        return distanceMethod.calculateDistance(this, p);
    }
}
