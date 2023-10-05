package org.example;

import org.example.integrators.AnalyticSolutionIntegrator;
import org.example.integrators.BeemanIntegrator;
import org.example.integrators.Gear5Integrator;
import org.example.integrators.Integrator;

import java.util.Arrays;
import org.example.integrators.VerletIntegrator;

public class HarmonicOscillatorSystem {

    // Parametrized conditions
    private static final double[] STEP_SCALES = {
            1.0d,
            2.0d,
            3.0d,
            4.0d,
            5.0d,
            6.0d
    }; // TIME_STEP = 10^(-STEP_SCALE)
    private static final double AMPLITUDE = 1.0;

    // Fixed conditions
    private static final double MASS = 70.0;
    private static final double K = 10000;
    private static final double GAMMA = 100.0;
    private static final double TF = 5.0;

    // Initial conditions (fixed)
    private static final double R0 = AMPLITUDE;
    private static final double V0 = -AMPLITUDE * GAMMA / (2 * MASS);

    // Output files names
    private static final String STATIC_FILE = "Static.txt";
    private static final String DYNAMIC_FILE = "Dynamic.txt";

    private static HarmonicOscillatorSystem harmonicOscillatorSystem = null;

    private HarmonicOscillatorSystem() {
    }

    public static HarmonicOscillatorSystem getInstance() {
        if (harmonicOscillatorSystem == null)
            harmonicOscillatorSystem = new HarmonicOscillatorSystem();
        return harmonicOscillatorSystem;
    }

    private static double getForce(double pos, double vel) {
        return (-K * pos - GAMMA * vel);
    }

    private double[] getGear5InitialDerivatives() {
        double[] derivatives = new double[Gear5Integrator.DERIVATIVE_COUNT];
        Arrays.fill(derivatives, 0);
        derivatives[0] = R0;
        derivatives[1] = V0;

        int positionDerivative = 0;
        int velocityDerivative = 1;
        for (int i = 2; i < Gear5Integrator.DERIVATIVE_COUNT; i++) {
            derivatives[i] = getForce(derivatives[positionDerivative], derivatives[velocityDerivative]) / MASS;
            positionDerivative += 1;
            velocityDerivative += 1;
        }
        return derivatives;
    }

    private Gear5Integrator setUpGear5(double deltaT) {
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
                true);
    }

    private BeemanIntegrator setUpBeeman(double deltaT) {
        return new BeemanIntegrator(deltaT, R0, V0, MASS, HarmonicOscillatorSystem::getForce);
    }

    private VerletIntegrator setUpVerlet(double deltaT) {
        return new VerletIntegrator(deltaT, R0, V0, MASS, HarmonicOscillatorSystem::getForce);
    }

    private AnalyticSolutionIntegrator setUpAnalytic(double deltaT) {
        return new AnalyticSolutionIntegrator(deltaT, AMPLITUDE, MASS, K, GAMMA);
    }

    public void simulate() {
        Integrator gear5, beeman, verlet, analytic;
        double currStep;

        for (double stepScale : STEP_SCALES) {
            double writeStep = Math.pow(10, -1.0);
            currStep = Math.pow(10, -stepScale);
            Writer writerG = new Writer("../gear5-" + stepScale + ".txt");
            gear5 = setUpGear5(currStep);

            Writer writerB = new Writer("../beeman-" + stepScale + ".txt");
            beeman = setUpBeeman(currStep);

            Writer writerV = new Writer("../verlet-" + stepScale + ".txt");
            verlet = setUpVerlet(currStep);

            Writer writerA = new Writer("../analytic-" + stepScale + ".txt");
            analytic = setUpAnalytic(currStep);

            double time = 0;
            int stepCounter = 0;
            writerG.writeState(time, gear5);
            writerB.writeState(time, beeman);
            writerV.writeState(time, verlet);
            writerA.writeState(time, analytic);

            while (time < TF) {
                gear5.advanceStep(HarmonicOscillatorSystem::getForce);
                beeman.advanceStep(HarmonicOscillatorSystem::getForce);
                verlet.advanceStep(HarmonicOscillatorSystem::getForce);
                analytic.advanceStep(HarmonicOscillatorSystem::getForce);
                time += currStep;

                stepCounter++;
                if (stepCounter >= writeStep / currStep) {
                    writerG.writeState(time, gear5);
                    writerB.writeState(time, beeman);
                    writerV.writeState(time, verlet);
                    writerA.writeState(time, analytic);
                    stepCounter = 0; // reset
                }
            }
            writerG.close();
            writerB.close();
            writerV.close();
            writerA.close();
        }

    }

    public static void main(String[] args) {
        HarmonicOscillatorSystem hos = HarmonicOscillatorSystem.getInstance();
        hos.simulate();
    }
}