package org.example;

public class Main {

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

    // Output files names
    private static final String STATIC_FILE = "Static.txt";
    private static final String DYNAMIC_FILE = "Dynamic.txt";

    public static void main(String[] args) {

    }
}