package org.example.integrators;

public interface Integrator {
    double getPosition();
    double getVelocity();
    void advanceStep(ForceCalculator forceCalculator);
}
