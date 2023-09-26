package org.example;

import picocli.CommandLine;
import picocli.CommandLine.*;


public class InputParser {

    private final double radius;
    private final double mass;
    private final double L;
    private final int N;
    private final int n;

    private InputParser(double radius, double mass, double L, int N, int n) {
        this.radius = radius;
        this.mass = mass;
        this.L = L;
        this.N = N;
        this.n = n;
    }

    @Command(name                    = "timeDrivenSimulation",
            description              = "Time-driven simulation of particles in a line.",
            version                  = "timeDrivenSimulation 1.0",
            mixinStandardHelpOptions = true)
    private static class ParseInputCommand implements Runnable {
        @Option(names       = {"-r", "--radius"},
                description = "Radii of particles.",
                required    = true)
        private double radius;

        @Option(names       = {"-m", "--mass"},
                description = "Masses of particles.",
                required    = true)
        private double mass;

        @Option(names       = {"-L", "--length"},
                description = "Length of the line where particles move.",
                required    = true)
        private double L;

        @Option(names       = {"-N", "--particle-amount"},
                description = "Amount of particles to be created on top of the line.",
                required    = true)
        private int N;

        @Option(names       = {"-n", "--step-scale"},
                description = "Scale of the integration step (10^-n).",
                required    = true)
        private int n;

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

    public static InputParser parse(String[] args) {
        InputParser inputParser = null;

        ParseInputCommand parseInputCommand = new ParseInputCommand();
        CommandLine cmdCommand              = new CommandLine(parseInputCommand);
        try {
            int exitCode = cmdCommand.execute(args);
            if (exitCode != 0)
                return null;

            inputParser = new InputParser(
                                parseInputCommand.radius,
                                parseInputCommand.mass,
                                parseInputCommand.L,
                                parseInputCommand.N,
                                parseInputCommand.n
                        );

        } catch (ParameterException e) {
            return null;
        }
        return inputParser;
    }
}


