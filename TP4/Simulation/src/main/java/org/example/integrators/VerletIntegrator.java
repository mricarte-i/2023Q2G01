package org.example.integrators;

public class VerletIntegrator implements Integrator {

    private final double dT, MASS;
    private double nextR, currR, currV, prevR;

    private final Double boundary;

    public VerletIntegrator(double deltaT, double position, double velocity, double mass,
            ForceCalculator forceCalculator) {
        this(deltaT, position, velocity, mass, forceCalculator, null);
    }

    public VerletIntegrator(double deltaT, double position, double velocity, double mass,
                            ForceCalculator forceCalculator, Double boundary) {
        this.dT = deltaT;
        this.MASS = mass;

        this.force = forceCalculator.calculateForce(position, velocity);

        this.prevR = eulerPosition(position, velocity, -deltaT, mass, this.force);
        this.currR = position;
        this.nextR = getNextR(); // Depends on prevR, currR and force

        this.currV = velocity;

        this.boundary = boundary;
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

    public double getNextPosition() { return this.nextR; }

    @Override
    public double getVelocity() {
        return this.currV;
    }

    private Double force;

    @Override
    public void evaluateForce(ForceCalculator forceCalculator) {
        force = forceCalculator.calculateForce(nextR, currV); // nextR is currR and currV is prevV in nextStep
    }

    @Override
    public void advanceStep() {
        this.prevR = this.currR;
        this.currR = this.nextR;

        this.nextR = getNextR();
        this.currV = getCurrV();
        this.force = null;
    }

    @Override
    public void advanceStep(ForceCalculator forceCalculator) {
        evaluateForce(forceCalculator);
        advanceStep();
    }

    private double getNextR() {
        double nextR = 2.0 * currR - prevR + (Math.pow(dT, 2) / MASS) * force;
        if (boundary != null) {
            nextR = nextR % boundary;
            if (nextR < 0) {
                nextR += boundary;
            }
        }
        return nextR;
    }

    private double getCurrV() {
        return (nextR - prevR) / (2.0 * dT);
    }
}
