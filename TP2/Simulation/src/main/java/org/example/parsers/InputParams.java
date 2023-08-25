package org.example.parsers;

import org.example.particles.OffLaticeParticle2D;
import org.example.particles.Particle;
import org.example.points.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class InputParams {
    private static InputParams instance;

    private long seed;

    private int particleNumber, steps;
    private double radius, interactionRadius, sideLength, initialParticleVelocity, noiseAmplitude;

    Collection<OffLaticeParticle2D> particles;
    private String staticPath, dynamicPath, outputPath, polarizationOutPath;

    private InputParams() {
        // Initialize default values here
        particleNumber = 0;
        interactionRadius = 0.0;
        sideLength = 0.0;
        initialParticleVelocity = 0.0;
        noiseAmplitude = 0.0;
        particles = new ArrayList<>();
        staticPath = dynamicPath = outputPath = polarizationOutPath = null;
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

    public long getSeed() {
        return this.seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int getSteps() {
        return this.steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
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

    public Collection<OffLaticeParticle2D> getParticles() {
        return particles;
    }

    public void setParticles(Collection<OffLaticeParticle2D> particles) {
        this.particles = particles;
    }

    public String getStaticPath() {
        return this.staticPath;
    }

    public void setStaticPath(String staticPath) {
        this.staticPath = staticPath;
    }

    public String getDynamicPath() {
        return this.dynamicPath;
    }

    public void setDynamicPath(String dynamicPath) {
        this.dynamicPath = dynamicPath;
    }

    public String getOutputPath() {
        return this.outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getPolarizationOutPath() {
        return this.polarizationOutPath;
    }

    public void setPolarizationOutPath(String polarizationOutPath) {
        this.polarizationOutPath = polarizationOutPath;
    }

}
