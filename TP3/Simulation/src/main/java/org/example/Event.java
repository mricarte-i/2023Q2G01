package org.example;

public class Event {
    private final double t;
    private final Particle particleA, particleB;
    private final int particleACollisionsAtEventCreation, particleBCollisionsAtEventCreation; // on instantiation, save both particles getCollisionCount
    // we will use that to check if this event is still valid

    public Event(double t, Particle particleA, Particle particleB) {
        this.t = t;
        this.particleA = particleA;
        this.particleB = particleB;
        this.particleACollisionsAtEventCreation = particleA.getCollisionCount();
        this.particleBCollisionsAtEventCreation = particleB.getCollisionCount();
    }

    public double getTime() {
        return 0;
    }

    public Particle getParticleA() {
        return this.particleA;
    }

    public Particle getParticleB() {
        return this.particleB;
    }

    // compare times, the highest priority should be the earliest times
    public int compareTo(Object o) {
        return 0;
    }

    public boolean wasSuperveningEvent() {
        return false;
    }

    public boolean isValid() {
        return this.particleA.getCollisionCount() == this.particleACollisionsAtEventCreation && this.particleB.getCollisionCount() == this.particleBCollisionsAtEventCreation;

    }

}
