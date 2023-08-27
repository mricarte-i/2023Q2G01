package org.example.parsers;

import java.math.BigInteger;
import java.util.*;
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

    String[] readParam = args[0].split("=");
    if(readParam.length != 2 || !readParam[0].startsWith("--")) {
      throw new RuntimeException("Invalid parameter format: " + args[0]);
    }
    String runMode = readParam[1];
    String[] params = Arrays.copyOfRange(args, 1, args.length);

    if (runMode.equals("parse")) {
      parseWithFiles(params);
    } else if (runMode.equals("random")) {
      parseWithRandom(params);
    } else {
      throw new RuntimeException("runTime argument value must be 'parse' or 'random'");
    }

  }

  private static void parseWithRandom(String[] params) {
    // N L R(=0 por ser puntuales) Rc eta v_i steps seed OUT POLOUT
    InputParams ip = InputParams.getInstance();
    // example: [jar] --N=420 --L=50 --Rc=1.0 --eta=0.3 --V=0.3
    // --T=500 --seed=10940 --ST="../Static420" --OUT="../Out420" --POLOUT="../Pol420"

    Map<String, String> paramMap = new HashMap<>();

    for(String param: params) {
      String[] parts = param.split("=");
      if(parts.length != 2 || !parts[0].startsWith("--")) {
        throw new RuntimeException("Invalid parameter format: " + param);
      }
      paramMap.put(parts[0].substring(2), parts[1]);
    }

    if(paramMap.size() < 9 || paramMap.size() > 10){
      throw new RuntimeException(
              "need parameters: N, L, Rc, eta, V, T, ST, OUT, POLOUT ('seed' is an optional parameter) " + params.length + " " + Arrays.toString(params));
    }

    ip.setParticleNumber(Integer.parseInt(paramMap.get("N")));
    ip.setSideLength(Double.parseDouble(paramMap.get("L")));
    ip.setInteractionRadius(Double.parseDouble(paramMap.get("Rc")));
    ip.setNoiseAmplitude(Double.parseDouble(paramMap.get("eta")));
    ip.setInitialParticleVelocity(Double.parseDouble(paramMap.get("V")));
    ip.setSteps(Integer.parseInt(paramMap.get("T")));
    ip.setStaticPath(paramMap.get("ST"));
    ip.setOutputPath(paramMap.get("OUT"));
    ip.setPolarizationOutPath(paramMap.get("POLOUT"));
    if(paramMap.containsKey("seed")){
      ip.setSeed(Long.parseLong(paramMap.get("seed")));
    }

    Random r;
    if(ip.getSeed() != -1){
      r = new Random(ip.getSeed());
    }else{
      r = new Random();
    }
    Supplier<Collection<OffLaticeParticle2D>> offLaticeParticlesSupplier = new RandomOffLaticeParticle2DSupplier(
        ip.getParticleNumber(), ip.getSideLength(), ip.getSideLength(), ip.getInitialParticleVelocity(), ip.getRadius(),
        r);

    ip.setParticles(offLaticeParticlesSupplier.get());

    StaticParser.writeFile();

  }

  private static void parseWithFiles(String[] params) {
    InputParams ip = InputParams.getInstance();
    // example: [jar] --T=500 --Rc=1.0 --eta=0.3
    // --ST="../Static100.txt" --DY="../Dynamic100.txt" --OUT="../Out100"
    // --POLOUT="../Pol100"

    Map<String, String> paramMap = new HashMap<>();

    for(String param: params) {
      String[] parts = param.split("=");
      if(parts.length != 2 || !parts[0].startsWith("--")) {
        throw new RuntimeException("Invalid parameter format: " + param);
      }
      paramMap.put(parts[0].substring(2), parts[1]);
    }

    if(paramMap.size() < 7){
      throw new RuntimeException(
              "need parameters: T, Rc, eta, ST, DY, OUT, POLOUT " + params.length + " " + Arrays.toString(params));
    }

    ip.setSteps(Integer.parseInt(paramMap.get("T")));
    ip.setInteractionRadius(Double.parseDouble(paramMap.get("Rc")));
    ip.setNoiseAmplitude(Double.parseDouble(paramMap.get("eta")));
    ip.setStaticPath(paramMap.get("ST"));
    ip.setDynamicPath(paramMap.get("DY"));
    ip.setOutputPath(paramMap.get("OUT"));
    ip.setPolarizationOutPath(paramMap.get("POLOUT"));

    // read that static file!
    StaticParser.parse(ip.getStaticPath()); // modifies the ip singleton!
    // read that dynamic file!
    DynamicParser.read(ip.getDynamicPath()); // adds particles!

  }

}
