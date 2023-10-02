package org.example;

import java.util.Objects;
import org.example.integrators.ForceCalculator;
import org.example.integrators.Gear5Integrator;
import org.example.integrators.Integrator;
import org.example.integrators.VerletIntegrator;

public class Particle {

  private int id;
  private double radius, mass, u, initR, initV;
  private Integrator integrator;
  private boolean leftContact, rightContact;
  private Particle leftNeighbour, rightNeighbour;

  private Double calculatedForce = null;

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
    this.evaluateForce();
    //this.integrator = new VerletIntegrator(dT, pos, v, mass, (r, vel) -> calculatedForce , boundary);
    this.integrator = new Gear5Integrator(pos, v, calculatedForce / mass, 0, 0, 0, dT, mass, true, boundary);
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

  public Integrator getIntegrator() {
    return integrator;
  }

  public void setIntegrator(Integrator integrator) {
    this.integrator = integrator;
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

  public void advanceStep() {
    if (calculatedForce == null)
      throw new RuntimeException("advanceStep called before evaluateForce");
    integrator.advanceStep((x, v) -> calculatedForce);
    this.setLeftContact(false);
    this.setRightContact(false);
    calculatedForce = null;
  }

  public void evaluateForce() {
    calculatedForce = totalForce();
  }

  private boolean checkLeftNeighbourContact() {
    return Math.abs(getPosition() - leftNeighbour.getPosition()) <= (radius + leftNeighbour.radius); // Assumes equal particle radius
  }

  private boolean checkRightNeighbourContact() {
    return Math.abs(getPosition() - rightNeighbour.getPosition()) <= (radius + rightNeighbour.radius); // Assumes equal particle radius
  }

  public void checkNeighbourContacts() {
    this.setLeftContact(checkLeftNeighbourContact());
    this.setRightContact(checkRightNeighbourContact());
  }

  private double propulsionForce() {
    double tao = 1.0; // Constant value = 1 second
    return (u - getVelocity()) / tao;
  }

  private double leftContactForce() {
    if(!this.leftContact) return 0;
    return contactForceWithNeighbour(leftNeighbour);
  }

  private double rightContactForce() {
    if(!this.rightContact) return 0;
    return contactForceWithNeighbour(rightNeighbour);
  }

  private double contactForceWithNeighbour(Particle neighbour) {
    double k = 2500.0; // Constant value 2500 g / s**2
    return k * (Math.abs(neighbour.getPosition() - getPosition()) - 2.0 * radius)
        * Math.signum(neighbour.getPosition() - getPosition());
  }

  private double totalForce() {
    return rightContactForce() + leftContactForce() + propulsionForce();
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
