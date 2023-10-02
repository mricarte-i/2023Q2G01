package org.example;

import org.example.integrators.Integrator;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class ParamsParser {

    private final static double RADIUS = 2.25d; // in cm
    private final static double MASS = 25d; // in g
    private final static double LENGTH = 135d; // in cm

    private final static double MIN_U = 9;
    private final static double MAX_U = 12;
    private final static double U_RANGE = MAX_U - MIN_U;

    private final Simulation simulation;

    private final List<Particle> particles;

    private final double L;
    private final int N;
    private final int n;
    private final int k;
    private final boolean ordered;

    private final String staticFile;
    private final String dynamicFile;

    private final Long seed;

    private ParamsParser(Simulation simulation) {
        this.simulation = simulation;
        this.particles = null;
        this.L = LENGTH;
        this.N = 0;
        this.n = 0;
        this.k = 0;
        this.ordered = false;
        this.staticFile = null;
        this.dynamicFile = null;
        this.seed = null;
    }

    private ParamsParser(Simulation simulation, List<Particle> particles, int N, int n, int k, String staticFile,
            String dynamicFile, boolean ordered, Long seed) {
        this.simulation = simulation;
        this.particles = particles;
        this.L = LENGTH;
        this.N = N;
        this.n = n;
        this.k = k;
        this.ordered = ordered;
        this.staticFile = staticFile;
        this.dynamicFile = dynamicFile;
        this.seed = seed;
    }

    private ParamsParser(Simulation simulation, List<Particle> particles, int N, int n, int k, String staticFile,
            String dynamicFile, boolean ordered, Long seed, double L) {
        this.simulation = simulation;
        this.particles = particles;
        this.L = L;
        this.N = N;
        this.n = n;
        this.k = k;
        this.ordered = ordered;
        this.staticFile = staticFile;
        this.dynamicFile = dynamicFile;
        this.seed = seed;
    }

    @Command(name = "timeDrivenSimulation", description = "Time-driven simulation of particles in a line.", mixinStandardHelpOptions = true, subcommands = {
            ParseInputCommand.ParseRandomInputCommand.class,
            ParseInputCommand.ParseFileInputCommand.class,
            ParseInputCommand.HarmonicOscillatorParseCommand.class
    })
    private static class ParseInputCommand {

        @Command(name = "hos", description = "Time-driven simulation of a Harmonic Oscillator.", version = "hos 1.0")
        private static class HarmonicOscillatorParseCommand implements Callable<ParamsParser> {

            @Override
            public ParamsParser call() throws Exception {
                HarmonicOscillatorSystem hos = HarmonicOscillatorSystem.getInstance();
                return new ParamsParser(
                        hos::simulate);
            }
        }

        @Command(version = "timeDrivenSimulation 1.0")
        private static class TimeDrivenParseMixin {

            @Option(names = { "-s",
                    "--static-file" }, description = "Static file path with number of particles (N), radii and masses per particle.", paramLabel = "[STATIC_FILE]", scope = ScopeType.INHERIT, required = true)
            private String staticFile;

            @Option(names = { "-d",
                    "--dynamic-file" }, description = "Dynamic file path with positions and velocities per particle.", paramLabel = "[DYNAMIC_FILE]", scope = ScopeType.INHERIT, required = true)
            private String dynamicFile;

            @Option(names = { "-n",
                    "--step-scale" }, description = "Scale of the integration step (10^-n).", paramLabel = "[STEP_SCALE]", scope = ScopeType.INHERIT, required = true)
            private int n;

            @Option(names = { "-k",
                    "--state-step-scale" }, description = "Save state every k steps (k*10^-n).", paramLabel = "[STATE_STEP_SCALE]", scope = ScopeType.INHERIT, required = true)
            private int k;

            @Option(names = "--seed", description = "Seed to use for pseudo-random generation.", paramLabel = "[SEED]", scope = ScopeType.INHERIT, required = false)
            private Long seed;

            @Option(names = "--ordered", description = "Whether particles should be ordered by u.", scope = ScopeType.INHERIT, negatable = true, defaultValue = "false", fallbackValue = "true", required = true)
            private boolean ordered;

            public static Queue<Double> getUParameters(Random rand, int N, boolean ordered) {
                Queue<Double> uParameters;
                if (ordered) {
                    uParameters = new PriorityQueue<>(N);
                } else {
                    uParameters = new ArrayDeque<>(N);
                }

                double u;
                for (int id = 0; id < N; id++) {
                    u = rand.nextDouble() * U_RANGE + MIN_U;
                    uParameters.offer(u);
                }
                return uParameters;
            }
        }

        @Command(name = "random", description = "Time-driven simulation of particles in a line. Use randomly generated particles.")
        private static class ParseRandomInputCommand implements Callable<ParamsParser> {
            @Mixin
            private TimeDrivenParseMixin timeDrivenParseMixin;

            @Option(names = { "-N",
                    "--particle-amount" }, description = "Amount of particles to be created on top of the line.", paramLabel = "[N]", required = true)
            private int N;

            @Override
            public ParamsParser call() throws Exception {
                List<Particle> particles = generateParticles();
                ParticleSystem ps = ParticleSystem.getInstance();

                return new ParamsParser(
                        ps::simulate,
                        particles,
                        this.N,
                        timeDrivenParseMixin.n,
                        timeDrivenParseMixin.k,
                        timeDrivenParseMixin.staticFile,
                        timeDrivenParseMixin.dynamicFile,
                        timeDrivenParseMixin.ordered,
                        timeDrivenParseMixin.seed);
            }

            private boolean checkIfPositionIsOccupied(double x, List<Particle> particles) {
                double particleX;
                double particleLeftBound;
                double particleRightBound;

                boolean particleNotWrapped;
                boolean positionBetweenBounds;

                for (Particle particle : particles) {
                    particleX = particle.getPosition();

                    particleLeftBound = particleX - RADIUS;
                    particleRightBound = particleX + RADIUS;

                    particleNotWrapped = particleLeftBound >= 0 && particleRightBound <= LENGTH;
                    if (particleNotWrapped) {
                        positionBetweenBounds = x >= particleLeftBound && x <= particleRightBound;
                        if (positionBetweenBounds)
                            return true;
                    } else {
                        if (particleLeftBound < 0)
                            particleLeftBound += LENGTH;

                        if (particleRightBound > LENGTH)
                            particleRightBound += LENGTH;

                        positionBetweenBounds = x < particleRightBound || x > particleLeftBound;
                        if (positionBetweenBounds)
                            return true;
                    }
                }
                return false;
            }

            private List<Particle> generateParticles() {
                List<Particle> particles = new ArrayList<>(N);
                Random rand = timeDrivenParseMixin.seed == null ? new Random() : new Random(timeDrivenParseMixin.seed);
                boolean overlaps;
                double x;

                Queue<Double> uParameters = TimeDrivenParseMixin.getUParameters(rand, N, timeDrivenParseMixin.ordered);

                Particle firstParticle = null;
                Particle prevParticle;
                Particle currParticle = null;

                double u;
                double deltaT = Math.pow(10, -timeDrivenParseMixin.n);
                for (int id = 0; id < N; id++) {
                    prevParticle = currParticle;

                    do {
                        x = rand.nextDouble() * LENGTH;
                        overlaps = checkIfPositionIsOccupied(x, particles);
                    } while (overlaps);

                    u = uParameters.poll();

                    currParticle = new Particle(RADIUS, MASS, u, false, false, prevParticle, null, x, deltaT, u, LENGTH);
                    particles.add(currParticle);

                    if (firstParticle == null)
                        firstParticle = currParticle;

                    if (prevParticle != null)
                        prevParticle.setRightNeighbour(currParticle);
                }

                if (firstParticle != null && firstParticle != currParticle) {
                    firstParticle.setLeftNeighbour(currParticle);
                    currParticle.setRightNeighbour(firstParticle);
                }

                return particles;
            }
        }

        @Command(name = "parse", description = "Time-driven simulation of particles in a line. Read particles from static and dynamic files.")
        private static class ParseFileInputCommand implements Callable<ParamsParser> {

            @Mixin
            private TimeDrivenParseMixin timeDrivenParseMixin;

            @Override
            public ParamsParser call() throws Exception {
                List<Particle> particles;

                int particleNumber = parseParticleNumber();
                double lineLength = parseL();

                if (particleNumber > 0) {
                    particles = parseParticles(particleNumber);
                    if (particles.size() != particleNumber) {
                        System.out.println("Error in input file \"" + timeDrivenParseMixin.staticFile
                                + "\". Wrong number of particles: expected " + particleNumber + ", parsed "
                                + particles.size());
                        return null;
                    }
                } else {
                    System.out.println("Error in input file. No particles found");
                    return null;
                }
                ParticleSystem ps = ParticleSystem.getInstance();

                return new ParamsParser(
                        ps::simulate,
                        particles,
                        particleNumber,
                        timeDrivenParseMixin.n,
                        timeDrivenParseMixin.k,
                        timeDrivenParseMixin.staticFile,
                        timeDrivenParseMixin.dynamicFile,
                        timeDrivenParseMixin.ordered,
                        timeDrivenParseMixin.seed,
                        lineLength);
            }

            private int parseParticleNumber() {
                Scanner input = null;
                int particleNumber = 0;
                try {
                    File file = new File(timeDrivenParseMixin.staticFile);
                    input = new Scanner(file);
                    particleNumber = Arrays.stream(
                            input.nextLine().split("\\s"))
                            .filter(s -> !s.isEmpty())
                            .map(Double::valueOf)
                            .collect(Collectors.toList()).get(0).intValue();
                    System.out.println("Particle number in input file parsed successfully!");
                } catch (Exception e) {
                    System.out.println(
                            "Error parsing particle number in input file \"" + timeDrivenParseMixin.staticFile + "\"");
                } finally {
                    if (input != null) {
                        input.close();
                    }
                }
                return particleNumber;
            }

            private double parseL() {
                Scanner input = null;
                double L = 0;
                try {
                    File file = new File(timeDrivenParseMixin.staticFile);
                    input = new Scanner(file);

                    // Ignore particle number
                    input.nextLine();

                    L = Arrays.stream(
                            input.nextLine().split("\\s"))
                            .filter(s -> !s.isEmpty())
                            .map(Double::valueOf)
                            .collect(Collectors.toList()).get(0);

                    System.out.println("Line length in input file parsed successfully!");
                } catch (Exception e) {
                    System.out.println(
                            "Error parsing line length in input file \"" + timeDrivenParseMixin.staticFile + "\"");
                } finally {
                    if (input != null) {
                        input.close();
                    }
                }
                return L;
            }

            private List<Particle> parseParticles(int N) {
                Scanner staticInput = null;
                Scanner dynamicInput = null;
                List<Particle> particles = new ArrayList<>(N);
                try {
                    File staticFile = new File(timeDrivenParseMixin.staticFile);
                    File dynamicFile = new File(timeDrivenParseMixin.dynamicFile);

                    staticInput = new Scanner(staticFile);
                    dynamicInput = new Scanner(dynamicFile);

                    // Ignore particle number
                    staticInput.nextLine();
                    // Ignore L
                    staticInput.nextLine();

                    // Ignore t=0 time-stamp
                    dynamicInput.nextLine();

                    Queue<Particle> particlesOrderedByPosition = new PriorityQueue<>(N,
                            Comparator.comparingDouble(Particle::getPosition));

                    int id = 0;

                    double mass, radius;
                    double x, y, vx, vy;
                    double u;
                    double deltaT = Math.pow(10, -timeDrivenParseMixin.n);

                    while (staticInput.hasNext() && dynamicInput.hasNext()) {
                        String nextStaticLine = staticInput.nextLine();
                        String nextDynamicLine = dynamicInput.nextLine();

                        List<Double> staticProperties = Arrays.stream(nextStaticLine.split("[\\s\\t]+"))
                                .filter(s -> !s.isEmpty())
                                .map(Double::valueOf).collect(Collectors.toList());

                        List<Double> dynamicProperties = Arrays.stream(nextDynamicLine.split("[\\s\\t]+"))
                                .filter(s -> !s.isEmpty())
                                .map(Double::valueOf).collect(Collectors.toList());

                        mass = staticProperties.get(0);
                        radius = staticProperties.get(1);

                        x = dynamicProperties.get(0);
                        vx = dynamicProperties.get(2);

                        u = vx;

                        Particle p = new Particle(radius, mass, u, false, false, null, null, x, deltaT, vx, LENGTH);
                        particlesOrderedByPosition
                                .add(p);

                        id++;
                    }

                    Particle firstParticle = null, currParticle = null, prevParticle;
                    while (!particlesOrderedByPosition.isEmpty()) {
                        prevParticle = currParticle;
                        currParticle = particlesOrderedByPosition.poll();

                        if (firstParticle == null)
                            firstParticle = currParticle;

                        if (prevParticle != null)
                            prevParticle.setRightNeighbour(currParticle);

                        currParticle.setLeftNeighbour(prevParticle);
                        particles.add(currParticle);
                    }
                    if (firstParticle != null && firstParticle != currParticle) {
                        currParticle.setRightNeighbour(firstParticle);
                        firstParticle.setLeftNeighbour(currParticle);
                    }

                    System.out.println("Particles in input file parsed successfully!");
                } catch (Exception e) {
                    System.out.println("Error parsing particles in input files \"" + timeDrivenParseMixin.staticFile
                            + "\" and \"" + timeDrivenParseMixin.dynamicFile + "\"");
                } finally {
                    if (staticInput != null) {
                        staticInput.close();
                    }
                    if (dynamicInput != null) {
                        dynamicInput.close();
                    }
                }
                return particles;
            }
        }

    }

    public Simulation getSimulation() {
        return simulation;
    }

    public List<Particle> getParticles() {
        return particles;
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

    public int getStateDeltaTimeScale() {
        return k;
    }

    public String getStaticFile() {
        return staticFile;
    }

    public String getDynamicFile() {
        return dynamicFile;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public Long getSeed() {
        return seed;
    }

    private static ParamsParser paramsParser = null;

    public static ParamsParser getInstance() {
        return paramsParser;
    }

    public static ParamsParser parse(String[] args) {
        ParamsParser paramsParser = null;

        ParseInputCommand parseInputCommand = new ParseInputCommand();
        CommandLine cmdCommand = new CommandLine(parseInputCommand);
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

        ParamsParser.paramsParser = paramsParser;

        return paramsParser;
    }
}
