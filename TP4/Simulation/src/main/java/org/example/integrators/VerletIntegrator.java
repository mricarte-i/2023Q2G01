package org.example.integrators;

public class VerletIntegrator implements Integrator {

    private final double dT, MASS;
    private double currR, currV, prevR;
    private double nextR;

    private final Double boundary;

    public VerletIntegrator(double deltaT, double position, double velocity, double mass,
            ForceCalculator forceCalculator) {
        this(deltaT, position, velocity, mass, forceCalculator, null);
    }

    public VerletIntegrator(double deltaT, double position, double velocity, double mass,
                            ForceCalculator forceCalculator, Double boundary) {
        this.dT = deltaT;
        this.MASS = mass;
        this.nextR = position;
        this.currR = this.prevR = eulerPosition(position, velocity, -deltaT, mass, forceCalculator.calculateForce(position, velocity)); //TODO: calculate step @ -0.1
        this.currV = velocity;
        this.boundary = boundary;
        this.advanceStep(forceCalculator);
    }

    private static double eulerPosition(double r, double v, double dT, double m, double f) {
        return r + dT * v + (Math.pow(dT, 2) / (2*m)) * f;
    }

    private static double eulerVelocity(double v, double dT, double m, double f) {
        return v + (dT / m) * f;
    }

    @Override
    public double getPosition() {
        return this.currR;
    }

    @Override
    public double getVelocity() {
        return this.currV;
    }

    @Override
    public void advanceStep(ForceCalculator forceCalculator) {
        this.prevR = this.currR;
        this.currR = this.nextR;

        double nextR = getNextPosition(prevR, forceCalculator);
        double currV = getNextVelocity(nextR, prevR);

        this.nextR = nextR;
        this.currV = currV;
    }

    private double getNextPosition(double prevR, ForceCalculator forceCalculator) {
        double nextR = 2.0 * currR - prevR + (Math.pow(dT, 2) / MASS) * forceCalculator.calculateForce(currR, currV);
        if (boundary != null) {
            nextR = nextR % boundary;
            if (nextR < 0) {
                nextR += boundary;
            }
        }
        return nextR;
    }

    private double getNextVelocity(double nextR, double prevR) {
        return (nextR - prevR) / (2.0 * dT);
    }
}
