package org.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ParamsParser {
  private static ParamsParser instance;

  private Collection<Particle> particles;
  private double L, w, h, vm, r, m;
  private int N;
  private String inputPath, dynamicPath, outputPath;

  private ParamsParser() {
    particles = new ArrayList<>();
    L = w = h = 0.09;
    vm = 0.01;
    r = 0.0015;
    m = 1;
    N = 200;
    inputPath = dynamicPath = outputPath = null;
  }

  public static ParamsParser getInstance() {
    if (instance == null) {
      synchronized (ParamsParser.class) {
        if (instance == null) {
          instance = new ParamsParser();
        }
      }
    }
    return instance;
  }

  public void parse(String[] args) {
    // --runmode=parse|random
    if (args.length <= 1) {
      throw new RuntimeException("need parameters!");
    }

    String[] readParam = args[0].split("=");
    if (readParam.length != 2 || !readParam[0].startsWith("--")) {
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

  private void parseWithFiles(String[] params) {
    // NOTA: w & h son opcionales, el enunciado los deja siempre como 0.09, solo
    // cambiaria L

    // --N=200 --L=0.09 --w=0.09 --h=0.09 --IN="../Input200"
    // --DYNOUT="../Dynamic200"
    // --OUT="../Out200"
    Map<String, String> paramMap = new HashMap<>();

    for (String param : params) {
      String[] parts = param.split("=");
      if (parts.length != 2 || !parts[0].startsWith("--")) {
        throw new RuntimeException("Invalid parameter format: " + param);
      }
      paramMap.put(parts[0].substring(2), parts[1]);
    }

    if (paramMap.size() < 5) {
      throw new RuntimeException(
          "*need* parameters: N, L, IN, DYNOUT, OUT; *optional*: w, h " + Arrays.toString(params));
    }

    this.N = Integer.parseInt(paramMap.get("N"));
    this.L = Double.parseDouble(paramMap.get("L"));
    this.inputPath = paramMap.get("IN");
    this.dynamicPath = paramMap.get("DYNOUT");
    this.outputPath = paramMap.get("OUT");

    this.w = paramMap.containsKey("w") ? Double.parseDouble(paramMap.get("w")) : 0.09;
    this.h = paramMap.containsKey("h") ? Double.parseDouble(paramMap.get("h")) : 0.09;

    // parse particles from file
    parseParticles();
  }

  private void parseParticles() {
    Scanner input = null;
    particles = new ArrayList<>();
    try {
      File file = new File(inputPath + ".txt");
      input = new Scanner(file);

      Double particleNumber = Arrays.stream(input.nextLine().split("\\s\\s\\s\\s")).filter(s -> !s.isEmpty())
          .map(Double::valueOf).collect(Collectors.toList()).get(0);
      N = particleNumber.intValue();

      int id = 0;

      while (input.hasNext()) {
        List<Double> properties = Arrays.stream(input.nextLine().split("\\s\\s\\s\\s")).filter(s -> !s.isEmpty())
            .map(Double::valueOf).collect(Collectors.toList());

        particles.add(new Particle(id, properties.get(0), properties.get(1), properties.get(2), properties.get(3),
            properties.get(4), properties.get(5)));

        id++;
      }
    } catch (FileNotFoundException e) {
      System.out.println("Error parsing input file " + inputPath + ".txt");
    } finally {
      if (input != null) {
        input.close();
      }

      System.out.println("Input file parsed sucessfully!");
    }
  }

  private void parseWithRandom(String[] params) {
    // NOTA: m, r, vm, w & h son opcionales, el enunciado los deja siempre como 1,
    // 0.0015, 0.01 y 0.09, solo cambiaria L

    // --N=200 --L=0.09 --vm=0.01 --r=0.0015 --m=1 --w=0.09 --h=0.09
    // --DYNOUT="../Dynamic200"
    // --OUT="../Out200"
    Map<String, String> paramMap = new HashMap<>();

    for (String param : params) {
      String[] parts = param.split("=");
      if (parts.length != 2 || !parts[0].startsWith("--")) {
        throw new RuntimeException("Invalid parameter format: " + param);
      }
      paramMap.put(parts[0].substring(2), parts[1]);
    }

    if (paramMap.size() < 4) {
      throw new RuntimeException(
          "*need* parameters: N, L, DYNOUT, OUT; *optionals*: w, h, vm, r, m " + Arrays.toString(params));
    }

    this.N = Integer.parseInt(paramMap.get("N"));
    this.L = Double.parseDouble(paramMap.get("L"));
    this.dynamicPath = paramMap.get("DYNOUT");
    this.outputPath = paramMap.get("OUT");

    this.w = paramMap.containsKey("w") ? Double.parseDouble(paramMap.get("w")) : 0.09;
    this.h = paramMap.containsKey("h") ? Double.parseDouble(paramMap.get("h")) : 0.09;
    this.vm = paramMap.containsKey("vm") ? Double.parseDouble(paramMap.get("vm")) : 0.01;
    this.r = paramMap.containsKey("r") ? Double.parseDouble(paramMap.get("r")) : 0.0015;
    this.m = paramMap.containsKey("m") ? Double.parseDouble(paramMap.get("m")) : 1;

    // random particle generation within first container's bounds
    generateParticles();
  }

  private void generateParticles() {
    particles = new ArrayList<>(N);
    Random rand = new Random();

    for (int id = 0; id < N; id++) {
      double x = rand.nextDouble() * w;
      double y = rand.nextDouble() * h;
      double angle = rand.nextDouble() * 2 * Math.PI;
      double vx = vm * Math.cos(angle);
      double vy = vm * Math.sin(angle);

      particles.add(new Particle(id, x, y, vx, vy, m, r));
    }
  }

  public Collection<Particle> getParticles() {
    return this.particles;
  }

  public void setParticles(Collection<Particle> particles) {
    this.particles = particles;
  }

  public double getL() {
    return this.L;
  }

  public void setL(double L) {
    this.L = L;
  }

  public double getW() {
    return this.w;
  }

  public void setW(double w) {
    this.w = w;
  }

  public double getH() {
    return this.h;
  }

  public void setH(double h) {
    this.h = h;
  }

  public int getN() {
    return this.N;
  }

  public void setN(int N) {
    this.N = N;
  }

  public String getInputPath() {
    return this.inputPath;
  }

  public void setInputPath(String inputPath) {
    this.inputPath = inputPath;
  }

  public String getDynamicPath() {
    return this.dynamicPath;
  }

  public void setDynamicPath(String dynamicPath) {
    this.dynamicPath = dynamicPath;
  }

  public String getOutputPath() {
    return this.outputPath;
  }

  public void setOutputPath(String outputPath) {
    this.outputPath = outputPath;
  }

}
