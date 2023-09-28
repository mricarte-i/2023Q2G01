package org.example.integrators;

@FunctionalInterface
public interface ForceCalculator {
    double calculate(Double time, Double position, Double velocity);
}