package org.example.integrators;

public class VerletIntegrator implements Integrator {

    private double dT, MASS;
    private double prevA, auxR, auxV, a, currR, currV;

    public VerletIntegrator(double deltaT, double position, double velocity, double mass,
            ForceCalculator forceCalculator) {
        this.dT = deltaT;
        this.MASS = mass;
        this.auxR = this.currR = position;
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
        double nextR = getNextPosition(auxR, forceCalculator);
        double nextV = getNextVelocity(nextR, auxR);

        auxR = nextR;
        auxV = nextV;

        prevA = a;

        a = forceCalculator.calculateForce(auxR, auxV) / MASS;
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
