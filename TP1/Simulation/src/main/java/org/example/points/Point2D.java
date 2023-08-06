package org.example.points;

public interface Point2D extends Point{
    Double getX();
    Double getY();
    Double[] getCoordinates();
    Double distanceTo(Point2D p);
}
