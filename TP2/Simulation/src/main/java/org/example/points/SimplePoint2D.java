package org.example.points;

import org.example.distance_methods.DistanceMethod;

public class SimplePoint2D implements Point2D {

    private final double positionX;
    private final double positionY;

    public SimplePoint2D(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public SimplePoint2D(SimplePoint2D p){
        this.positionX = p.positionX;
        this.positionY = p.positionY;
    }

    @Override
    public double getX() {
        return positionX;
    }

    @Override
    public double getY() {
        return positionY;
    }

    @Override
    public double[] getCoordinates() {
        return new double[] { positionX, positionY };
    }

    @Override
    public double distanceTo(Point2D p, DistanceMethod<Point2D> distanceMethod) {
        return distanceMethod.calculateDistance(this, p);
    }
}
