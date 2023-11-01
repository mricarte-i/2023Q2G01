package org.example;

import org.javatuples.Pair;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public class ParamsParser {
    private static final String SYSTEM_DESCRIPTION = "Simulate a granular system of particles with vibrating base.";

    private final static double LENGTH = 70; //in cm
    private final static double WIDTH = 20; //in cm
    private final static double DEFAULT_APERTURE = 3;

    private final static double PARTICLE_MASS = 1; //in g
    private final static double[] RADII_BOUNDS = { 0.85d, 1.15d };

    private final static double DEFAULT_ANGULAR_VEL = 5;
    private final static double AMPLITUDE = 0.15; // in cm
    private final static double K_N = 250; // in dina/cm
    private final static double GAMMA = 0.7;
    private final static double MU = 0.7;

    private final static int DEFAULT_PARTICLE_COUNT = 200;
    private final static int INTEGRATION_EXP = 3;

    private final Supplier<SimulationSystem> simulationSystem;
    private final List<Particle> particles;

    private final double L;
    private final double W;
    private final double D;
    private final double lowerOutOfBoundsPosition;
    private final double reinjectionLowerBound;
    private final double reinjectionUpperBound;

    private final double w;
    private final double A;
    private final double Kn;
    private final double Kt;
    private final double gamma;
    private final double mu;

    private final int N;
    private final int n;
    private final int k;

    private final String staticFile;
    private final String dynamicFile;

    private final Random random;

    public List<Particle> getParticles() {
        return particles;
    }
    public SimulationSystem getSimulationSystem() {
        return simulationSystem.get();
    }

    public double getL() {
        return L;
    }
    public double getW() {
        return W;
    }
    public double getD() {
        return D;
    }
    public double getLowerOutOfBoundsPosition() {
        return lowerOutOfBoundsPosition;
    }
    public double getReinjectionLowerBound() {
        return reinjectionLowerBound;
    }
    public double getReinjectionUpperBound() {
        return reinjectionUpperBound;
    }

    public double getAngularVelocity() {
        return w;
    }
    public double getA() {
        return A;
    }
    public double getKn() {
        return Kn;
    }
    public double getKt() {
        return Kt;
    }
    public double getGamma() {
        return gamma;
    }
    public double getMu() {
        return mu;
    }

    public int getN() {
        return N;
    }
    public int getIntegrationDeltaTimeExp() {
        return n;
    }
    public int getStateWritingDeltaTimeExp() {
        return k;
    }

    public String getStaticFile() {
        return staticFile;
    }
    public String getDynamicFile() {
        return dynamicFile;
    }
    public Random getRandom() {
        return random;
    }

    private ParamsParser(Supplier<SimulationSystem> simulationSystem, List<Particle> particles,
                         Double L, Double W, Double D,
                         Double reinjectionUpperBound, Double reinjectionLowerBound,
                         Double w, Double A, Double Kn, Double Kt, Double gamma, Double mu,
                         Integer N, Integer n, Integer k,
                         String staticFile, String dynamicFile, Random random) {
        this.simulationSystem = simulationSystem;
        this.particles  = particles;

        this.L = L == null ? LENGTH : L;
        this.W = W == null ? WIDTH  : W;
        this.D = D == null ? DEFAULT_APERTURE : D;

        this.w  = w == null ? DEFAULT_ANGULAR_VEL : w;
        this.A  = A == null ? AMPLITUDE : A;
        this.Kn = Kn == null ? K_N : Kn;
        this.gamma = gamma == null ? GAMMA : gamma;
        this.mu    = mu == null    ? MU    : mu;

        this.N = N == null ? DEFAULT_PARTICLE_COUNT : N;
        this.n = n == null ? INTEGRATION_EXP        : n;
        this.k = k == null ? this.n : k;

        this.Kt = Kt == null ? 2*this.Kn : Kt;
        this.lowerOutOfBoundsPosition = -this.L / 10;
        this.reinjectionUpperBound = reinjectionUpperBound == null ? this.L : reinjectionUpperBound;
        this.reinjectionLowerBound = reinjectionLowerBound == null ? (this.L / 7) * 4 : reinjectionLowerBound;

        if (this.reinjectionLowerBound < this.lowerOutOfBoundsPosition)
            throw new RuntimeException();
        if (this.reinjectionUpperBound > this.L)
            throw new RuntimeException();

        this.staticFile  = staticFile;
        this.dynamicFile = dynamicFile;
        this.random = random;
    }

    @Command(name = "granularSim", description = SYSTEM_DESCRIPTION,
            mixinStandardHelpOptions = true,
            subcommands = {
                ParseInputCommand.ParseRandomInputCommand.class,
                //ParseInputCommand.ParseFileInputCommand.class
    })
    private static class ParseInputCommand {

        @Command(version = "granularSim 1.0")
        private static class GranularSimParserMixin {

            @Option(names = { "-s", "--static-file" },
                    description = "Static file path with number of particles (N), radii and masses per particle.",
                    paramLabel = "[STATIC_FILE]",
                    scope = ScopeType.INHERIT,
                    required = true)
            private String staticFile;

            @Option(names = { "-d", "--dynamic-file" },
                    description = "Dynamic file path with positions and velocities per particle.",
                    paramLabel = "[DYNAMIC_FILE]",
                    scope = ScopeType.INHERIT,
                    required = true)
            private String dynamicFile;

            @Option(names = { "-n", "--integration-step-scale" },
                    description = "Scale of the integration step (10^-n).",
                    paramLabel = "[INTEGRATION_STEP_SCALE]",
                    scope = ScopeType.INHERIT,
                    required = true)
            private int n;

            @Option(names = { "-k", "--state-step-scale" },
                    description = "Save state every k steps (k*10^-n).",
                    paramLabel = "[STATE_STEP_SCALE]",
                    scope = ScopeType.INHERIT,
                    required = true)
            private int k;

            @Option(names = "--seed",
                    description = "Seed to use for pseudo-random generation.",
                    paramLabel = "[SEED]",
                    scope = ScopeType.INHERIT, required = false)
            private Long seed;
        }

        @Command(name = "random", description = SYSTEM_DESCRIPTION + " Use randomly generated particles.")
        private static class ParseRandomInputCommand implements Callable<ParamsParser> {
            @Mixin
            private GranularSimParserMixin granularSimParserMixin;

            @Option(names = { "-D", "--vibrating-base-aperture" },
                    description = "Aperture of the vibrating base.",
                    paramLabel = "[W]",
                    required = true)
            private double D;

            @Option(names = { "-N", "--particle-count" },
                    description = "Amount of particles to be generated.",
                    paramLabel = "[N]",
                    required = true)
            private int N;

            @Option(names = { "-w", "--vibrating-base-w" },
                    description = "Angular velocity of vibrating base.",
                    paramLabel = "[W]",
                    required = true)
            private double w;

            @Override
            public ParamsParser call() throws Exception {
                double L = LENGTH;
                double W = WIDTH;
                Double D = this.D;
                Double reinjectionUpperBound = null;
                Double reinjectionLowerBound = null;

                Double w = this.w;
                Double A = null;
                Double Kn = null;
                Double Kt = null;
                Double gamma = null;
                Double mu = null;

                int N = this.N;
                int n = this.granularSimParserMixin.n;
                Integer k = this.granularSimParserMixin.k;

                double particleMass = PARTICLE_MASS;
                double radiiLowerBound = RADII_BOUNDS[0];
                double radiiUpperBound = RADII_BOUNDS[1];

                String staticFile = this.granularSimParserMixin.staticFile;
                String dynamicFile = this.granularSimParserMixin.dynamicFile;

                Random rand = granularSimParserMixin.seed == null ? new Random() : new Random(granularSimParserMixin.seed);

                List<Particle> particles = generateParticles(N, 0, W, 0, L, radiiLowerBound, radiiUpperBound, particleMass, n, rand);

                return new ParamsParser(
                        ParticleSystem::new, particles,
                        L, W, D,
                        reinjectionUpperBound,
                        reinjectionLowerBound,
                        w, A, Kn, Kt, gamma, mu,
                        N, n, k,
                        staticFile, dynamicFile,
                        rand
                );
            }

            private boolean checkIfPositionIsOccupied(double x, double y, double radius, Collection<Pair<Double, Double>> particlePositions, Iterator<Double> radii) {
                double otherX;
                double otherY;
                double otherRadius;

                double distance;
                for (Pair<Double, Double> position : particlePositions){
                    otherX = position.getValue0();
                    otherY = position.getValue1();
                    otherRadius = radii.next();

                    distance = Math.sqrt(Math.pow(x - otherX, 2) + Math.pow(y - otherY, 2));

                    if (distance <= radius + otherRadius) {
                        return true;
                    }
                }
                return false;
            }

            private Queue<Double> uniformRandomRadiiForParticles(int N, Random rand, double lower, double upper) {
                double radius;
                Queue<Double> radii = new ArrayDeque<>(N);

                double range = upper - lower;

                for (int id = 0; id < N; id++) {
                    radius = rand.nextDouble() * range + lower;
                    radii.add(radius);
                }

                return radii;
            }

            private Queue<Pair<Double, Double>> uniformRandomPositionsForParticles(int N, Random rand,
                                                                                   double xLower, double xUpper,
                                                                                   double yLower, double yUpper, Collection<Double> radii) {
                double x, y;
                double radius;
                boolean overlaps;
                Queue<Pair<Double, Double>> positions = new ArrayDeque<>(N);

                double particleXRange, particleYRange;
                double particleXLower, particleYLower;
                double particleXUpper, particleYUpper;

                Iterator<Double> radiiIter = radii.iterator();

                for (int id = 0; id < N; id++) {
                    radius = radiiIter.next();
                    do {
                        particleXLower = xLower + radius;
                        particleXUpper = xUpper - radius;
                        particleXRange = particleXUpper - particleXLower;

                        particleYLower = yLower + radius;
                        particleYUpper = yUpper - radius;
                        particleYRange = particleYUpper - particleYLower;

                        x = rand.nextDouble() * particleXRange + particleXLower;
                        y = rand.nextDouble() * particleYRange + particleYLower;
                        overlaps = checkIfPositionIsOccupied(x, y, radius, positions, radii.iterator());
                    } while (overlaps);
                    positions.add(new Pair<>(x, y));
                }

                return positions;
            }

            private List<Particle> generateParticles(int N, double lowerX, double upperX, double lowerY, double upperY,
                                                     double lowerRadius, double upperRadius, double particleMass,
                                                     double integrationStepExp, Random rand) {
                List<Particle> particles = new ArrayList<>(N);
                if (N <= 0)
                    return particles;

                double x;
                double y;
                double radius;

                Queue<Double> particleRadii = uniformRandomRadiiForParticles(N, rand, lowerRadius, upperRadius);
                Queue<Pair<Double, Double>> particlePositions = uniformRandomPositionsForParticles(N, rand, lowerX, upperX, lowerY, upperY, particleRadii);

                Particle currParticle;

                double deltaT = Math.pow(10, -integrationStepExp);
                for (int id = 0; id < N; id++) {
                    Pair<Double, Double> position = particlePositions.poll();

                    x = position.getValue0();
                    y = position.getValue1();
                    radius = particleRadii.poll();

                    currParticle = new Particle(id, radius, particleMass, ParticleSystem.GRAVITY, x, y, deltaT, 0, 0, N);
                    particles.add(currParticle);
                }

                return particles;
            }
        }
        /*
        @Command(name = "parse", description = SYSTEM_DESCRIPTION + " Read particles from static and dynamic files.")
        private static class ParseFileInputCommand implements Callable<ParamsParser> {

            @Mixin
            private GranularSimParserMixin granularSimParserMixin;

            @Override
            public ParamsParser call() throws Exception {
                List<Particle> particles;

                int particleNumber = parseParticleNumber();
                double lineLength = parseL();

                if (particleNumber > 0) {
                    particles = parseParticles(particleNumber);
                    if (particles.size() != particleNumber) {
                        System.out.println("Error in input file \"" + granularSimParserMixin.staticFile
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
                        granularSimParserMixin.n,
                        granularSimParserMixin.k,
                        granularSimParserMixin.staticFile,
                        granularSimParserMixin.dynamicFile,
                        granularSimParserMixin.ordered,
                        granularSimParserMixin.seed,
                        lineLength);
            }

            private int parseParticleNumber() {
                Scanner input = null;
                int particleNumber = 0;
                try {
                    File file = new File(granularSimParserMixin.staticFile);
                    input = new Scanner(file);
                    particleNumber = Arrays.stream(
                            input.nextLine().split("\\s"))
                            .filter(s -> !s.isEmpty())
                            .map(Double::valueOf)
                            .collect(Collectors.toList()).get(0).intValue();
                    System.out.println("Particle number in input file parsed successfully!");
                } catch (Exception e) {
                    System.out.println(
                            "Error parsing particle number in input file \"" + granularSimParserMixin.staticFile + "\"");
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
                    File file = new File(granularSimParserMixin.staticFile);
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
                            "Error parsing line length in input file \"" + granularSimParserMixin.staticFile + "\"");
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
                    File staticFile = new File(granularSimParserMixin.staticFile);
                    File dynamicFile = new File(granularSimParserMixin.dynamicFile);

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
                    double deltaT = Math.pow(10, -granularSimParserMixin.n);

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

                        Particle p = new Particle(id, radius, mass, u, false, false, null, null, x, deltaT, vx, LENGTH);
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
                    System.out.println("Error parsing particles in input files \"" + granularSimParserMixin.staticFile
                            + "\" and \"" + granularSimParserMixin.dynamicFile + "\"");
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
        }*/

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
