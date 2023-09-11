package org.example;

import java.math.BigDecimal;

public class Event implements Comparable<Event> {
    private final WallCollision type;
    private final BigDecimal t;
    private final Particle particleA, particleB;
    private final int particleACollisionsAtEventCreation, particleBCollisionsAtEventCreation; // on instantiation, save
                                                                                              // both particles
                                                                                              // getCollisionCount
    // we will use that to check if this event is still valid

    public Event(BigDecimal t, Particle particleA, Particle particleB) {
        this.t = t;
        this.particleA = particleA;
        this.particleB = particleB;
        this.particleACollisionsAtEventCreation = particleA.getCollisionCount();
        this.particleBCollisionsAtEventCreation = particleB.getCollisionCount();
        this.type = null;
    }

    public Event(BigDecimal t, Particle particleA, Particle particleB, WallCollision type) {
        this.t = t;
        this.particleA = particleA;
        this.particleB = particleB;
        this.particleACollisionsAtEventCreation = particleA.getCollisionCount();
        this.particleBCollisionsAtEventCreation = particleB != null ? particleB.getCollisionCount() : -1;
        this.type = type;
    }

    public WallCollision getCollisionType() {
        return type;
    }

    public BigDecimal getTime() {
        return t;
    }

    public Particle getParticleA() {
        return this.particleA;
    }

    public Particle getParticleB() {
        return this.particleB;
    }

    // compare times, the highest priority should be the earliest times
    @Override
    public int compareTo(Event o) {
        //return Double.compare(this.getTime(), o.getTime());
        return this.getTime().compareTo(o.getTime());
    }

    public boolean isInvalid(){
        if(t.signum() == -1){ //negative time???
            throw new RuntimeException("INVALID TIME");
        }

        return t.signum() == -1 || !wasSuperveningEvent();
    }
    private boolean wasSuperveningEvent() {
        // only the first is "real"
        if (this.particleB == null || this.type == WallCollision.VERTEX) {
            return this.particleA.getCollisionCount() == this.particleACollisionsAtEventCreation;
        }
        // check both
        return this.particleA.getCollisionCount() == this.particleACollisionsAtEventCreation
                && this.particleB.getCollisionCount() == this.particleBCollisionsAtEventCreation;

    }

}
