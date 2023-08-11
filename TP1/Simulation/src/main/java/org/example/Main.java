package org.example;

import org.example.distance_methods.DistanceMethod;
import org.example.distance_methods.EuclideanDistanceMethod2D;
import org.example.neighbour_finding_methods.CellIndexMethod2D;
import org.example.neighbour_finding_methods.NeighbourFindingMethod;
import org.example.parsers.DynamicParser;
import org.example.parsers.ResultParser;
import org.example.parsers.StaticParser;
import org.example.particles.Particle2D;
import org.example.particles.SimpleParticle2D;
import org.example.points.Point2D;
import org.example.suppliers.IncreasingBigIntegersSupplier;
import org.example.utils.Pair;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) {
        System.out.printf("Hello and welcome!\n");
        //example: [jar] --M=50 --alg=naive|cim --mode=wrap|normal --ST="../Static100.txt" --DY="../Dynamic100.txt" --OUT="../Out100"

        if(args.length != 6){
            throw new RuntimeException("need parameters: M, alg, mode, ST, DY, OUT " + args.length);
        }
        int M = 0;
        String staticFile = null, dynamicFile = null, outputLocation = null;

        // placeholder param parser...
        for (int i = 0; i < args.length; i++) {
            String[] line = args[i].split("(?:--(\\w+)=\\s*)");
            if (line.length < 2) {
                throw new RuntimeException("all parameters must have key and value." + Arrays.toString(line));
            }
            switch(i){
                case 0:
                    M = Integer.parseInt(line[1]);
                    break;
                case 1:
                    String readAlg = line[1];
                    System.out.println("sure buddy " + readAlg);
                    break;
                case 2:
                    String readMode = line[1];
                    System.out.println("sure " + readMode);
                    break;
                case 3:
                    staticFile = line[1];
                    break;
                case 4:
                    dynamicFile = line[1];
                    break;
                case 5:
                    outputLocation = line[1];
                    break;
            }
            //System.out.println(args[i]);
        }

        List<Pair<Double>> stRes =  StaticParser.parse(staticFile);
        Pair<Double> nlPair = stRes.get(0);
        int N = nlPair.getFirst().intValue();
        double L = (double)nlPair.getSecond();

        System.out.println("N: " + N + " L: " + L + " M: " + M);

        List<Pair<Double>> dyRes =  DynamicParser.parse(dynamicFile);

        List<Particle2D> particles = new ArrayList<>();
        Supplier<BigInteger> idSupp = new IncreasingBigIntegersSupplier();
        for(int i = 0; i < dyRes.size(); i++){
            Pair<Double> rprPair = stRes.get(i + 1);
            Pair<Double> xyPair = dyRes.get(i);

            particles.add(new SimpleParticle2D(idSupp.get(), xyPair.getFirst(), xyPair.getSecond(), rprPair.getFirst()));
        }

        //call that algorithm!
        DistanceMethod<Point2D> distMethod = new EuclideanDistanceMethod2D<>();
        NeighbourFindingMethod<Point2D, Particle2D> cellIndex = new CellIndexMethod2D(distMethod, L, M);
        ResultParser rp = new ResultParser();
        rp.parseAndWriteToFile(cellIndex.calculateNeighbours(particles, 1.0d), outputLocation);

    }
}