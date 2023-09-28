package org.example.integrators;

@FunctionalInterface
public interface ForceCalculator {
    double calculateForce(double position, double velocity);
}
