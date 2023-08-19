package org.example.parsers;

import org.example.particles.Particle;
import org.example.particles.SimpleParticle2D;
import org.example.points.Point;
import org.example.utils.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class DynamicParser {
    public static void parse (String path){
        Scanner input = null;
        List<Particle<Point>> particles = new ArrayList<>();
        int currentParticleId = 0;
        InputParams inputParams = InputParams.getInstance();
        double interactionRadius = inputParams.getInteractionRadius();

        try {
            System.out.println("Parsing dynamic file...");

            File file = new File(path);
            input = new Scanner(file);

            input.nextLine(); //skips first timestamp

            while(input.hasNext()) {
                List<Double> positionAndVelocity = Arrays.stream(input.nextLine().split("\\s\\s\\s")).filter(s -> !s.isEmpty()).map(Double::valueOf).collect(Collectors.toList());

                // TODO: this takes for granted a constructor like the following one. Once created, uncomment line below
                // SimpleParticle2D(BigInteger id, Double positionX, Double positionY, Double velocityX, Double velocityY, Double radius)
                // particles.add(new SimpleParticle2D(currentParticleId++, positionAndVelocity.get(0), positionAndVelocity.get(1), positionAndVelocity.get(2), positionAndVelocity.get(3), interactionRadius));
                inputParams.setParticles(particles);
            }
        } catch(FileNotFoundException e) {
            System.out.println("Error parsing dynamic file " + path);
        } finally {
            if(input != null)
                input.close();

            System.out.println("Dynamic file parsed successfully");
        }
    }
}
