package org.example;

import org.example.integrators.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashSet;
import java.util.Set;

public class Particle {

  private final int id;
  private BeemanIntegrator xIntegrator;
  private BeemanIntegrator yIntegrator;
  private final double mass, radius;
  private Set<Particle> prevContacts, nextContacts;

  private boolean prevLeftWallContact, prevRightWallContact, prevTopWallContact;
  private boolean prevLeftVertexContact, prevBaseContact, prevRightVertexContact;
  private boolean nextLeftWallContact, nextRightWallContact, nextTopWallContact;
  private boolean nextLeftVertexContact, nextBaseContact, nextRightVertexContact;

  private Particle(int id, double radius, double mass, double pos, double dT, double v) {
    this.id = id;
    this.xIntegrator = new BeemanIntegrator(dT, pos, v, mass, (x, y) -> 0);
    this.yIntegrator = new BeemanIntegrator(dT, pos, v, mass, (x, y) -> 0);
    this.radius = radius;
    this.mass = mass;
  }

  public Particle(int id, double radius, double mass, double pos, double dT, double v, Integer totalParticles) {
    this(id, radius, mass, pos, dT, v);
    if (totalParticles != null) {
      this.prevContacts = new HashSet<>(totalParticles);
      this.nextContacts = new HashSet<>(totalParticles);
    } else {
      this.prevContacts = new HashSet<>();
      this.nextContacts = new HashSet<>();
    }
  }

  private static boolean checkContactParticle(double x, double y, double r, double otherX, double otherY, double otherR) {
    double euclideanDistance = Math.sqrt(Math.pow(x - otherX, 2) + Math.pow(y - otherY, 2));
    return euclideanDistance <= r + otherR;
  }

  private static boolean checkContactWall(double position, double r, double wallPosition) {
    if (position < wallPosition)
      return wallPosition - position <= r;
    return position - wallPosition <= r;
  }

  private static boolean checkContactVerticalWall(double x, double r, double wallX) {
    return checkContactWall(x, r, wallX);
  }

  private static boolean checkContactHorizontalWall(double y, double r, double wallY) {
    return checkContactWall(y, r, wallY);
  }

  public double getRadius() {
    throw new NotImplementedException();
  }

  public double getMass() {
    throw new NotImplementedException();
  }

  public void evaluateForce() {
    throw new NotImplementedException();
  }

  public void advanceStep() {
    throw new NotImplementedException();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Particle)) {
      return false;
    }
    Particle particle = (Particle) o;
    return id == particle.id;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(id);
  }

  public double getPosition() {
    throw new NotImplementedException();
  }

  public double getVelocity() {
    throw new NotImplementedException();
  }
}
