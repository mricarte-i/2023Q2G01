package org.example;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

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

    private void updateNeighbours() {
        Queue<Particle> particles = new PriorityQueue<>(Comparator.comparingDouble(Particle::getPosition));
        particles.addAll(this.particles);

        Particle firstParticle = null, prevParticle, currParticle = null;
        while (!particles.isEmpty()) {
            prevParticle = currParticle;
            currParticle = particles.poll();

            if (firstParticle == null)
                firstParticle = currParticle;

            if (prevParticle != null)
                prevParticle.setRightNeighbour(currParticle);

            currParticle.setLeftNeighbour(prevParticle);
        }

        if (firstParticle != null && firstParticle != currParticle) {
            firstParticle.setLeftNeighbour(currParticle);
            currParticle.setRightNeighbour(firstParticle);
        }
    }

    @Override
    public void simulate() {
        this.paramsParser = ParamsParser.getInstance();
        this.particles = paramsParser.getParticles();
        this.writer = new Writer(paramsParser.getDynamicFile());
        this.writer.writeStatic(paramsParser.getStaticFile(), paramsParser.getL(), this.particles);

        double simStep = Math.pow(10, -paramsParser.getDeltaTimeScale()); //n
        double writeStep = Math.pow(10, -paramsParser.getStateDeltaTimeScale()); //k

        double writeScale = Math.pow(10, paramsParser.getStateDeltaTimeScale());
        double stepRatio = writeStep / simStep; //(k/n)

        int iter = 0;
        int stepCounter = 0;
        //as a separate variable, we add up the step counter
        //when its equal to the step ratio (k/n) that means we are in a printing step
        //ex: n=10^-2, k=10^-1, every 10 steps we should print the state

        double time = 0;
        //print dynamic state @t=0
        this.writer.writeState(time, this.particles);

        while(time < this.TF) {
            for(Particle p : this.particles) {
                p.predict();
            }
            for(Particle p : this.particles){
                p.checkNeighbourContacts();
            }
            for(Particle p : this.particles){
                p.evaluateForce();
            }
            for(Particle p : this.particles){
                p.advanceStep();
            }
            updateNeighbours();

            iter++;
            time = iter*simStep; //sim time is the number of iterations times n (iter*simStep)

            stepCounter++;
            if(stepCounter == stepRatio){
                double printTime = (iter/stepRatio)*writeStep;
                printTime = Math.round(printTime * writeScale) / writeScale;
                //(number of steps / n)*k gives us the number of steps if we were working with k
                //that times writeStep gives us a nice round number for the print time
                this.writer.writeState(printTime, this.particles);
                stepCounter = 0;
            }
        }
        this.writer.close();
    }
}
