package org.example.off_latice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.example.particles.OffLaticeParticle2D;
import org.example.particles.SimpleOffLaticeParticle2D;
import org.example.points.Point2D;
import org.example.points.SimpleVector2D;
import org.example.points.Vector2D;

public class OffLaticeEvolver<P extends OffLaticeParticle2D> implements Evolver<Point2D, P> {

  private double noiseAmplitude;

  public OffLaticeEvolver(double noiseAmplitude) {
    this.noiseAmplitude = noiseAmplitude;
  }

  @Override
  public Collection<P> update(Collection<P> particles, Map<P, Collection<P>> neighboursData) {
    Collection<P> nextParticles = new ArrayList<>();

    for (P particle : particles) {
      double newPosX = particle.getX() + particle.xVelocity(); // deltaTime is always equal to one?
      double newPosY = particle.getY() + particle.yVelocity();

      Vector2D averageNeighbourhoodVelocity = calculateAverageVelocityVector2D(neighboursData.get(particle));
      double newVelX = averageNeighbourhoodVelocity.xAxisProjection() + this.noiseAmplitude;
      double newVelY = averageNeighbourhoodVelocity.yAxisProjection() + this.noiseAmplitude;
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

}
