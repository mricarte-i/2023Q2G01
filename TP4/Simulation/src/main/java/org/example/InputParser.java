package org.example;

import picocli.CommandLine;
import picocli.CommandLine.*;


public class InputParser {

    private final double radius;
    private final double mass;
    private final double L;
    private final int N;
    private final int n;

    private final String staticFile;
    private final String dynamicFile;

    private final Long seed;

    private InputParser(int N, int n, String staticFile, String dynamicFile, Long seed) {
        this.radius = 21.49d; // in cm
        this.mass   = 25d;    // in g
        this.L      = 135d;   // in cm
        this.N      = N;
        this.n      = n;
        this.staticFile  = staticFile;
        this.dynamicFile = dynamicFile;
        this.seed        = seed;
    }

    @Command(name                    = "timeDrivenSimulation",
            description              = "Time-driven simulation of particles in a line.",
            version                  = "timeDrivenSimulation 1.0",
            mixinStandardHelpOptions = true)
    private static class ParseInputCommand implements Runnable {

        @Option(names       = {"-s", "--static-file"},
                description = "Static file path with number of particles (N), radii and masses per particle.",
                required    = true)
        private String staticFile;

        @Option(names       = {"-d", "--dynamic-file"},
                description = "Dynamic file path with positions and velocities per particle.",
                required    = true)
        private String dynamicFile;

        @Option(names       = {"-N", "--particle-amount"},
                description = "Amount of particles to be created on top of the line.",
                required    = true)
        private int N;

        @Option(names       = {"-n", "--step-scale"},
                description = "Scale of the integration step (10^-n).",
                required    = true)
        private int n;

        @Option(names       = "--seed",
                description = "Seed to use for pseudo-random generation.",
                required    = false)
        private Long seed;

        public void run() {}
    }

    public double getRadius() {
        return radius;
    }

    public double getMass() {
        return mass;
    }

    public double getL() {
        return L;
    }

    public int getN() {
        return N;
    }

    public int getDeltaTimeScale() {
        return n;
    }

    public String getStaticFile() {
        return staticFile;
    }

    public String getDynamicFile() {
        return dynamicFile;
    }

    public Long getSeed() {
        return seed;
    }

    public static InputParser parse(String[] args) {
        InputParser inputParser = null;

        ParseInputCommand parseInputCommand = new ParseInputCommand();
        CommandLine cmdCommand              = new CommandLine(parseInputCommand);
        try {
            int exitCode = cmdCommand.execute(args);
            if (exitCode != 0)
                return null;

            inputParser = new InputParser(
                                parseInputCommand.N,
                                parseInputCommand.n,
                                parseInputCommand.staticFile,
                                parseInputCommand.dynamicFile,
                                parseInputCommand.seed
                        );

        } catch (ParameterException e) {
            return null;
        }
        return inputParser;
    }
}


