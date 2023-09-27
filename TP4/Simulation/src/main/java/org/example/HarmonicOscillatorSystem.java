package org.example;

import org.example.integrators.Gear5Integrator;
import org.example.integrators.Integrator;

public class HarmonicOscillatorSystem {

    // Parametrized conditions
    private static final double[] STEP_SCALES = {
            1.0d,
            2.0d,
            3.0d,
            4.0d,
            5.0d
    }; // TIME_STEP = 10^(-STEP_SCALE)
    private static final double AMPLITUDE = 1.0d;

    // Fixed conditions
    private static final double MASS = 70.0d;
    private static final double K = 10^4;
    private static final double GAMMA = 100.0d;
    private static final double TF = 5.0d;

    // Initial conditions (fixed)
    private static final double R0 = 1;
    private static final double V0 = - AMPLITUDE * GAMMA / (2 * MASS);
    private static final double ELASTIC_COEFFICIENT = - K / MASS;
    private static final double VISCOSITY_COEFFICIENT = - GAMMA / MASS;

    // Output files names
    private static final String STATIC_FILE = "Static.txt";
    private static final String DYNAMIC_FILE = "Dynamic.txt";

    private static HarmonicOscillatorSystem harmonicOscillatorSystem = null;

    private HarmonicOscillatorSystem() {}

    public static HarmonicOscillatorSystem getInstance() {
        if (harmonicOscillatorSystem == null)
            harmonicOscillatorSystem = new HarmonicOscillatorSystem();
        return harmonicOscillatorSystem;
    }

    private double[] getGear5InitialDerivatives() {
        double[] derivatives = new double[Gear5Integrator.DERIVATIVE_COUNT];
        derivatives[0] = R0;
        derivatives[1] = V0;

        int positionDerivative = 0;
        int velocityDerivative = 1;
        for (int i = 2; i < Gear5Integrator.DERIVATIVE_COUNT; i++) {
            derivatives[i] = ELASTIC_COEFFICIENT * derivatives[positionDerivative] + VISCOSITY_COEFFICIENT * derivatives[velocityDerivative];
            positionDerivative += 1;
            velocityDerivative += 1;
        }
        return derivatives;
    }

    private Integrator setUpGear5(double deltaT) {
        double[] initialDerivatives = getGear5InitialDerivatives();
        return new Gear5Integrator(
                initialDerivatives[0],
                initialDerivatives[1],
                initialDerivatives[2],
                initialDerivatives[3],
                initialDerivatives[4],
                initialDerivatives[5],
                deltaT,
                MASS,
                true
        );
    }

    public void simulate() {
        Integrator gear5;
        double currStep;
        for (double stepScale : STEP_SCALES) {
            currStep = Math.pow(10, -stepScale);

            gear5 = setUpGear5(currStep);
        }

    }

    public static void main(String[] args) {
        HarmonicOscillatorSystem hos = HarmonicOscillatorSystem.getInstance();
        hos.simulate();
    }
}