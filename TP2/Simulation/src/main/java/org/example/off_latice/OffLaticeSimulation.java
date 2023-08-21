package org.example.off_latice;

import org.example.distance_methods.DistanceMethod;
import org.example.distance_methods.EuclideanDistanceMethod2D;
import org.example.neighbour_finding_methods.CellIndexMethod2DWithWrapAround;
import org.example.neighbour_finding_methods.NeighbourFindingMethod;
import org.example.parsers.InputParams;
import org.example.parsers.OutputWriter;
import org.example.particles.OffLaticeParticle2D;
import org.example.points.Point2D;

import java.util.Collection;
import java.util.Map;

public class OffLaticeSimulation {

    public static void main(String[] args) {
        // TODO: returns an InputParams instance, after having picked either Random or
        // Parse runMode
        InputParams ip = TUI.getParams();
        // TODO: calculate M based on side length, number of particles, interaction
        // radius
        int M = 10;

        // if all is good, start simulation
        Collection<OffLaticeParticle2D> particles = ip.getParticles();
        double va = calculatePolarization(particles);
        // TODO: add many paths to InputParams
        OutputWriter oWriter = new OutputWriter();
        oWriter.writeFile(va, 0, ip.getPolarizationPath());

        // TODO: impl DynamicWriter
        DynamicWriter dWriter = new DynamicWriter(ip.getDynamicOutputPath());
        dWriter.log(0, particles);

        DistanceMethod<Point2D> distMethod = new EuclideanDistanceMethod2D<>();
        NeighbourFindingMethod<Point2D, OffLaticeParticle2D> cim = new CellIndexMethod2DWithWrapAround<>(distMethod,
                ip.getSideLength(), M);
        Evolver<Point2D, OffLaticeParticle2D> evolver = new OffLaticeEvolver<>(ip.getNoiseAmplitude());

        long startCalc = System.nanoTime();
        // TODO: add iterations/steps to InputParams
        for (int step = 1; step <= ip.getSteps(); step++) {
            Map<OffLaticeParticle2D, Collection<OffLaticeParticle2D>> neighboursData = cim
                    .calculateNeighbours(particles, ip.getInteractionRadius());
            particles = evolver.update(particles, neighboursData);
            dWriter.log(step, particles);

            va = calculatePolarization(particles);
            oWriter.writeFile(va, step, ip.getPolarizationPath());

        }

        // pWriter.close();
        dWriter.close();
        // print a done message & paths to written files

        // print time
        long endCalc = System.nanoTime();
        System.out.println("Time: " + String.valueOf(endCalc - startCalc) + " nanoseconds");
        // print final va
        System.out.println("Final polarization value: " + String.valueOf(va));
        // print density & noise amplitude to be nice
        System.out.println(
                "Density: " + String.valueOf(va) + " & Noise Amplitude: " + String.valueOf(ip.getNoiseAmplitude()));

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
