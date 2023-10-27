package org.example.integrators;

public class BeemanIntegrator implements Integrator {

    private final double dT, MASS;
    private double prevA, a, prevR, currR, prevV, currV, predictedV, nextR, futureA;

    private final Double boundary;

    public BeemanIntegrator(double deltaT, double position, double velocity, double mass,
            ForceCalculator forceCalc) {
        this(deltaT, position, velocity, mass, forceCalc, null);
    }

    public BeemanIntegrator(double deltaT, double position, double velocity, double mass,
                            ForceCalculator forceCalc, Double boundary) {
        double initForce = forceCalc.calculateForce(position, velocity);

        this.dT = deltaT;
        this.MASS = mass;
        this.currR = position;
        this.currV = velocity;
        this.a = initForce / MASS;
        this.prevR = eulerPosition(position, velocity, -deltaT, mass, initForce);
        this.prevV = eulerVelocity(velocity, -deltaT, mass, initForce);
        this.boundary = boundary;
    }

    private static double eulerPosition(double r, double v, double dT, double m, double f) {
        return r + dT * v + (Math.pow(dT, 2) / (2*m)) * f;
    }

    private static double eulerVelocity(double v, double dT, double m, double f) {
        return v + (dT / m) * f;
    }

    public void initPrevForce(ForceCalculator prevForceCalc) {
        this.prevA = prevForceCalc.calculateForce(prevR, prevV) / MASS;
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
    public void evaluateForce(ForceCalculator forceCalculator) {
        nextR = getNextPosition();
        predictedV = getPredictedVelocity();

        futureA = forceCalculator.calculateForce(nextR, predictedV) / MASS;
    }

    @Override
    public void advanceStep() {
        prevR = currR;
        prevV = currV;

        currR = nextR;
        currV = getCorrectedVelocity();

        prevA = a;
        a = futureA;
    }

    @Override
    public void advanceStep(ForceCalculator forceCalculator) {
        evaluateForce(forceCalculator);
        advanceStep();
    }

    public double getNextPosition() {
        double nextR = currR + currV * dT + ((2.0 / 3.0) * a - (1.0 / 6.0) * prevA) * Math.pow(dT, 2);
        if (boundary != null) {
            nextR = nextR % boundary;
            if (nextR < 0) {
                nextR += boundary;
            }
        }
        return nextR;
    }

    private double getPredictedVelocity() {
        return currV + (3.0 / 2.0) * a * dT - (1.0 / 2.0) * prevA * dT;
    }

    private double getCorrectedVelocity() {
        return currV + (1.0 / 3.0) * futureA * dT + (5.0 / 6.0) * a * dT - (1.0 / 6.0) * prevA * dT;
    }

    public void reinsert(double position) {
        currR = position;
        nextR = getNextPosition();
    }
}
