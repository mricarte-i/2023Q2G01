package org.example;

import java.util.Objects;
import org.example.integrators.ForceCalculator;
import org.example.integrators.Integrator;
import org.example.integrators.VerletIntegrator;

public class Particle {
  private double radius, mass, u;
  private Integrator integrator;
  private boolean leftContact, rightContact;
  private Particle leftNeighbour, rightNeighbour;

  public Particle(double radius, double mass, double u, boolean leftContact,
      boolean rightContact, Particle leftNeighbour, Particle rightNeighbour, double pos, double dT, double v) {
    this.radius = radius;
    this.mass = mass;
    this.u = u;
    this.integrator = new VerletIntegrator(dT, pos, v, mass, this::totalForce);
    this.leftContact = leftContact;
    this.rightContact = rightContact;
    this.leftNeighbour = leftNeighbour;
    this.rightNeighbour = rightNeighbour;
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
    integrator.advanceStep(this::totalForce);
  }

  private boolean checkLeftNeighbourContact() {
    return Math.abs(getPosition() - leftNeighbour.getPosition()) < 2.0 * radius; // Assumes equal particle radius
  }

  private boolean checkRightNeighbourContact() {
    return Math.abs(getPosition() - rightNeighbour.getPosition()) < 2.0 * radius; // Assumes equal particle radius
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

  private double totalForce(double x, double v) {
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
    return Double.compare(particle.radius, radius) == 0
        && Double.compare(particle.mass, mass) == 0 && leftContact == particle.leftContact
        && rightContact == particle.rightContact && Objects.equals(integrator,
            particle.integrator)
        && Objects.equals(leftNeighbour, particle.leftNeighbour)
        && Objects.equals(rightNeighbour, particle.rightNeighbour);
  }

  @Override
  public int hashCode() {
    return Objects.hash(radius, mass, integrator, leftContact, rightContact, leftNeighbour,
        rightNeighbour);
  }

  public double getPosition() {
    return integrator.getPosition();
  }

  public double getVelocity() {
    return integrator.getVelocity();
  }
}
