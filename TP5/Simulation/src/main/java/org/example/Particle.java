package org.example;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import org.example.integrators.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Particle {

  private int id;
  /*
  private double radius, mass, u, initR, initV, boundary;
  private BeemanIntegrator integrator;
  private boolean leftContact, rightContact;
  private Particle leftNeighbour, rightNeighbour;
   */

  /*public Particle(int id, double radius, double mass, double u, boolean leftContact,
      boolean rightContact, Particle leftNeighbour, Particle rightNeighbour, double pos, double dT, double v, double boundary) {
    this.id = id;
    this.radius = radius;
    this.mass = mass;
    this.u = u;
    this.initR = pos;
    this.initV = v;
    this.leftContact = leftContact;
    this.rightContact = rightContact;
    this.leftNeighbour = leftNeighbour;
    this.rightNeighbour = rightNeighbour;
    //this.integrator = new Gear5Integrator(pos, v, totalForceCurrent(pos, v) / mass, - (totalForceCurrent(pos, v) / mass) / mass, ((totalForceCurrent(pos, v) / mass) / mass) / mass, - (((totalForceCurrent(pos, v) / mass) / mass) / mass) / mass, dT, mass, true, boundary);
    this.integrator = new BeemanIntegrator(dT, pos, v, mass, this::totalForceCurrent, boundary);
    //this.integrator = new VerletIntegrator(dT, pos, v, mass, this::totalForceCurrent , boundary);
    this.boundary = boundary;
  }*/

  public double getRadius() {
    throw new NotImplementedException();
  }

  public void setRadius(double radius) {
    throw new NotImplementedException();
  }

  public double getMass() {
    throw new NotImplementedException();
  }

  public void setMass(double mass) {
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
