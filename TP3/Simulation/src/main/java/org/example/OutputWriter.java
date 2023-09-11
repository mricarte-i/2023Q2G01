package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;

public class OutputWriter {
  // NOTE: maybe two files for each pressure? or all in one?
  private FileWriter fwZs, fwPressures;
  private String fnZs, fnPressures;
  private Collection<Particle> initParticles;
  private double impulseA, impulseB, border, dT;
  private BigDecimal fromTime, deltaT;
  private double wA, hA, wB, hB;

  public OutputWriter(){
    ParamsParser paramsParser = ParamsParser.getInstance();
    fnZs = paramsParser.getOutputPath() + "-Zs";
    try {
      File file = new File(fnZs + ".txt");
      if (file.exists()) {
        file.delete();
      }
      file.createNewFile();
      this.fwZs = new FileWriter(fnZs + ".txt", true);
    } catch (IOException e) {
      System.out.println("Error creating file " + fnZs + ".txt");
    }

    dT = paramsParser.getDeltaT();
    deltaT = BigDecimal.valueOf(paramsParser.getDeltaT());
    fromTime = BigDecimal.ZERO;
    impulseA = impulseB = 0;
    wA = wB = border = paramsParser.getW();
    hA = paramsParser.getH();
    hB = paramsParser.getL();
    initParticles = paramsParser.getParticles();

    fnPressures = paramsParser.getOutputPath() + "-pressures";
    try {
      File file = new File(fnPressures + ".txt");
      if (file.exists()) {
        file.delete();
      }
      file.createNewFile();
      this.fwPressures = new FileWriter(fnPressures + ".txt", true);
    } catch (IOException e) {
      System.out.println("Error creating file " + fnPressures + ".txt");
    }
  }

  public void addTransferredImpulse(BigDecimal simTime, Event event) {
    if(simTime.subtract(fromTime).compareTo(deltaT) == 1){
      writePressures();
      impulseA = impulseB = 0;
      fromTime = simTime;
    }


    Particle p = event.getParticleA();
    double transferredVelocity;
    switch (event.getCollisionType()){
      case X:
        transferredVelocity = p.getVx();
        break;
      case Y:
        transferredVelocity = p.getVy();
        break;
      default:
        return; //ignore this collision event
    }
    double impulse = p.getMass() * Math.abs(transferredVelocity);
    if(p.getPositionX() < border){
      impulseA += impulse;
    }else{
      impulseB += impulse;
    }

    // figure out in which container by checking the x position agaist params.w

    // keep a buffer of impulse taken by each container within a certain delta time
    // once given an event that is beyond that delta time is received, sum it all
    // up, divide by the container's perimeter and delta, then print to file
    // or when close() is called
  }

  private void writePressures() {
    try {
      this.fwPressures.write(fromTime.add(deltaT).toString() + "\n");
      double pressureA = impulseA / ((2*wA + 2*hA) * dT);
      double pressureB = impulseB / ((2*wB + 2*hB) * dT);
      this.fwPressures.write(pressureA + " " + pressureB + "\n");
    } catch (IOException e) {
      System.out.println("Error writing to file " + fnPressures + ".txt");
    }
  }

  public void writeZ(BigDecimal simTime, Collection<Particle> currParticles) {
    // calculate each z_i against initialPositions
    // print to file
    try {
      this.fwZs.write(simTime.toString() + "\n");
      for (Particle p : currParticles) {
        this.fwZs.write(getZ_i(p) + "\n");
      }
    } catch (IOException e) {
      System.out.println("Error writing to file " + fnZs + ".txt");
    }
  }

  public void close(){
    try{
      this.fwZs.close();
      this.fwPressures.close();
    }catch (IOException e){
      throw new RuntimeException("Error closing the files " + fnZs + ".txt and " + fnPressures +".txt");
    }
    System.out.println("Successfully written to "+ fnZs + ".txt and " + fnPressures +".txt");
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
}
