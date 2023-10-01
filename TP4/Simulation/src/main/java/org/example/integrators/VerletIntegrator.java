package org.example.integrators;

public class VerletIntegrator implements Integrator {

    private final double dT, MASS;
    private double prevA, auxR, auxV, a, currR, currV, prevR;

    public VerletIntegrator(double deltaT, double position, double velocity, double mass,
            ForceCalculator forceCalculator) {
        this.dT = deltaT;
        this.MASS = mass;
        this.auxR = this.currR = position;
        this.prevR = position; //TODO: calculate step @ -0.1
        this.auxV = this.currV = velocity;
        this.a = forceCalculator.calculateForce(position, velocity) / MASS;
        this.prevA = forceCalculator.calculateForce(auxR - (deltaT * velocity), auxV - (deltaT * a)) / MASS;

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
        double nextR = getNextPosition(prevR, forceCalculator);
        double nextV = getNextVelocity(nextR, prevR);

        auxR = nextR;
        auxV = nextV;

        prevA = a;

        a = forceCalculator.calculateForce(auxR, auxV) / MASS;

        prevR = currR;

        currR = nextR;
        currV = nextV;
    }

    private double getNextPosition(double prevR, ForceCalculator forceCalculator) {
        return 2.0 * auxR - prevR + (Math.pow(dT, 2) / MASS) * forceCalculator.calculateForce(auxR, auxV);
    }

    private double getNextVelocity(double nextR, double prevR) {
        return (nextR - prevR) / (2.0 * dT);
    }
}
