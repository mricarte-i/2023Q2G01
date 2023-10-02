package org.example.integrators;

public class BeemanIntegrator implements Integrator {

    private final double dT, MASS;
    private double prevA, auxR, auxV, a, currR, currV;

    private final Double boundary;

    public BeemanIntegrator(double deltaT, double position, double velocity, double mass,
            ForceCalculator forceCalculator) {
        this.dT = deltaT;
        this.MASS = mass;
        this.auxR = this.currR = position;
        this.auxV = this.currV = velocity;
        this.a = forceCalculator.calculateForce(position, velocity) / MASS;
        this.prevA = forceCalculator.calculateForce(auxR - (deltaT * velocity), auxV - (deltaT * a)) / MASS;
        this.boundary = null;
    }

    public BeemanIntegrator(double deltaT, double position, double velocity, double mass,
                            ForceCalculator forceCalculator, Double boundary) {
        this.dT = deltaT;
        this.MASS = mass;
        this.auxR = this.currR = position;
        this.auxV = this.currV = velocity;
        this.a = forceCalculator.calculateForce(position, velocity) / MASS;
        this.prevA = forceCalculator.calculateForce(auxR - (deltaT * velocity), auxV - (deltaT * a)) / MASS;
        this.boundary = boundary;
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
        double nextR = getNextPosition();
        double nextV = getNextVelocity(nextR, forceCalculator);

        auxR = nextR;
        auxV = nextV;

        prevA = a;

        a = forceCalculator.calculateForce(auxR, auxV) / MASS;
        currR = nextR;
        currV = nextV;
    }

    private double getNextPosition() {
        double nextR = auxR + auxV * dT + ((2.0 / 3.0) * a - (1.0 / 6.0) * prevA) * Math.pow(dT, 2);
        if (boundary != null) {
            nextR = nextR % boundary;
            if (nextR < 0) {
                nextR += boundary;
            }
        }
        return nextR;
    }

    private double getNextVelocity(double nextR, ForceCalculator forceCalculator) {
        double futureA = forceCalculator.calculateForce(nextR, auxV) / MASS;
        return auxV + (1.0 / 3.0) * futureA * dT + (5.0 / 6.0) * a * dT - (1.0 / 6.0) * prevA * dT;
    }
}
