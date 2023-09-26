package org.example.integrators;

public interface Integrator {
    double getPosition();
    double getVelocity();
    double advanceStep(double nextStepForce);
    int getStep();
}
