package org.example.integrators;

public interface Integrator {
    double getPosition();
    double getVelocity();
    void advanceStep(double nextStepAcceleration);
    int getStep();
}
