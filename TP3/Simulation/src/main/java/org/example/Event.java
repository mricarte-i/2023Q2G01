package org.example;

public class Event {
    private double t;
    private Integer collisionsP1, collisionsP2; // on instantiation, save both particles getCollisionCount
    // we will use that to check if this event is still valid
    private Particle p1, p2;

    public Event(double t, Particle a, Particle b) {

    }

    public double getTime() {
        return 0;
    }

    public Particle getParticle1() {
        return this.p1;
    }

    public Particle getParticle2() {
        return this.p2;
    }

    // compare times, the highest priority should be the earliest times
    public int compareTo(Object o) {
        return 0;
    }

    // if collisionP1 == p1.getCollisionCount() that means
    // that the particle hasn't been hit with a new unknown at the time event, so we
    // can still use this event!
    public boolean wasSuperveningEvent() {
        return false;
    }

}
