package org.example.integrators;

@FunctionalInterface
public interface ForceCalculator {
    double calculateForce(Double time, Double position, Double velocity);
}
