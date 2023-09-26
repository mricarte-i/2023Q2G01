package org.example;

import picocli.CommandLine;
import picocli.CommandLine.*;

import java.util.concurrent.Callable;


public class ParamsParser {

    private final double radius;
    private final double mass;
    private final double L;
    private final int N;
    private final int n;

    private final String staticFile;
    private final String dynamicFile;

    private final Long seed;

    private ParamsParser(int N, int n, String staticFile, String dynamicFile, Long seed) {
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
            mixinStandardHelpOptions = true,
            subcommands = {
                    ParseInputCommand.ParseRandomInputCommand.class,
                    ParseInputCommand.ParseFileInputCommand.class
            })
    private static class ParseInputCommand {

        @Option(names       = {"-s", "--static-file"},
                description = "Static file path with number of particles (N), radii and masses per particle.",
                paramLabel  = "STATIC_FILE",
                scope       = ScopeType.INHERIT,
                required    = true)
        private String staticFile;

        @Option(names       = {"-d", "--dynamic-file"},
                description = "Dynamic file path with positions and velocities per particle.",
                paramLabel  = "DYNAMIC_FILE",
                scope       = ScopeType.INHERIT,
                required    = true)
        private String dynamicFile;

        @Option(names       = {"-n", "--step-scale"},
                description = "Scale of the integration step (10^-n).",
                paramLabel  = "STEP_SCALE",
                scope       = ScopeType.INHERIT,
                required    = true)
        private int n;

        @Command(name       = "random",
                description = "Use randomly generated particles.")
        private static class ParseRandomInputCommand implements Callable<ParamsParser> {
            @Option(names       = {"-N", "--particle-amount"},
                    description = "Amount of particles to be created on top of the line.",
                    paramLabel  = "N",
                    required    = true)
            private int N;

            @Option(names       = "--seed",
                    description = "Seed to use for pseudo-random generation.",
                    paramLabel  = "SEED",
                    required    = false)
            private Long seed;

            @Override
            public ParamsParser call() throws Exception {
                return null;
            }
        }

        @Command(name       = "parse",
                description = "Read particles from static and dynamic files.")
        private static class ParseFileInputCommand implements Callable<ParamsParser> {

            @Override
            public ParamsParser call() throws Exception {
                return null;
            }
        }
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

    public static ParamsParser parse(String[] args) {
        ParamsParser paramsParser = null;

        ParseInputCommand parseInputCommand = new ParseInputCommand();
        CommandLine cmdCommand              = new CommandLine(parseInputCommand);
        try {
            int exitCode = cmdCommand.execute(args);
            if (exitCode != 0)
                return null;

            paramsParser = cmdCommand.getExecutionResult();
        } catch (ParameterException e) {
            return null;
        }
        return paramsParser;
    }
}


