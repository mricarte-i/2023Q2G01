package org.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

public class ParticleCollisionSystem {

    private Collection<Particle> particles, initParticles;
    private PriorityQueue<Event> eventQueue;

    public ParticleCollisionSystem() {
        eventQueue = new PriorityQueue<>();
        init();
    }

    private void init() {
        initParticles = ParamsParser.getInstance().getParticles();
        particles = new ArrayList<>(initParticles); // making a copy of the initial
                                                    // particles, will be used for
                                                    // calculating Z
    }

    private void evolve() {
        // steps to time t (event @time t)
    }

    private void applyCollisions() {
        // updates particles velocity & tallies up collisions that happened (might be
        // kept in some state or kept in each particle)
    }

    private void updateCollisions() {
        // recalculates possible collisions using the list of particles that have
        // collided (see applyCollisions tally)
        for (Particle p1 : particles) {
            // particle v. particle collisions
            for (Particle p2 : particles) {
                if (!p1.equals(p2)) {
                    double timeUntilCollision = p1.collides(p2);
                    if (timeUntilCollision != Double.POSITIVE_INFINITY) {
                        eventQueue.add(new Event(timeUntilCollision, p1, p2));
                    }
                }
            }
            // wall collisions
            double tCollX = p1.collidesX();
            if (tCollX != Double.POSITIVE_INFINITY) {
                eventQueue.add(new Event(tCollX, p1, null));
            }
            double tCollY = p1.collidesY();
            if (tCollY != Double.POSITIVE_INFINITY) {
                eventQueue.add(new Event(tCollY, p1, null));
            }
            // TODO: static point (aka. convex vertices) collision check
        }
    }

    private void saveState() {
        // prints current state to output dynamic
    }

    private double getPressureFromFirstContainer() {
        // returns pressure inside the first container
        return 0;
    }

    private double getPressureFromSecondContainer() {
        // returns pressure inside the other container
        return 0;
    }

    // idea: make a getPressureFromContainer(container bounds)

    private void writeOutput() {
        // prints pressures and Z over time (used in observables)
    }

    private double getZ(Particle p) {
        // returns square distance to particles origin
        Particle pOriginal = initParticles.stream().filter(pi -> pi.equals(p)).findFirst().orElse(null);
        if (pOriginal == null) {
            throw new RuntimeException("Could not find particle in initial particles???");
        }
        double vx = p.getPositionX() - pOriginal.getPositionX();
        double vy = p.getPositionY() - pOriginal.getPositionY();
        return ((vx * vx) + (vy * vy));
    }

    public void simulate() {
        // runs the whole thing:
        /**
         * 1. calls updateCollisions
         * 2. iterates for each event: (check if event is still valid, if not skip)
         * - a. evolve()
         * - b. saveState()
         * - c. applyCollisions()
         * - d. updateCollisions()
         * - e. writeOutputs()
         */

        updateCollisions();
        // iterate for a certain time or until some calculation of equilibrium is met
        // - pick the first event in the eventQueue, check for validity (if not, remove
        // from queue and get next event that is valid)
        // - evolve() moves particles to event's time
        // - saveState() writes current positions and velocities...
        // - applyCollisions() / could also just call p1.bounce(p2)
        // - remove first valid event (this one) from the eventQueue
        // - updateCollsions() gets new events
    }
}
