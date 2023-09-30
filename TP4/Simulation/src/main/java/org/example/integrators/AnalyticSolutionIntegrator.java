package org.example.integrators;

public class AnalyticSolutionIntegrator implements Integrator {
    private double dT, MASS, K, G, A, r, time;
    public AnalyticSolutionIntegrator(double deltaT, double position, double mass, double k, double g){
        this.dT = deltaT;
        this.MASS = mass;
        this.A = this.r = position;
        this.K = k;
        this.G = g;
        this.time = 0;
    }
    @Override
    public double getPosition() {
        return this.r;
    }

    @Override
    public double getVelocity() {
        return 0;
    }

    @Override
    public void advanceStep(ForceCalculator forceCalculator) {
        //IGNORES FORCE CALCULATOR
        this.r = A*Math.exp((-G*time)/(2*MASS))*Math.cos(Math.sqrt(K/MASS - Math.pow(G/(2*MASS), 2) )*time);
        this.time += dT;
    }
}
