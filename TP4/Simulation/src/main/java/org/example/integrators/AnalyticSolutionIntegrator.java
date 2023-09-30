package org.example.integrators;

public class AnalyticSolutionIntegrator implements Integrator {
    @Override
    public double getPosition() {
        return 0;
    }

    @Override
    public double getVelocity() {
        return 0;
    }

    @Override
    public void advanceStep(ForceCalculator forceCalculator) {

    }
}
