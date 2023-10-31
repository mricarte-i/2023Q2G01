package org.example;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.Random;

public class ParticleSystem implements SimulationSystem {
    private ParamsParser paramsParser;
    private List<Particle> particles;
    private Writer writer;
    private double TF = 1000;
    private double GRAVITY = 0.098; //en la consulta dijieron usar 9.8 cm/s^2 para la gravedad
    private Random random;
    private double baseY = 0;
    private double W, L, D;

    public ParticleSystem() {
        this.paramsParser = ParamsParser.getInstance();
        this.writer = new Writer(paramsParser.getDynamicFile());
        //set up writer and paramsparser
        random = paramsParser.getRandom();
        W = paramsParser.getW();
        L = paramsParser.getL();
        D = paramsParser.getD();
    }

    private void initParticles(){
        for (Particle particle : this.particles) {
            particle.initialize();
        }
    }

    private void calculateNextNeighbors(){
        throw new NotImplementedException();
        //use cell index to get a map of all neighbors for each particle
    }

    private void advanceBase(double time) {
        baseY = paramsParser.getA()*Math.sin(paramsParser.getAngularVelocity()*time);
    }

    private void advanceParticles() {
        for(Particle p: this.particles){
            p.advanceStep();
        }
    }

    private void evaluateParticleForces() {
        for(Particle p: this.particles){
            p.evaluateNextForces();
        }
    }

    private void reinsertParticle(Particle p) {
        double x, y;
        double r = p.getRadius();
        double heightRange = 30; //todo: assumes L = 70, y in [40,70]
        boolean overlaps = false;
        do {
            x = random.nextDouble() * (W - 2*r) + r;
            y = random.nextDouble() * (heightRange - 2*r) + r + 40;
            overlaps = false;

            for(Particle other: particles) {
                if(!other.equals(p)){
                    double dx = x - other.getPositionX();
                    double dy = y - other.getPositionY();
                    double minDist = r + other.getRadius();

                    if((dx*dx) + (dy*dy) <= (minDist*minDist)){
                        overlaps = true;
                        break; //overlapped with another particle, retry
                    }
                }
            }
        } while(overlaps);

        p.reinsert();
    }

    private void reinsertParticles() {
        double lowerBound = paramsParser.getLowerOutOfBoundsPosition();
        for(Particle p : this.particles){
            if(p.needsReinsertion(lowerBound)) {
                reinsertParticle(p);
            }
        }
    }

    private void checkNextContacts() {
        double lwx = 0;
        double rwx = W;
        double lvx = (W - D)/2, rvx = (W + D)/2;
        double twy = baseY + L;

        for (Particle pi: this.particles) {
            // 1 - check for particle-particle interactions
            //TODO use CELL INDEX NEIGHBORS instead of looping over everything
            for (Particle pj: this.particles) {
                pi.checkNextStepContact(pj);
            }
            // 2 - check for vertex collisions
            pi.checkNextStepContactLeftVertex(lvx, baseY);
            pi.checkNextStepContactRightVertex(rvx, baseY);
            // 3 - check for base collisions
            pi.checkNextStepContactBase(baseY);
            // 4 - check for wall collisions
            pi.checkNextStepContactLeftWall(lwx);
            pi.checkNextStepContactRightWall(rwx);
            pi.checkNextStepContactTopWall(twy);
        }
    }
    public void simulate() {
        this.particles = paramsParser.getParticles();
        this.writer.writeStatic(paramsParser.getStaticFile(), paramsParser.getL(), paramsParser.getW(), particles);

        double simStep = Math.pow(10, -paramsParser.getStateWritingDeltaTimeExp()); //deltaT should be 3
        double time = 0;
        int iter = 0;

        this.writer.writeState(time, this.particles);

        initParticles();

        while(time < TF) {
            advanceBase(time);
            //TODO cell index
            //calculateNextNeighbors();
            checkNextContacts();
            evaluateParticleForces();
            advanceParticles();
            reinsertParticles();

            iter++;
            time = iter*simStep;

            this.writer.writeState(time, particles);
        }

        this.writer.close();
    }

}
