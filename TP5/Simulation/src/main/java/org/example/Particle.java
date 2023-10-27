package org.example;

import org.example.integrators.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Particle {

  private final int id;
  private BeemanIntegrator xIntegrator;
  private BeemanIntegrator yIntegrator;
  private final double mass, radius;

  /*
  private double radius, mass, u, initR, initV, boundary;
  private BeemanIntegrator integrator;
  private boolean leftContact, rightContact;
  private Particle leftNeighbour, rightNeighbour;
   */

  public Particle(int id, double radius, double mass, double pos, double dT, double v, double boundary) {
    this.id = id;
    this.xIntegrator = new BeemanIntegrator(dT, pos, v, mass, (x, y) -> 0);
    this.yIntegrator = new BeemanIntegrator(dT, pos, v, mass, (x, y) -> 0);
    this.radius = radius;
    this.mass = mass;
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
