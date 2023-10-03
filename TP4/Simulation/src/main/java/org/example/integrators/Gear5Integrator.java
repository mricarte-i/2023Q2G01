package org.example.integrators;

public class Gear5Integrator implements Integrator {

    private static final int POSITION_DERIVATIVE = 0;
    private static final int VELOCITY_DERIVATIVE = 1;
    private static final int ACCELERATION_DERIVATIVE = 2;
    public static final int DERIVATIVE_COUNT = 6;
    public static final int LAST_DERIVATIVE = 5;
    private static final double[] FACTORIALS = {
            1.0d,
            1.0d,
            2.0d,
            6.0d,
            24.0d,
            120.0d
    };

    private static final double[] VELOCITY_INDEPENDENT_COEFFICIENTS = {
            3.0d / 20.0d,
            251.0d / 360.0d,
            1.0d,
            11.0d / 18.0d,
            1.0d / 6.0d,
            1.0d / 60.0d
    };

    private static final double[] VELOCITY_DEPENDENT_COEFFICIENTS = {
            3.0d / 16.0d,
            251.0d / 360.0d,
            1.0d,
            11.0d / 18.0d,
            1.0d / 6.0d,
            1.0d / 60.0d
    };

    private final double[] correctedCoefficients;
    private final double[] inverseCorrectedCoefficients;

    private final double[] correctedDerivatives;
    private Double deltaR2;
    private final double[] predictedDerivatives;

    private Gear5Stage stage;

    private enum Gear5Stage {
        PREDICTED,
        CORRECTED
    }

    private final boolean forceIsVDependent;

    private final double mass;
    private final double deltaT;

    private Double previousForce;
    private double previousAcceleration;

    private final Double boundary;

    public Gear5Integrator(
            double r0,
            double r1,
            double r2,
            double r3,
            double r4,
            double r5,
            double deltaT,
            double mass,
            boolean forceIsVelocityDependent
    ){
        this(r0, r1, r2, r3, r4, r5, deltaT, mass, forceIsVelocityDependent, null);
    }

    public Gear5Integrator(
            double r0,
            double r1,
            double r2,
            double r3,
            double r4,
            double r5,
            double deltaT,
            double mass,
            boolean forceIsVelocityDependent,
            Double boundary
    ){
        this.correctedDerivatives = new double[] { r0, r1, r2, r3, r4, r5 };
        this.deltaR2 = null;
        this.predictedDerivatives = new double[DERIVATIVE_COUNT];
        this.stage = Gear5Stage.CORRECTED;
        this.mass = mass;
        this.deltaT = deltaT;
        this.previousForce = null;
        this.forceIsVDependent = forceIsVelocityDependent;
        this.boundary = boundary;
        this.correctedCoefficients = new double[DERIVATIVE_COUNT];
        this.inverseCorrectedCoefficients = new double[DERIVATIVE_COUNT];
        initCorrectedCoefficients();
    }

    private void initCorrectedCoefficients(){
        double currCoefficient;
        double currInverseCoefficient;
        double deltaTPow;
        for (int i = 0; i < DERIVATIVE_COUNT; i++) {

            if (i == 0) {
                currCoefficient        = 1;
                currInverseCoefficient = 1;
            } else if (i == 1) {
                currCoefficient        = deltaT;
                currInverseCoefficient = 1.0d / deltaT;
            } else {
                deltaTPow = Math.pow(deltaT, i);

                currCoefficient        = deltaTPow / FACTORIALS[i];
                currInverseCoefficient = FACTORIALS[i] / deltaTPow;
            }

            correctedCoefficients[i]        = currCoefficient;
            inverseCorrectedCoefficients[i] = currInverseCoefficient;
        }
    }

    private double getPredictorCoefficient(int order){
        return forceIsVDependent ? VELOCITY_DEPENDENT_COEFFICIENTS[order] : VELOCITY_INDEPENDENT_COEFFICIENTS[order];
    }

    private double getNextStepRealAcceleration(ForceCalculator forceCalculator) {
        double nextStepForce = forceCalculator.calculateForce(predictedDerivatives[POSITION_DERIVATIVE], predictedDerivatives[VELOCITY_DERIVATIVE]);
        if (previousForce != null && previousForce == nextStepForce)
            return previousAcceleration;

        previousForce        = nextStepForce;
        previousAcceleration = nextStepForce / mass;

        return previousAcceleration;
    }

    private void applyBoundary(double[] derivativesArray) {
        if (boundary == null) return;
        derivativesArray[POSITION_DERIVATIVE] = derivativesArray[POSITION_DERIVATIVE] % boundary;
        if (derivativesArray[POSITION_DERIVATIVE] < 0) {
            derivativesArray[POSITION_DERIVATIVE] += boundary;
        }
    }

    public void predict() {
        if (this.stage == Gear5Stage.PREDICTED)
            throw new RuntimeException("Attempted prediction on already predicted stage. Call correct() before calling predict() again.");

        int currCoef;
        for (int i = LAST_DERIVATIVE; i >= 0; i--) {
            currCoef = 0;
            predictedDerivatives[i] = 0;
            for (int j = i; j <= LAST_DERIVATIVE; j++){
                predictedDerivatives[i] += correctedDerivatives[j] * correctedCoefficients[currCoef];
                currCoef += 1;
            }
        }

        applyBoundary(predictedDerivatives);

        this.stage = Gear5Stage.PREDICTED;
    }

    @Override
    public void evaluateForce(ForceCalculator forceCalculator) {
        if (this.stage == Gear5Stage.CORRECTED)
            predict();

        double predictedA = predictedDerivatives[ACCELERATION_DERIVATIVE];
        double realA  = getNextStepRealAcceleration(forceCalculator);
        double deltaA = realA - predictedA;

        this.deltaR2 = deltaA * correctedCoefficients[ACCELERATION_DERIVATIVE];
    }

    public void correct() {
        if (this.stage == Gear5Stage.CORRECTED)
            throw new RuntimeException("Attempted correction on already corrected stage. Call predict() before calling correct() again.");

        for (int i = 0; i < DERIVATIVE_COUNT; i++) {
            correctedDerivatives[i] = predictedDerivatives[i] + getPredictorCoefficient(i) * deltaR2 * inverseCorrectedCoefficients[i];
        }

        applyBoundary(correctedDerivatives);

        this.stage = Gear5Stage.CORRECTED;
    }

    @Override
    public double getPosition() {
        switch (this.stage){
            case PREDICTED:
                return predictedDerivatives[POSITION_DERIVATIVE];
            case CORRECTED:
                return correctedDerivatives[POSITION_DERIVATIVE];
        }
        return Double.NaN;
    }

    @Override
    public double getVelocity() {
        switch (this.stage) {
            case PREDICTED:
                return predictedDerivatives[VELOCITY_DERIVATIVE];
            case CORRECTED:
                return correctedDerivatives[VELOCITY_DERIVATIVE];
        }
        return Double.NaN;
    }

    @Override
    public void advanceStep() {
        if (this.deltaR2 == null)
            throw new RuntimeException("Cannot advance step without evaluating force. Call advanceStep with a ForceCalculator or call evaluateForce first.");
        correct();
        this.deltaR2 = null;
    }

    @Override
    public void advanceStep(ForceCalculator forceCalculator) {
        if (this.stage == Gear5Stage.CORRECTED)
            predict();
        evaluateForce(forceCalculator);
        correct();
        this.deltaR2 = null;
    }
}
