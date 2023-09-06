package org.example;

public class ParticleCollisionSystem {

    public ParticleCollisionSystem(){

    }

    private void init(){
        //parse args
        //parse files
    }

    private void evolve(){
        //steps to time t (event @time t)
    }

    private void applyCollisions(){
        //updates particles velocity & tallies up collisions that happened (might be kept in some state or kept in each particle)
    }

    private void updateCollisions(){
        //recalculates possible collisions using the list of particles that have collided (see applyCollisions tally)
    }

    private void saveState(){
        //prints current state to output dynamic
    }

    private double getPressureFromFirstContainer(){
        //returns pressure inside the first container
        return 0;
    }

    private double getPressureFromSecondContainer(){
        //returns pressure inside the other container
        return 0;
    }

    //idea: make a getPressureFromContainer(container bounds)

    private void writeOutput(){
        //prints pressures and Z over time (used in observables)
    }

    private double getZ(Particle p){
        //returns distance moved by particle
        return 0;
    }

    private void simulate(){
        //runs the whole thing:
        /**
         * 1. calls updateCollisions
         * 2. iterates for each event:
         *  a. evolve()
         *  b. saveState()
         *  c. applyCollisions()
         *  d. writeOutputs()
         */
    }
}
