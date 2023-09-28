package org.example;

import picocli.CommandLine;
import picocli.CommandLine.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;


public class ParamsParser {

    private final Simulation simulation;

    private final double radius;
    private final double mass;
    private final double L;
    private final int N;
    private final int n;

    private final String staticFile;
    private final String dynamicFile;

    private final Long seed;

    private ParamsParser(Simulation simulation, int N, int n, String staticFile, String dynamicFile, Long seed) {
        this.simulation = simulation;
        this.radius = 21.49d; // in cm
        this.mass   = 25d;    // in g
        this.L      = 135d;   // in cm
        this.N      = N;
        this.n      = n;
        this.staticFile  = staticFile;
        this.dynamicFile = dynamicFile;
        this.seed        = seed;
    }

    @Command(
            name        = "timeDrivenSimulation",
            description = "Time-driven simulation of particles in a line.",
            mixinStandardHelpOptions = true,
            subcommands = {
                    ParseInputCommand.ParseRandomInputCommand.class,
                    ParseInputCommand.ParseFileInputCommand.class,
                    ParseInputCommand.HarmonicOscillatorParseCommand.class
            }
    )
    private static class ParseInputCommand {

        @Command(name                    = "hos",
                description              = "Time-driven simulation of a Harmonic Oscillator.",
                version                  = "hos 1.0"
        )
        private static class HarmonicOscillatorParseCommand implements Callable<ParamsParser> {

            @Override
            public ParamsParser call() throws Exception {
                HarmonicOscillatorSystem hos = HarmonicOscillatorSystem.getInstance();
                return new ParamsParser(
                        hos::simulate,
                        0,
                        0,
                        null,
                        null,
                        null
                );
            }
        }

        @Command(version = "timeDrivenSimulation 1.0")
        private static class TimeDrivenParseMixin {

            @Option(names = {"-s", "--static-file"},
                    description = "Static file path with number of particles (N), radii and masses per particle.",
                    paramLabel = "[STATIC_FILE]",
                    scope = ScopeType.INHERIT,
                    required = true)
            private String staticFile;

            @Option(names = {"-d", "--dynamic-file"},
                    description = "Dynamic file path with positions and velocities per particle.",
                    paramLabel = "[DYNAMIC_FILE]",
                    scope = ScopeType.INHERIT,
                    required = true)
            private String dynamicFile;

            @Option(names = {"-n", "--step-scale"},
                    description = "Scale of the integration step (10^-n).",
                    paramLabel = "[STEP_SCALE]",
                    scope = ScopeType.INHERIT,
                    required = true)
            private int n;

            @Option(names = "--seed",
                    description = "Seed to use for pseudo-random generation.",
                    paramLabel = "[SEED]",
                    scope = ScopeType.INHERIT,
                    required = false)
            private Long seed;
        }

        @Command(name       = "random",
                description = "Time-driven simulation of particles in a line. Use randomly generated particles.")
        private static class ParseRandomInputCommand implements Callable<ParamsParser> {
            @Mixin
            private TimeDrivenParseMixin timeDrivenParseMixin;

            @Option(names       = {"-N", "--particle-amount"},
                    description = "Amount of particles to be created on top of the line.",
                    paramLabel  = "[N]",
                    required    = true)
            private int N;

            @Override
            public ParamsParser call() throws Exception {
                return new ParamsParser(
                        null,
                        this.N,
                        timeDrivenParseMixin.n,
                        timeDrivenParseMixin.staticFile,
                        timeDrivenParseMixin.dynamicFile,
                        timeDrivenParseMixin.seed
                );
            }
        }

        @Command(name       = "parse",
                description = "Time-driven simulation of particles in a line. Read particles from static and dynamic files.")
        private static class ParseFileInputCommand implements Callable<ParamsParser> {

            @Mixin
            private TimeDrivenParseMixin timeDrivenParseMixin;

            @Override
            public ParamsParser call() throws Exception {
                Scanner input = null;
                try {
                    File file = new File(timeDrivenParseMixin.staticFile);
                    input = new Scanner(file);
                    int particleNumber = Arrays.stream(
                                    input.nextLine().split("\\s"))
                            .filter(s -> !s.isEmpty())
                            .map(Double::valueOf)
                            .collect(Collectors.toList()).get(0).intValue();
                    return new ParamsParser(
                            null,
                            particleNumber,
                            timeDrivenParseMixin.n,
                            timeDrivenParseMixin.staticFile,
                            timeDrivenParseMixin.dynamicFile,
                            timeDrivenParseMixin.seed
                    );
                } catch (FileNotFoundException e) {
                    System.out.println("Error parsing input file \"" + timeDrivenParseMixin.staticFile + "\"");
                } finally {
                    if (input != null) {
                        input.close();
                    }
                    System.out.println("Input file parsed sucessfully!");
                }
                return null;
            }
        }
    }

    public Simulation getSimulation() {
        return simulation;
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
            ParseResult parseResult = cmdCommand.getParseResult();
            if (parseResult.subcommand() != null) {
                CommandLine subCommand = parseResult.subcommand().commandSpec().commandLine();
                paramsParser = subCommand.getExecutionResult();
            }
        } catch (ParameterException e) {
            return null;
        }
        return paramsParser;
    }
}


