package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;

public class SimulationWriter {

  private FileWriter fwStatic, fwDynamic;
  private String fnStatic, fnDynamic;

  private double leftBound, rightBound, upperBound, lowerBound;

  public SimulationWriter(String fileNameStatic, String fileNameDynamic) {
    this.fnStatic = fileNameStatic;
    try {
      File file = new File(fnStatic + ".txt");
      if (file.exists()) {
        file.delete();
      }
      file.createNewFile();
      this.fwStatic = new FileWriter(fnStatic + ".txt", true);
    } catch (IOException e) {
      System.out.println("Error creating file " + fnStatic + ".txt");
    }

    this.fnDynamic = fileNameDynamic;
    try {
      File file = new File(fnDynamic + ".txt");
      if (file.exists()) {
        file.delete();
      }
      file.createNewFile();
      this.fwDynamic = new FileWriter(fnDynamic + ".txt", true);
    } catch (IOException e) {
      System.out.println("Error creating file " + fnDynamic + ".txt");
    }

    ParamsParser pp = ParamsParser.getInstance();
    this.leftBound = 0;
    this.rightBound = pp.getW() *2;
    this.lowerBound = 0;
    this.upperBound = pp.getH();
  }

  public void writeStatic() {
    ParamsParser pp = ParamsParser.getInstance();
    try {
      this.fwStatic.write(pp.getN() + "\n");
      for (Particle p : pp.getParticles()) {
        this.fwStatic.write(p.getMass() + " " + p.getRadius() + "\n");
      }
      this.fwStatic.close();
    } catch (IOException e) {
      System.out.println("Error writing to file " + fnStatic + ".txt");
    }

    System.out.println("Succesfully written static properties to file: " + fnStatic + ".txt");
  }

  public void writeDynamic(Collection<Particle> particles, BigDecimal simTime) {
    try {
      this.fwDynamic.write(simTime.toString() + "\n");
      for (Particle p : particles) {
        double x = p.getPositionX();
        double y = p.getPositionY();
        if(x > this.rightBound || x < this.leftBound || y > this.upperBound || y < this.lowerBound || !Double.isFinite(x) || !Double.isFinite(y)){
          throw new RuntimeException("OUT OF BOUNDS!");
        }
        this.fwDynamic.write(p.getPositionX() + " " + p.getPositionY() + " " + p.getVx() + " " + p.getVy() + "\n");
      }
    } catch (IOException e) {
      System.out.println("Error writing to file " + fnDynamic + ".txt");
    }
  }

  public void closeDynamic() {
    try {
      this.fwDynamic.close();
    } catch (IOException e) {
      System.out.println("Error closing file " + fnDynamic + ".txt");
    }
    System.out.println("Succesfully written dynamic properties to file: " + fnDynamic + ".txt");
  }

}
