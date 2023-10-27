package org.example.integrators;

public interface Integrator {
    double getPosition();
    double getVelocity();
    void evaluateForce(ForceCalculator force);
    void advanceStep();
    void advanceStep(ForceCalculator force);
}
