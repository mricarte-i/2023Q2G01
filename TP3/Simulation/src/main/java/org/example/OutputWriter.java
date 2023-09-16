package org.example;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;

public class OutputWriter {
  // NOTE: maybe two files for each pressure? or all in one?
  private FileWriter fwWallEvents;
  private String fnWallEvents;
  private double border;

  public OutputWriter() {
    ParamsParser paramsParser = ParamsParser.getInstance();
    border = paramsParser.getW();

    fnWallEvents = paramsParser.getOutputPath() + "-wallEvents-sep-" + border;
    try {
      File file = new File(fnWallEvents + ".txt");
      if (file.exists()) {
        file.delete();
      }
      file.createNewFile();
      this.fwWallEvents = new FileWriter(fnWallEvents + ".txt", true);
    } catch (IOException e) {
      System.out.println("Error creating file " + fnWallEvents + ".txt");
    }
  }

  public void writeWallCollisionEvent(BigDecimal simTime, Event event) {
    Particle p = event.getParticleA();
    double transferredVelocity;
    switch (event.getCollisionType()) {
      case X:
        transferredVelocity = p.getVx();
        break;
      case Y:
        transferredVelocity = p.getVy();
        break;
      default:
        return; // ignore this collision event
    }
    double impulse = p.getMass() * Math.abs(transferredVelocity);
    int containerId = p.getPositionX() < border ? 1 : 2;

    try {
      this.fwWallEvents.write(simTime.toString() + "\n");
      // don't assume the perimeters are without the gap!
      //double pressureA = impulseA / ((2 * wA + hA + (hA - L)) * dT);
      //double pressureB = impulseB / ((2 * wB + hB + (hB - L)) * dT);
      this.fwWallEvents.write(impulse + " " + containerId + "\n");
    } catch (IOException e) {
      System.out.println("Error writing to file " + fnWallEvents + ".txt");
    }
  }

  public void close() {
    try {
      this.fwWallEvents.close();
    } catch (IOException e) {
      throw new RuntimeException("Error closing the file " + fnWallEvents + ".txt");
    }
    System.out.println("Successfully written to " + fnWallEvents + ".txt");
  }

}
