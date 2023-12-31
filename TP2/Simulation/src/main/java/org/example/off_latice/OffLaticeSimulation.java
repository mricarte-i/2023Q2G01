package org.example.off_latice;

import org.example.distance_methods.DistanceMethod;
import org.example.distance_methods.EuclideanDistanceMethod2D;
import org.example.neighbour_finding_methods.CellIndexMethod2DWithWrapAround;
import org.example.neighbour_finding_methods.NeighbourFindingMethod;
import org.example.parsers.*;
import org.example.particles.OffLaticeParticle2D;
import org.example.points.Point2D;

import java.util.Collection;
import java.util.Map;

public class OffLaticeSimulation {

    public static void main(String[] args) {
        ParamsParser.parse(args);
        InputParams ip = InputParams.getInstance();

        // if all is good, start simulation
        Collection<OffLaticeParticle2D> particles = ip.getParticles();
        double va = calculatePolarization(particles);

        OutputWriter oWriter = new OutputWriter(ip.getPolarizationOutPath());
        oWriter.writeFile(va, 0);

        DynamicWriter dWriter = new DynamicWriter(ip.getOutputPath());
        dWriter.writeFile(particles, 0);

        DistanceMethod<Point2D> distMethod = new EuclideanDistanceMethod2D<>();
        // cim now has its own optimal M function! how cool :)
        NeighbourFindingMethod<Point2D, OffLaticeParticle2D> cim = new CellIndexMethod2DWithWrapAround<>(distMethod,
                ip.getSideLength());
        Evolver<Point2D, OffLaticeParticle2D> evolver = new OffLaticeEvolver<>(ip.getNoiseAmplitude(), ip.getSideLength());

        long startCalc = System.nanoTime();
        for (int step = 1; step < ip.getSteps(); step++) {
            Map<OffLaticeParticle2D, Collection<OffLaticeParticle2D>> neighboursData = cim
                    .calculateNeighbours(particles, ip.getInteractionRadius());
            particles = evolver.update(particles, neighboursData);
            dWriter.writeFile(particles, step);

            va = calculatePolarization(particles);
            oWriter.writeFile(va, step);
        }

        oWriter.close();
        dWriter.close();
        // print a done message & paths to written files
        System.out.println("Wrote to file " + ip.getOutputPath() + ".txt");
        System.out.println("Wrote to file " + ip.getPolarizationOutPath() + ".txt");

        // print time
        long endCalc = System.nanoTime();
        System.out.println("Time: " + String.valueOf(endCalc - startCalc) + " nanoseconds");
        // print final va
        System.out.println("Final polarization value: " + String.valueOf(va));
        // print density & noise amplitude to be nice
        System.out.println(
                "Density: " + String.valueOf(particles.size()/Math.pow(ip.getSideLength(), 2)) + " & Noise Amplitude: " + String.valueOf(ip.getNoiseAmplitude()));

    }

    private static double calculatePolarization(Collection<OffLaticeParticle2D> particles) {
        double sumOfXAxisProyectionAngle = 0;
        double sumOfYAxisProyectionAngle = 0;

        for (OffLaticeParticle2D p : particles) {
            sumOfXAxisProyectionAngle += p.getVelocity().xAxisProjectionAngle();
            sumOfYAxisProyectionAngle += p.getVelocity().yAxisProjectionAngle();
        }

        double res = Math.sqrt(Math.pow(sumOfXAxisProyectionAngle, 2) + Math.pow(sumOfYAxisProyectionAngle, 2))
                / particles.size();

        return res;
    }
}
