package org.example.parsers;

import org.example.particles.Particle;
import org.example.points.Point;

import java.util.ArrayList;
import java.util.List;

public class InputParams {
    private static InputParams instance;

    private int particleNumber;
    private double interactionRadius, sideLength, initialParticleVelocity, noiseAmplitude;
    List<Particle<Point>> particles;

    private InputParams() {
        // Initialize default values here
        particleNumber = 0;
        interactionRadius = 0.0;
        sideLength = 0.0;
        initialParticleVelocity = 0.0;
        noiseAmplitude = 0.0;
        particles = new ArrayList<>();
    }

    public static InputParams getInstance() {
        if (instance == null) {
            synchronized (InputParams.class) {
                if (instance == null) {
                    instance = new InputParams();
                }
            }
        }
        return instance;
    }

    public int getParticleNumber() {
        return particleNumber;
    }

    public void setParticleNumber(int particleNumber) {
        this.particleNumber = particleNumber;
    }

    public double getInteractionRadius() {
        return interactionRadius;
    }

    public void setInteractionRadius(double interactionRadius) {
        this.interactionRadius = interactionRadius;
    }

    public double getSideLength() {
        return sideLength;
    }

    public void setSideLength(double sideLength) {
        this.sideLength = sideLength;
    }

    public double getInitialParticleVelocity() {
        return initialParticleVelocity;
    }

    public void setInitialParticleVelocity(double initialParticleVelocity) {
        this.initialParticleVelocity = initialParticleVelocity;
    }

    public double getNoiseAmplitude() {
        return noiseAmplitude;
    }

    public void setNoiseAmplitude(double noiseAmplitude) {
        this.noiseAmplitude = noiseAmplitude;
    }

    public List<Particle<Point>> getParticles() {
        return particles;
    }

    public void setParticles(List<Particle<Point>> particles) {
        this.particles = particles;
    }
}
