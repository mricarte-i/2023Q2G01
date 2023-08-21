package org.example.points;

public class SimpleVector2D implements Vector2D {
    private final double magnitude;
    private final double angle;

    public SimpleVector2D(double magnitude, double angle) {
        this.magnitude = magnitude;
        this.angle = angle;
    }

    public SimpleVector2D(Point2D start, Point2D end) {
        this(start.getX(), start.getY(), end.getX(), end.getY());
    }

    public SimpleVector2D(double magnitude, double xAxisProjectionAngle, double yAxisProjectionAngle) {
        this.angle = Math.atan(yAxisProjectionAngle / xAxisProjectionAngle);
        this.magnitude = magnitude;
    }

    public SimpleVector2D(double startX, double startY, double endX, double endY) {
        double xCoordinate = endX - startX;
        double yCoordinate = endY - startY;
        this.magnitude = Math.sqrt(Math.pow(xCoordinate, 2) + Math.pow(yCoordinate, 2));

        double angle = Math.atan(Math.abs(yCoordinate) / Math.abs(xCoordinate));
        if (xCoordinate < 0 && yCoordinate >= 0) {
            angle = Math.PI - angle;
        } else if (xCoordinate < 0 && yCoordinate < 0) {
            angle = Math.PI + angle;
        } else if (xCoordinate >= 0 && yCoordinate < 0) {
            angle = 2 * Math.PI - angle;
        }
        this.angle = angle;
    }

    @Override
    public double getMagnitude() {
        return magnitude;
    }

    @Override
    public double getAngle() {
        return angle;
    }

    @Override
    public double[] getAngles() {
        return new double[] { angle };
    }

    @Override
    public double xAxisProjectionAngle() {
        return Math.cos(angle);
    }

    @Override
    public double yAxisProjectionAngle() {
        return Math.sin(angle);
    }

    @Override
    public double xAxisProjection() {
        return getMagnitude() * xAxisProjectionAngle();
    }

    @Override
    public double yAxisProjection() {
        return getMagnitude() * yAxisProjectionAngle();
    }
}
