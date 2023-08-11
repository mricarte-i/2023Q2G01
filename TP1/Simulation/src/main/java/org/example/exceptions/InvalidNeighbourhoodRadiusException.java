package org.example.exceptions;

public class InvalidNeighbourhoodRadiusException extends RuntimeException {

    double cellSide;
    double rc;
    double r1;
    double r2;

    public InvalidNeighbourhoodRadiusException(double cellSide, double rc, double r1, double r2) {
        this.cellSide = cellSide;
        this.rc = rc;
        this.r1 = r1;
        this.r2 = r2;
    }

    @Override
    public String getMessage() {
        return "L / M (" + cellSide + ") should be bigger than rc (" + rc + ") + biggest two radii (" + r1 + ", " + r2 + ")";
    }
}
