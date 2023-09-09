package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.PriorityQueue;

public class ParticleCollisionSystem {

    private Collection<Particle> particles, initParticles;
    private PriorityQueue<Event> eventQueue;
    private FileWriter fileWriter;

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

    private void evolve(double t) {
        // steps to time t (event @time t)
        for (Particle p : particles) {
            p.updatePosition(t);
        }
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

    private void openFile() {
        // TODO: add fileName param
        // this.fileName = fileName;
        try{
            // TODO: add fileName param
            // File file = new File(fileName + ".txt");
            File file = new File("output.txt");
            if(file.exists()){
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

    public void writeFile (double timestamp) {
        try {
            this.fileWriter.write( timestamp + "\n");
            for(Particle p: particles){
                this.fileWriter.write(p.getPositionX() + " " + p.getPositionY() + " " + p.getVx() + " "
                        + p.getVy() + " " + p.getMass() + " " + p.getRadius() + "\n");
            }
        } catch (IOException e) {
            // TODO: add fileName param
            // System.out.println("Error writing file " + this.fileName + ".txt");
            System.out.println("Error writing file output.txt");
        }
    }

    public void closeFile(){
        try {
            this.fileWriter.close();
        }catch (IOException e){
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

    private double getPressureFromFirstContainer() {
        // returns pressure inside the first container
        return 0;
    }

    private double getPressureFromSecondContainer() {
        // returns pressure inside the other container
        return 0;
    }

    // TODO:
    // private double getPressureFromContainer(double x0, x1, x2, x3){
    // particles inside / area;
    // }

    private void writeOutput() {
        // prints pressures and Z over time (used in observables)
    }

    private double getZ() {
        // returns average getZ_i for each
        return 0;
    }

    private double getZ_i(Particle p) {
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

        updateCollisions();
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
