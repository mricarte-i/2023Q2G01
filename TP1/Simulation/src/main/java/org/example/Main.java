package org.example;

import org.example.distance_methods.DistanceMethod;
import org.example.distance_methods.EuclideanDistanceMethod2D;
import org.example.neighbour_finding_methods.CellIndexMethod2D;
import org.example.neighbour_finding_methods.NaiveNeighbourFindingMethod2D;
import org.example.neighbour_finding_methods.NaiveNeighbourFindingMethod2DWithWrapAround;
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
import java.util.Collection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) {
        System.out.printf("Hello and welcome!\n");

        //example: [jar] --runmode=parse|random {...specific params}


        if(args.length <= 1){
            throw new RuntimeException("need parameters!");
        }

        String[] readParam = args[0].split("(?:--(\\w+)=\\s*)");
        if(readParam.length != 2){
            throw new RuntimeException("TELL ME A RUN MODE");
        }
        String runMode = readParam[1];
        String[] params = Arrays.copyOfRange(args, 1, args.length);
        //String[] params = new String[args.length -1];
        //System.arraycopy(params, 1, args, args.length -1);

        //List<Particle2D> particles = null;
        if(runMode.equals("parse")){
            //particles = runParse(params);
            runParse(params);
        }else if(runMode.equals("random")){
            //particles = runRandom(params);
            runRandom(params);
        }



    }

    private static void runRandom(String[] params) {
        return;
    }

    private static void runParse(String[] params) {
        //example: [jar] --M=50 --Rc=1.0 --alg=naive|cim --mode=wrap|normal --ST="../Static100.txt" --DY="../Dynamic100.txt" --OUT="../Out100"


        //TODO: M isn't optional???
        if(params.length != 7){
            throw new RuntimeException("need parameters: M, Rc, alg, mode, ST, DY, OUT " + params.length + " " + Arrays.toString(params));
        }
        int M = 0;
        double Rc = 0d;
        String staticFile = null, dynamicFile = null, outputLocation = null, readAlg = null, readMode = null;

        // placeholder param parser...
        for (int i = 0; i < params.length; i++) {
            String[] line = params[i].split("(?:--(\\w+)=\\s*)");
            if (line.length < 2) {
                throw new RuntimeException("all parameters must have key and value." + Arrays.toString(line));
            }
            switch(i){
                case 0:
                    M = Integer.parseInt(line[1]);
                    break;
                case 1:
                    Rc = Double.parseDouble(line[1]);
                    break;
                case 2:
                    readAlg = line[1];
                    System.out.println("Algorithm: " + readAlg);
                    break;
                case 3:
                    readMode = line[1];
                    System.out.println("(only for naive) mode: " + readMode);
                    break;
                case 4:
                    staticFile = line[1];
                    break;
                case 5:
                    dynamicFile = line[1];
                    break;
                case 6:
                    outputLocation = line[1];
                    break;
            }
            //System.out.println(params[i]);
        }

        //get those constants!
        List<Pair<Double>> stRes =  StaticParser.parse(staticFile);
        Pair<Double> nlPair = stRes.get(0);
        int N = nlPair.getFirst().intValue();
        double L = (double)nlPair.getSecond();

        System.out.println("N: " + N + " L: " + L + " M: " + M + " Rc: " + Rc);

        List<Pair<Double>> dyRes =  DynamicParser.parse(dynamicFile);

        //build those particles!
        List<Particle2D> particles = new ArrayList<>();
        Supplier<BigInteger> idSupp = new IncreasingBigIntegersSupplier();
        for(int i = 0; i < dyRes.size(); i++){
            Pair<Double> rprPair = stRes.get(i + 1);
            Pair<Double> xyPair = dyRes.get(i);

            particles.add(new SimpleParticle2D(idSupp.get(), xyPair.getFirst(), xyPair.getSecond(), rprPair.getFirst()));
        }

        DistanceMethod<Point2D> distMethod = new EuclideanDistanceMethod2D<>();
        ResultParser rp = new ResultParser();
        Map<Particle2D, Collection<Particle2D>> neighbourData = null;

        long startCalc = System.nanoTime();
        //call that algorithm!
        if(readAlg.equals("cim")){
            //TODO: catch InvalidNeighbourhoodRadiusException
            NeighbourFindingMethod<Point2D, Particle2D> cellIndex = new CellIndexMethod2D<>(distMethod, L, M);
            neighbourData = cellIndex.calculateNeighbours(particles, Rc);
        } else if (readAlg.equals("naive")) {
            if(readMode.equals("wrap")){
                NeighbourFindingMethod<Point2D, Particle2D> naiveWrap = new NaiveNeighbourFindingMethod2DWithWrapAround(distMethod, L);
                neighbourData = naiveWrap.calculateNeighbours(particles, Rc);
            } else if (readMode.equals("normal")) {
                NeighbourFindingMethod<Point2D, Particle2D> naiveNormal = new NaiveNeighbourFindingMethod2D(distMethod);
                neighbourData = naiveNormal.calculateNeighbours(particles, Rc);
            }
        }
        long endCalc = System.nanoTime();
        System.out.println("Time: " + String.valueOf(endCalc - startCalc) + " nanoseconds");

        if(neighbourData != null){
            rp.parseAndWriteToFile(neighbourData, outputLocation);
        }else {
            throw new RuntimeException("Finder has failed to run, check your parameters.");
        }
    }
}