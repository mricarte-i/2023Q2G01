package org.example;

import java.util.List;

public class ParticleSystem implements Simulation{
    private ParamsParser paramsParser;
    private List<Particle> particles;
    private double TF = 180;
    private Writer writer;
    private static ParticleSystem particleSystem = null;
    private ParticleSystem(){
    }

    public static ParticleSystem getInstance() {
        if(particleSystem == null){
            particleSystem = new ParticleSystem();
        }
        return particleSystem;
    }
    @Override
    public void simulate() {
        this.paramsParser = ParamsParser.getInstance();
        this.particles = paramsParser.getParticles();
        this.writer = new Writer(paramsParser.getDynamicFile());
        this.writer.writeStatic(paramsParser.getStaticFile(), paramsParser.getL(), this.particles);

        double currStep = Math.pow(10, -paramsParser.getDeltaTimeScale());
        double writeStep = Math.pow(10, -paramsParser.getStateDeltaTimeScale());
        double time = 0;
        int stepCounter = 0;
        //print dynamic state @t=0
        this.writer.writeState(time, this.particles);

        while(time < this.TF) {
            for(Particle p : this.particles){
                p.evaluateForce();
            }
            for(Particle p : this.particles){
                p.advanceStep();
            }
            for(Particle p : this.particles){
                p.checkNeighbourContacts();
            }

            time += currStep;
            stepCounter++;
            if(stepCounter >= writeStep / currStep){
                this.writer.writeState(time, this.particles);
                stepCounter = 0;
            }
        }
        this.writer.close();
    }
}
