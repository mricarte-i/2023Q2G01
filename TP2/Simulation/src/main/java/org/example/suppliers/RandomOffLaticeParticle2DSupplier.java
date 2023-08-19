package org.example.suppliers;

import org.example.particles.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class RandomOffLaticeParticle2DSupplier implements Supplier<Collection<OffLaticeParticle2D>> {

    protected final int n;
    protected final Random randomGenerator;
    protected final double maxX;
    protected final double maxY;
    protected final double v;
    protected final double particleRadius;

    public RandomOffLaticeParticle2DSupplier(int n, double maxX, double maxY, double v, double particleRadius) {
        this.n = n;
        this.maxX = maxX;
        this.maxY = maxY;
        this.v    = v;
        this.particleRadius = particleRadius;
        this.randomGenerator = new Random();
    }

    public RandomOffLaticeParticle2DSupplier(int n, double maxX, double maxY, double v, double particleRadius, long seed) {
        this.n = n;
        this.maxX = maxX;
        this.maxY = maxY;
        this.v    = v;
        this.particleRadius = particleRadius;
        this.randomGenerator = new Random(seed);
    }

    public RandomOffLaticeParticle2DSupplier(int n, double maxX, double maxY, double v, double particleRadius, Random randomGenerator) {
        this.n = n;
        this.maxX = maxX;
        this.maxY = maxY;
        this.v    = v;
        this.particleRadius = particleRadius;
        this.randomGenerator = randomGenerator;
    }

    @Override
    public Collection<OffLaticeParticle2D> get() {
        List<OffLaticeParticle2D> generatedParticles = new ArrayList<>(n);
        Supplier<BigInteger> idGenerator = new IncreasingBigIntegersSupplier();
        for (int i = 0; i < n; i++) {
            double x     = getNextX();
            double y     = getNextY();
            double r     = getNextRadius();
            double v     = getNextV();
            double theta = getNextTheta();
            generatedParticles.add(new SimpleOffLaticeParticle2D(idGenerator.get(), x, y, v, theta, r));
        }
        return generatedParticles;
    }

    protected double getNextX() {
        return randomGenerator.nextDouble()*maxX;
    }

    protected double getNextY() {
        return randomGenerator.nextDouble()*maxX;
    }

    protected double getNextV() {
        return this.v;
    }

    protected double getNextTheta() {
        return randomGenerator.nextDouble()*2*Math.PI;
    }

    protected double getNextRadius() {
        return particleRadius;
    }
}
