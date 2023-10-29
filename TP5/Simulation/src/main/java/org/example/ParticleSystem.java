package org.example;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class ParticleSystem implements SimulationSystem {
    private ParamsParser paramsParser;
    private List<Particle> particles;
    private Writer writer;
    private double TF = 1000;
    private double GRAVITY = 0.098; //en la consulta dijieron usar 9.8 cm/s^2 para la gravedad

    private double baseY = 0;
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

    private void reinsertParticles() {
        double lowerBound = paramsParser.getLowerBound();
        for(Particle p : this.particles){
            if(p.needsReinsertion(lowerBound)) {
                /*TODO:
                    - reset velocity and acceleration
                    - get random x,y (40< y <70) without overlapping
                */
            }
        }
    }

    private void checkNextContacts(double time) {
        double lwx = 0;
        double rwx = paramsParser.getW();
        double lvx = (paramsParser.getW() - paramsParser.getD())/2, rvx = (paramsParser.getW() + paramsParser.getD())/2;
        double twy = baseY + paramsParser.getL();

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
        this.paramsParser = ParamsParser.getInstance();
        this.particles = paramsParser.getParticles();
        this.writer = new Writer();
        this.writer.writeStatic();

        double simStep = Math.pow(10, -paramsParser.getDeltaT());
        double time = 0;
        int iter = 0;
        this.writer.writeState(time, this.particles);

        while(time < TF) {
            advanceBase(time);
            //TODO cell index
            //calculateNextNeighbors();
            checkNextContacts(time);
            evaluateParticleForces();
            advanceParticles();
            reinsertParticles();
            iter++;
            time = iter*simStep;

            this.writer.writeState(time, particles);
        }

        this.writer.closeState();
    }

}
