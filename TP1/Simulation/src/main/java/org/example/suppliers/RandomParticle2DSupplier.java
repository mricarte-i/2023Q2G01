package org.example.suppliers;

import org.example.particles.Particle2D;
import org.example.particles.SimpleParticle2D;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class RandomParticle2DSupplier implements Supplier<Collection<Particle2D>> {

    protected final int n;
    protected final Random randomGenerator;
    protected final double maxX;
    protected final double maxY;
    protected final double maxRadius;

    public RandomParticle2DSupplier(int n,  double maxX, double maxY, double maxRadius) {
        this.n = n;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxRadius = maxRadius;
        this.randomGenerator = new Random();
    }

    public RandomParticle2DSupplier(int n, double maxX, double maxY, double maxRadius, long seed) {
        this.n = n;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxRadius = maxRadius;
        this.randomGenerator = new Random(seed);
    }

    @Override
    public Collection<Particle2D> get() {
        List<Particle2D> generatedParticles = new ArrayList<>(n);
        Supplier<BigInteger> idGenerator = new IncreasingBigIntegersSupplier();
        for (int i = 0; i < n; i++) {
            double x = getNextX();
            double y = getNextY();
            double r = getNextRadius();
            generatedParticles.add(new SimpleParticle2D(idGenerator.get(), x, y, r));
        }
        return generatedParticles;
    }

    protected double getNextX() {
        return randomGenerator.nextDouble()*maxX;
    }

    protected double getNextY() {
        return randomGenerator.nextDouble()*maxX;
    }

    protected double getNextRadius() {
        return randomGenerator.nextDouble()*maxRadius;
    }
}
