package org.example.parsers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import org.example.particles.OffLaticeParticle2D;
import org.example.particles.Particle2D;
import org.example.particles.SimpleParticle2D;
import org.example.suppliers.IncreasingBigIntegersSupplier;
import org.example.suppliers.RandomOffLaticeParticle2DSupplier;
import org.example.utils.Pair;

public class ParamsParser {

  public static void parse(String[] args) {
    // example: [jar] --runmode=parse|random {...specific params}

    if (args.length <= 1) {
      throw new RuntimeException("need parameters!");
    }

    String[] readParam = args[0].split("(?:--(\\w+)=\\s*)");
    if (readParam.length != 2) {
      throw new RuntimeException("TELL ME A RUN MODE");
    }
    String runMode = readParam[1];
    String[] params = Arrays.copyOfRange(args, 1, args.length);

    if (runMode.equals("parse")) {
      // particles = runParse(params);
      parseWithFiles(params);
    } else if (runMode.equals("random")) {
      // particles = runRandom(params);
      parseWithRandom(params);
    } else {
      throw new RuntimeException("runTime argument value must be 'parse' or 'random'");
    }

  }

  private static void parseWithRandom(String[] params) {
    // N L R(=0 por ser puntuales) Rc eta v_i steps seed OUT POLOUT
    InputParams ip = InputParams.getInstance();
    // example: [jar] --N=420 --L=50 --Rc=1.0 --eta=0.3 --V=0.3
    // --T=500 --seed=10940 --OUT="../Out420" --POLOUT="../Pol420"

    if (params.length != 9) {
      throw new RuntimeException("need parameters: N, L, Rc, eta, V, T, seed, OUT, POLOUT");
    }

    // placeholder param parser...
    for (int i = 0; i < params.length; i++) {
      String[] line = params[i].split("(?:--(\\w+)=\\s*)");
      if (line.length < 2) {
        throw new RuntimeException("all parameters must have key and value." + Arrays.toString(line));
      }
      switch (i) {
        case 0:
          ip.setParticleNumber(Integer.parseInt(line[1]));
          break;
        case 1:
          ip.setSideLength(Double.parseDouble(line[1]));
          break;
        case 2:
          ip.setInteractionRadius(Double.parseDouble(line[1]));
          break;
        case 3:
          ip.setNoiseAmplitude(Double.parseDouble(line[1]));
          break;
        case 4:
          ip.setInitialParticleVelocity(Double.parseDouble(line[1]));
          break;
        case 5:
          ip.setSteps(Integer.parseInt(line[1]));
          break;
        case 6:
          ip.setSeed(Long.parseLong(line[1]));
          break;
        case 7:
          ip.setOutputPath(line[1]);
          break;
        case 8:
          ip.setPolarizationOutPath(line[1]);
          break;
      }
      // System.out.println(params[i]);
    }

    Random r = new Random(ip.getSeed());
    Supplier<Collection<OffLaticeParticle2D>> offLaticeParticlesSupplier = new RandomOffLaticeParticle2DSupplier(
        ip.getParticleNumber(), ip.getSideLength(), ip.getSideLength(), ip.getInitialParticleVelocity(), ip.getRadius(),
        r);

    ip.setParticles(offLaticeParticlesSupplier.get());

  }

  private static void parseWithFiles(String[] params) {
    InputParams ip = InputParams.getInstance();
    // example: [jar] --T=500 --Rc=1.0 --eta=0.3
    // --ST="../Static100.txt" --DY="../Dynamic100.txt" --OUT="../Out100"
    // --POLOUT="../Pol100"

    // TODO: M isn't optional???
    if (params.length != 6) {
      throw new RuntimeException(
          "need parameters: T, Rc, eta, ST, DY, OUT, POLOUT " + params.length + " " + Arrays.toString(params));
    }

    // placeholder param parser...
    for (int i = 0; i < params.length; i++) {
      String[] line = params[i].split("(?:--(\\w+)=\\s*)");
      if (line.length < 2) {
        throw new RuntimeException("all parameters must have key and value." + Arrays.toString(line));
      }
      switch (i) {
        case 0:
          // M = Integer.parseInt(line[1]);
          ip.setSteps(Integer.parseInt(line[1]));
          break;
        case 1:
          // Rc = Double.parseDouble(line[1]);
          ip.setInteractionRadius(Double.parseDouble(line[1]));
          break;
        case 2:
          ip.setNoiseAmplitude(Double.parseDouble(line[1]));
          break;
        case 3:
          // staticFile = line[1];
          ip.setStaticPath(line[1]);
          break;
        case 4:
          // dynamicFile = line[1];
          ip.setDynamicPath(line[1]);
          break;
        case 5:
          // outputLocation = line[1];
          ip.setOutputPath(line[1]);
          break;
        case 6:
          ip.setPolarizationOutPath(line[1]);
          break;
      }
      // System.out.println(params[i]);
    }

    // read that static file!
    StaticParser.parse(ip.getStaticPath()); // modifies the ip singleton!
    // read that dynamic file!
    DynamicParser.read(ip.getDynamicPath()); // adds particles!

  }

}
