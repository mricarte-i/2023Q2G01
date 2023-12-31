package org.example.integrators;

public class BeemanIntegrator implements Integrator {

    private final double dT, MASS;
    private double prevA, a, prevR, currR, prevV, currV, predictedV, futureA;
    private Double nextR;

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
        this.nextR = null;
        this.boundary = boundary;
    }

    private static double eulerPosition(double r, double v, double dT, double m, double f) {
        return r + dT * v + (Math.pow(dT, 2) / (2*m)) * f;
    }

    private static double eulerVelocity(double v, double dT, double m, double f) {
        return v + (dT / m) * f;
    }

    /*
        MEMO: Remember to call it after calling constructor
        AND AFTER REINSERTION
     */
    public void initPrevForce(ForceCalculator prevForceCalc) {
        this.prevA = prevForceCalc.calculateForce(prevR, prevV) / MASS;

        this.nextR = calculateNextPosition();
        this.predictedV = calculatePredictedVelocity();
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
        futureA = forceCalculator.calculateForce(nextR, predictedV) / MASS;
    }

    @Override
    public void advanceStep() {
        prevR = currR;
        prevV = currV;

        currR = nextR;
        currV = calculateCorrectedVelocity();

        prevA = a;
        a = futureA;

        nextR = calculateNextPosition();
        predictedV = calculatePredictedVelocity();
    }

    @Override
    public void advanceStep(ForceCalculator forceCalculator) {
        evaluateForce(forceCalculator);
        advanceStep();
    }

    public double getPreviousPosition() {
        return prevR;
    }

    public double getNextPosition() {
        return nextR;
    }

    private double calculateNextPosition() {
        double nextR = currR + currV * dT + ((2.0 / 3.0) * a - (1.0 / 6.0) * prevA) * Math.pow(dT, 2);
        if (boundary != null) {
            nextR = nextR % boundary;
            if (nextR < 0) {
                nextR += boundary;
            }
        }
        return nextR;
    }

    public double getPreviousVelocity() {
        return prevV;
    }

    public double getPredictedVelocity() {
        return predictedV;
    }

    private double calculatePredictedVelocity() {
        return currV + (3.0 / 2.0) * a * dT - (1.0 / 2.0) * prevA * dT;
    }

    private double calculateCorrectedVelocity() {
        return currV + (1.0 / 3.0) * futureA * dT + (5.0 / 6.0) * a * dT - (1.0 / 6.0) * prevA * dT;
    }

    public void reinsert(double position, double velocity, ForceCalculator reinsertionForceCalc) {
        double reinsertionForce = reinsertionForceCalc.calculateForce(position, velocity);

        this.currR = position;
        this.currV = velocity;
        this.a     = reinsertionForce / MASS;

        this.prevR = eulerPosition(position, velocity, -dT, MASS, reinsertionForce);
        this.prevV = eulerVelocity(velocity, -dT, MASS, reinsertionForce);

        this.nextR = null;
    }
}
