package org.example.integrators;

public interface Integrator {
    double getPosition();
    double getVelocity();
    void evaluateForce(ForceCalculator forceCalculator);
    void advanceStep();
    void advanceStep(ForceCalculator forceCalculator);
}
