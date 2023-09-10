package org.example;

import java.util.Collection;

public class OutputWriter {
  // two fileparsers, one for each output (pressures, z's)
  // NOTE: maybe two files for each pressure? or all in one?
  private Collection<Particle> initParticles;

  public void addTransferedImpulse(double simTime, Particle p) {
    // figure out in which container by checking the x position agaist params.w

    // keep a buffer of impulse taken by each container within a certain delta time
    // once given an event that is beyond that delta time is received, sum it all
    // up, divide by the container's perimeter and delta, then print to file
    // or when close() is called
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

  public void writeZ(double simTime, Collection<Particle> currParticles) {
    // calculate each z_i against initialPositions
    // print to file
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

  /*
   * TODO:
   * - write out collision events w/wall (maybe with a way to diff between
   * containers)
   * OR write out pressure values (might need to pass a delta time from params)
   * - write out Z_i over time after some number of events (~t_equilibrium) for
   * some
   * number of events (50?)
   */

}
