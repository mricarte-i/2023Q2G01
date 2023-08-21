package org.example;

import org.example.distance_methods.DistanceMethod;
import org.example.distance_methods.EuclideanDistanceMethod2D;
import org.example.neighbour_finding_methods.CellIndexMethod2DWithWrapAround;
import org.example.neighbour_finding_methods.NeighbourFindingMethod;
import org.example.parsers.InputParams;
import org.example.particles.OffLaticeParticle2D;
import org.example.points.Point2D;
import org.example.suppliers.RandomOffLaticeParticle2DSupplier;

import java.util.*;
import java.util.function.Supplier;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        // Press Alt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        parseInputParams(args);

        // Press Shift+F10 or click the green arrow button in the gutter to run the code.
        for (int i = 1; i <= 5; i++) {

            // Press Shift+F9 to start debugging your code. We have set one breakpoint
            // for you, but you can always add more by pressing Ctrl+F8.
            System.out.println("i = " + i);
        }

        Random globalRandomGenerator = new Random(42L);
        sampleParticleCIMRun(2d, globalRandomGenerator);
    }

    private static void sampleParticleCIMRun(double interactivityRadius, Random globalRandomGenerator){
        int N       = 2000;
        double L    = 50d;
        double maxX = L;
        double maxY = L;
        double v    = 0.03d;
        double particleRadius = 0d;
        Supplier<Collection<OffLaticeParticle2D>>    offLaticeParticleSupplier = new RandomOffLaticeParticle2DSupplier(N, maxX, maxY, v, particleRadius, globalRandomGenerator);
        DistanceMethod<Point2D>                              euclideanDistance = new EuclideanDistanceMethod2D<>();
        NeighbourFindingMethod<Point2D, OffLaticeParticle2D> cellIndexMethod2D = new CellIndexMethod2DWithWrapAround<>(euclideanDistance, L);
        Map<OffLaticeParticle2D, Collection<OffLaticeParticle2D>> neighbourMap = cellIndexMethod2D.calculateNeighbours(offLaticeParticleSupplier.get(), interactivityRadius);

        for (OffLaticeParticle2D particle : neighbourMap.keySet()) {
            System.out.println(particle);
            System.out.println(neighbourMap.get(particle));
            System.out.println();
        }
    }

    private static void parseInputParams(String[] args) {
        if(args.length != 1)
            throw new RuntimeException("Invalid args count");

        String[] firstArg = args[0].split("--(\\w+)=\\s*");
        if(firstArg.length != 2 || !Objects.equals(firstArg[0], "noiseAmplitude")){
            throw new RuntimeException("Invalid noiseAmplitude arg");
        }

        InputParams inputParams = InputParams.getInstance();
        inputParams.setNoiseAmplitude(Double.parseDouble(firstArg[1]));
    }
}