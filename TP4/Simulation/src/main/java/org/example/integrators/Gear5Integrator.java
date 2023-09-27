package org.example.integrators;

public class Gear5Integrator implements Integrator {

    private static final int POSITION_DERIVATIVE = 0;
    private static final int VELOCITY_DERIVATIVE = 1;
    private static final int ACCELERATION_DERIVATIVE = 2;
    private static final int DERIVATIVE_COUNT = 6;
    private static final int LAST_DERIVATIVE = 5;
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
    private final double[] predictedDerivatives;

    private final boolean forceIsVDependent;

    private final double deltaT;
    private int stepCount;

    public Gear5Integrator(
            double r0,
            double r1,
            double r2,
            double r3,
            double r4,
            double r5,
            double deltaT,
            boolean forceIsVelocityDependent
    ){
        this.correctedDerivatives = new double[] { r0, r1, r2, r3, r4, r5 };
        this.predictedDerivatives = new double[DERIVATIVE_COUNT];
        this.deltaT = deltaT;
        this.stepCount = 0;
        this.forceIsVDependent = forceIsVelocityDependent;
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

    private void predict() {
        for (int i = LAST_DERIVATIVE; i >= 0; i--) {
            for (int j = LAST_DERIVATIVE; j >= 0; j--){
                predictedDerivatives[i] = correctedDerivatives[j] * correctedCoefficients[j];
            }
        }
    }

    private double evaluateForce(double nextStepAcceleration) {
        double predictedA = predictedDerivatives[ACCELERATION_DERIVATIVE];
        double deltaA = nextStepAcceleration - predictedA;

        return deltaA * correctedCoefficients[ACCELERATION_DERIVATIVE];
    }

    private void correct(double deltaR2) {
        for (int i = 0; i < DERIVATIVE_COUNT; i++) {
            correctedDerivatives[i] = predictedDerivatives[i] + getPredictorCoefficient(i) * deltaR2 * inverseCorrectedCoefficients[i];
        }
    }

    @Override
    public double getPosition() {
        return correctedDerivatives[POSITION_DERIVATIVE];
    }

    @Override
    public double getVelocity() {
        return correctedDerivatives[VELOCITY_DERIVATIVE];
    }

    @Override
    public void advanceStep(double nextStepAcceleration) {
        predict();

        double deltaR2 = evaluateForce(nextStepAcceleration);
        correct(deltaR2);

        this.stepCount += 1;
    }

    @Override
    public int getStep() {
        return stepCount;
    }
}
