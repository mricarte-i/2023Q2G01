package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

public class ParticleCollisionSystem {

    private double simTime;
    private int maxEvents, postEq;
    private Collection<Particle> particles;
    private PriorityQueue<Event> eventQueue;
    private FileWriter fileWriter;
    private ParamsParser paramsParser;

    public ParticleCollisionSystem() {
        eventQueue = new PriorityQueue<>();
        init();
    }

    private void init() {
        paramsParser = ParamsParser.getInstance();
        simTime = 0;
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

        if (p1 != null && p2 != null) {
            p1.bounce(p2);
        } else if (p1 != null && p2 == null) {
            switch (event.getCollisionType()) {
                case X:
                    p1.bounceX();
                    break;
                case Y:
                    p1.bounceY();
                    break;

            }
        }
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
            // convex vertex collisions
            double tUpperVertex = p1.collides(p1.getUpperVertex());
            if (tUpperVertex != Double.POSITIVE_INFINITY) {
                eventQueue.add(new Event(tUpperVertex, p1, p1.getUpperVertex(), WallCollision.VERTEX));
            }
            double tLowerVertex = p1.collides(p1.getLowerVertex());
            if (tUpperVertex != Double.POSITIVE_INFINITY) {
                eventQueue.add(new Event(tLowerVertex, p1, p1.getLowerVertex(), WallCollision.VERTEX));
            }

            // wall collisions
            double tCollX = p1.collidesX();
            if (tCollX != Double.POSITIVE_INFINITY) {
                eventQueue.add(new Event(tCollX, p1, null, WallCollision.X));
            }
            double tCollY = p1.collidesY();
            if (tCollY != Double.POSITIVE_INFINITY) {
                eventQueue.add(new Event(tCollY, p1, null, WallCollision.Y));
            }
        }
    }

    private void openFile() {
        // TODO: add fileName param
        // this.fileName = fileName;
        try {
            // TODO: add fileName param
            // File file = new File(fileName + ".txt");
            File file = new File("output.txt");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            // TODO: add fileName param
            // this.fileWriter = new FileWriter(fileName + ".txt", true);
            this.fileWriter = new FileWriter("output.txt", true);
        } catch (IOException e) {
            // TODO: add fileName param
            // System.out.println("Error creating file " + fileName + ".txt");
            System.out.println("Error creating file output.txt");
        }

    }

    public void writeFile(double timestamp) {
        try {
            this.fileWriter.write(timestamp + "\n");
            for (Particle p : particles) {
                this.fileWriter.write(p.getPositionX() + " " + p.getPositionY() + " " + p.getVx() + " "
                        + p.getVy() + " " + p.getMass() + " " + p.getRadius() + "\n");
            }
        } catch (IOException e) {
            // TODO: add fileName param
            // System.out.println("Error writing file " + this.fileName + ".txt");
            System.out.println("Error writing file output.txt");
        }
    }

    public void closeFile() {
        try {
            this.fileWriter.close();
        } catch (IOException e) {
            // TODO: add fileName param
            // System.out.println("Error closing file " + this.fileName + ".txt");
            System.out.println("Error closing file output.txt");
        }
    }

    private void saveState(double timestamp) {
        if (fileWriter == null)
            openFile();
        writeFile(timestamp);
    }

    private void writeOutput(boolean isInEq) {
        // calls OutputWriter.addTransferedImpulse and .writeZ
        // prints pressures and Z over time (used in observables)

        // NOTE: print Z_i only after eq is reached (eventsTillEq) for the number of
        // events given (eventsPostEq)
        // use OutputWriter class!
    }

    public void simulate() {
        Event event;
        double t;

        updateCollisions();
        int eventsParsed = 0;
        while (eventsParsed < (maxEvents + postEq)) {
            try {
                event = eventQueue.remove();
            } catch (Exception e) {
                // TODO: handle empty eventQueue
                break;
            }
            if (!event.wasSuperveningEvent()) {
                continue; // skip loop without adding up to eventsParsed
            }

            // TODO: is it ok to evolve with event.getTime()?
            t = event.getTime();
            simTime += t;

            evolve(t);
            saveState(t); // TODO: pass time and not event.time when writing
            applyCollisions(event);
            writeOutput(eventsParsed >= maxEvents);

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

        closeFile();
    }
}
