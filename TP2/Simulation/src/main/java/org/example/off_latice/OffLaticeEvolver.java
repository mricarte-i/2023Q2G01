package org.example.off_latice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Random;

import org.example.particles.OffLaticeParticle2D;
import org.example.particles.SimpleOffLaticeParticle2D;
import org.example.points.Point2D;
import org.example.points.SimpleVector2D;
import org.example.points.Vector2D;

public class OffLaticeEvolver<P extends OffLaticeParticle2D> implements Evolver<Point2D, P> {

  private double noiseAmplitude;
  private double sideLength;
  private Random rand;

  public OffLaticeEvolver(double noiseAmplitude, double sideLength) {
    this.noiseAmplitude = noiseAmplitude;
    this.sideLength = sideLength;
    this.rand = new Random();
  }

  public OffLaticeEvolver(double noiseAmplitude, double sideLength, long seed) {
    this.noiseAmplitude = noiseAmplitude;
    this.sideLength = sideLength;
    this.rand = new Random(seed);
  }

  @Override
  public Collection<P> update(Collection<P> particles, Map<P, Collection<P>> neighboursData) {
    Collection<P> nextParticles = new ArrayList<>();

    for (P particle : particles) {
      // deltaTime is always equal to one?
      double newPosX = particle.getX() + particle.xVelocity();
      double newPosY = particle.getY() + particle.yVelocity();

      newPosX = clampExclusive(newPosX, 0, this.sideLength);
      newPosY = clampExclusive(newPosY, 0, this.sideLength);


      if(neighboursData.get(particle) == null){
        throw new RuntimeException("no neighbor data for " + particle.getId());
      }
      Vector2D averageNeighbourhoodVelocity = calculateAverageVelocityVector2D(neighboursData.get(particle));
      // number between [-eta/2; +eta/2]
      double noise = (this.noiseAmplitude * rand.nextDouble()) - (this.noiseAmplitude / 2);
      double newVelX = averageNeighbourhoodVelocity.xAxisProjection() + noise;
      double newVelY = averageNeighbourhoodVelocity.yAxisProjection() + noise;
      Vector2D newVel = new SimpleVector2D(1, newVelX, newVelY);

      P newParticle = (P) new SimpleOffLaticeParticle2D(particle.getId(), newPosX, newPosY,
          particle.getVelocityMagnitude(), newVel.getAngle(), particle.getRadius());
      nextParticles.add(newParticle);
    }

    return nextParticles;
  }

  private Vector2D calculateAverageVelocityVector2D(Collection<P> particles) {
    double averageSin = 0;
    double averageCos = 0;
    for (P p : particles) {
      averageSin = p.getVelocity().yAxisProjectionAngle();
      averageCos = p.getVelocity().xAxisProjectionAngle();
    }
    averageSin /= particles.size();
    averageCos /= particles.size();

    double averageAngle = Math.atan(averageSin / averageCos);
    return new SimpleVector2D(1, averageAngle);
  }

  private double clampExclusive(double value, double minInclusive, double maxExclusive) {
    return Math.max(minInclusive, Math.min(maxExclusive, value)) % maxExclusive;
  }

}
