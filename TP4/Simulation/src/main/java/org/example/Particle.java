package org.example;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

import org.example.integrators.*;

public class Particle {

  private int id;
  private double radius, mass, u, initR, initV, boundary;
  private BeemanIntegrator integrator;
  private boolean leftContact, rightContact;
  private Particle leftNeighbour, rightNeighbour;

  public Particle(int id, double radius, double mass, double u, boolean leftContact,
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
  }

  public double getRadius() {
    return radius;
  }

  public void setRadius(double radius) {
    this.radius = radius;
  }

  public double getMass() {
    return mass;
  }

  public void setMass(double mass) {
    this.mass = mass;
  }

  public boolean isLeftContact() {
    return leftContact;
  }

  public void setLeftContact(boolean leftContact) {
    this.leftContact = leftContact;
  }

  public boolean isRightContact() {
    return rightContact;
  }

  public void setRightContact(boolean rightContact) {
    this.rightContact = rightContact;
  }

  public Particle getLeftNeighbour() {
    return leftNeighbour;
  }

  public void setLeftNeighbour(Particle leftNeighbour) {
    this.leftNeighbour = leftNeighbour;
  }

  public Particle getRightNeighbour() {
    return rightNeighbour;
  }

  public void setRightNeighbour(Particle rightNeighbour) {
    this.rightNeighbour = rightNeighbour;
  }

  /*public void predict() {
    integrator.predict();
  }*/

  public void evaluateForce() {
    integrator.evaluateForce(this::totalForceNext);
  }

  public void advanceStep() {
    integrator.advanceStep();
    this.setLeftContact(false);
    this.setRightContact(false);
  }

  private double getMinDiffToParticle(double x, Supplier<Double> getNeighbourX) {
    double particleX = getNeighbourX.get();

    double directDiff = particleX - x;
    double wrapAroundDiff = x < particleX ?
            -(x + (this.boundary - particleX)) :
            particleX + (this.boundary - x);

    if (Math.abs(directDiff) > Math.abs(wrapAroundDiff))
      return wrapAroundDiff;
    return directDiff;
  }

  private boolean checkLeftNeighbourContact(double x) {
    return Math.abs(getMinDiffToParticle(x, leftNeighbour.integrator::getNextPosition)) <= (radius + leftNeighbour.radius); // Assumes equal particle radius
  }

  private boolean checkRightNeighbourContact(double x) {
    return Math.abs(getMinDiffToParticle(x, rightNeighbour.integrator::getNextPosition)) <= (radius + rightNeighbour.radius); // Assumes equal particle radius
  }

  public void checkNeighbourContacts() {
    double nextPosition = integrator.getNextPosition();
    this.setLeftContact(checkLeftNeighbourContact(nextPosition));
    this.setRightContact(checkRightNeighbourContact(nextPosition));
  }

  private double propulsionForce(double v) {
    double tao = 1.0; // Constant value = 1 second
    return (u - v) / tao;
  }

  private double leftContactForce(double x, Supplier<Double> leftNeighbour) {
    if(!this.leftContact) return 0;
    return contactForceWithNeighbour(x, leftNeighbour);
  }

  private double rightContactForce(double x, Supplier<Double> rightNeighbour) {
    if(!this.rightContact) return 0;
    return contactForceWithNeighbour(x, rightNeighbour);
  }

  private double contactForceWithNeighbour(double x, Supplier<Double> neighbour) {
    double k = 2500.0; // Constant value 2500 g / s**2
    return k * (Math.abs(getMinDiffToParticle(x, neighbour)) - 2.0 * radius)
        * Math.signum(getMinDiffToParticle(x, neighbour));
  }

  private double totalForceCurrent(double x, double v) {
    Supplier<Double> leftNeighbourGetPosition = leftNeighbour != null ? leftNeighbour::getPosition : null;
    Supplier<Double> rightNeighbourGetPosition = rightNeighbour != null ? rightNeighbour::getPosition : null;
    return totalForce(x, v, leftNeighbourGetPosition, rightNeighbourGetPosition);
  }


  private double totalForceNext(double x, double v) {
    return totalForce(x, v, leftNeighbour.integrator::getNextPosition, rightNeighbour.integrator::getNextPosition);
  }

  private double totalForce(double x, double v, Supplier<Double> leftNeighbour, Supplier<Double> rightNeighbour) {
    return rightContactForce(x, rightNeighbour) + leftContactForce(x, leftNeighbour) + propulsionForce(v);
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
    if(integrator == null) return this.initR;
    return integrator.getPosition();
  }

  public double getVelocity() {
    if(integrator == null) return this.initV;
    return integrator.getVelocity();
  }
}
