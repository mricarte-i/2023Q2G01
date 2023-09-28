package org.example.integrators;

public class BeemanIntegrator implements Integrator {

    private double dT, MASS, K, G;
    private double prevA, auxR, auxV, a, currR, currV;

    private double getAcceleration(double r, double v) {
        return -(K*r + G*v) / MASS;
    }

    public BeemanIntegrator(double deltaT, double position, double velocity, double mass, double k, double g) {
        this.dT = deltaT;
        this.K = k;
        this.G= g;
        this.MASS = mass;
        this.auxR = this.currR = position;
        this.auxV = this.currV = velocity;
        this.a = getAcceleration(position, velocity);
        this.prevA = getAcceleration(auxR-(deltaT*velocity), auxV-(deltaT*a));


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
    public void advanceStep(double nextStepForce) {
        //TODO: what about that nextStepForce ???
        double nextR = getNextPosition();
        double nextV = getNextVelocity(nextR);

        auxR = nextR;
        auxV = nextV;

        prevA = a;

        a = getAcceleration(auxR, auxV);
        currR = nextR;
        currV = nextV;
    }

    private double getNextPosition() {
        return auxR + auxV*dT + ((2.0/3.0)*a - (1.0/6.0)*prevA)*Math.pow(dT, 2);
    }

    private double getNextVelocity(double nextR) {
        double futureA = getAcceleration(nextR, auxV);
        return auxV + (1.0/3.0)*futureA*dT + (5.0/6.0)*a*dT - (1.0/6.0)*prevA*dT;
    }
}
