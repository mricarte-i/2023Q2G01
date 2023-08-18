package org.example.points;

public interface Vector2D extends Vector {
    double getMagnitude();
    double getAngle();
    double[] getAngles();
    double xAxisProjectionAngle();
    double yAxisProjectionAngle();
    double xAxisProjection();
    double yAxisProjection();
}
