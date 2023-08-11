package org.example.suppliers;

public class RandomParticle2DWithFixedRadiusSupplier extends RandomParticle2DSupplier {
    public RandomParticle2DWithFixedRadiusSupplier(int n, double maxX, double maxY, double maxRadius) {
        super(n, maxX, maxY, maxRadius);
    }
    public RandomParticle2DWithFixedRadiusSupplier(int n, double maxX, double maxY, double maxRadius, long seed) {
        super(n, maxX, maxY, maxRadius, seed);
    }

    @Override
    protected double getNextRadius() {
        return maxRadius;
    }
}
