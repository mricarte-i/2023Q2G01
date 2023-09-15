package org.example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

public class ParticleCollisionSystem {

    private BigDecimal simTime;
    private int maxEvents, postEq;
    private Collection<Particle> particles, involvedParticles;
    private PriorityQueue<Event> eventQueue;
    private ParamsParser paramsParser;
    private SimulationWriter simulationWriter;
    private OutputWriter outputWriter;

    public ParticleCollisionSystem() {
        eventQueue = new PriorityQueue<>();
        init();
    }

    private void init() {
        paramsParser = ParamsParser.getInstance();
        simulationWriter = new SimulationWriter(paramsParser.getStaticPath(), paramsParser.getDynamicPath());
        simulationWriter.writeStatic();
        outputWriter = new OutputWriter();

        simTime = BigDecimal.ZERO;
        particles = new ArrayList<>(paramsParser.getParticles()); // making a copy of the initial
        // particles, will be used for
        // calculating Z
        maxEvents = paramsParser.getEventsTillEq();
        postEq = paramsParser.getEventsPostEq();
    }

    private void evolve(double t) {
        // steps to time t (event @time t)
        for (Particle p : particles) {
            p.updatePosition(t);
        }
    }

    private void applyCollisions(Event event) {
        // updates particles velocity & tallies up collisions that happened (might be
        // kept in some state or kept in each particle)
        Particle p1 = event.getParticleA();
        Particle p2 = event.getParticleB();

        involvedParticles = new ArrayList<>();

        if (p1 != null && p2 != null) {
            p1.bounce(p2);
            involvedParticles.add(p1);
            if(p2 != p1.getLowerVertex() && p2 != p1.getUpperVertex()){
                involvedParticles.add(p2);
            }
        } else if (p1 != null && p2 == null) {
            involvedParticles.add(p1);
            switch (event.getCollisionType()) {
                case X:
                    p1.bounceX();
                    break;
                case Y:
                    p1.bounceY();
                    break;
                default:
                    throw new RuntimeException("Event with only one particle and an unexpected WallCollision type");
            }
        } else {
            throw new RuntimeException("Event without any particles");
        }
    }

    private void updateCollisions() {
        updateCollisions(particles);
    }
    private void updateCollisions(Collection<Particle> fromParticles) {
        // recalculates possible collisions using the list of particles that have
        // collided (see applyCollisions tally)
        for (Particle p1 : fromParticles) {
            // particle v. particle collisions
            for (Particle p2 : particles) {
                if (!p1.equals(p2)) {
                    double timeUntilCollision = p1.collides(p2);
                    if (Double.isFinite(timeUntilCollision)) {
                        BigDecimal time = BigDecimal.valueOf(timeUntilCollision).add(simTime);
                        eventQueue.add(new Event(time, p1, p2));
                    }
                }
            }
            // convex vertex collisions
            double tUpperVertex = p1.collides(p1.getUpperVertex());
            if (Double.isFinite(tUpperVertex)) {
                BigDecimal time = BigDecimal.valueOf(tUpperVertex).add(simTime);
                eventQueue.add(new Event(time, p1, p1.getUpperVertex(), WallCollision.VERTEX));
            }
            double tLowerVertex = p1.collides(p1.getLowerVertex());
            if (Double.isFinite(tLowerVertex)) {
                BigDecimal time = BigDecimal.valueOf(tLowerVertex).add(simTime);
                eventQueue.add(new Event(time, p1, p1.getLowerVertex(), WallCollision.VERTEX));
            }

            // wall collisions
            double tCollX = p1.collidesX();
            if (Double.isFinite(tCollX)) {
                BigDecimal time = BigDecimal.valueOf(tCollX).add(simTime);
                eventQueue.add(new Event(time, p1, null, WallCollision.X));
            }
            double tCollY = p1.collidesY();
            if (Double.isFinite(tCollY)) {
                BigDecimal time = BigDecimal.valueOf(tCollY).add(simTime);
                eventQueue.add(new Event(time, p1, null, WallCollision.Y));
            }
        }
    }

    private void saveState() {
        simulationWriter.writeDynamic(particles, simTime);
    }

    private void writeOutput(Event event, boolean isInEq) {
        // calls OutputWriter.addTransferedImpulse and .writeZ
        // prints pressures and Z over time (used in observables)
        if(event.getCollisionType() != null){
            outputWriter.addTransferredImpulse(simTime, event);
        }

        if(isInEq){
            outputWriter.writeZ(simTime, particles);
        }

        // NOTE: print Z_i only after eq is reached (eventsTillEq) for the number of
        // events given (eventsPostEq)
        // use OutputWriter class!
    }

    public void simulate() {
        Event event;
        double deltaT;
        saveState(); //initial state
        updateCollisions(); //initial events (all particles are involved)
        int eventsParsed = 0;
        while (eventsParsed < (maxEvents + postEq)) {
            try {
                event = eventQueue.remove();
            } catch (Exception e) {
                // TODO: handle empty eventQueue
                break;
            }
            if (event.isInvalid()) {
                continue; // skip loop without adding up to eventsParsed
            }

            deltaT = event.getTime().subtract(simTime).doubleValue(); //get deltaT
            simTime = event.getTime();

            evolve(deltaT);
            applyCollisions(event);
            saveState();
            writeOutput(event, eventsParsed >= maxEvents);
            updateCollisions(involvedParticles); //only recalculate for latest involved particles

            // etc...
            eventsParsed++;
        }



        // iterate for a certain time or until some calculation of equilibrium is met
        // - pick the first event in the eventQueue, check for validity (if not, remove
        // from queue and get next event that is valid)
        // - evolve(event.time) moves particles to event's time
        // - saveState(event.time) writes current positions and velocities...
        // - applyCollisions() / could also just call p1.bounce(p2)
        // - writeOutputs()
        // - remove first valid event (this one) from the eventQueue
        // - updateCollsions() gets new events

        simulationWriter.closeDynamic();
        outputWriter.close();
    }
}
