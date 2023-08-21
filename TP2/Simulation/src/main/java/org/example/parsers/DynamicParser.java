package org.example.parsers;

import org.example.particles.Particle;
import org.example.particles.SimpleParticle2D;
import org.example.points.Point;
import org.example.points.SimplePoint2D;
import org.example.utils.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class DynamicParser {
    public static List<Particle<Point>> read (String fileName){
        Scanner input = null;
        List<Particle<Point>> particles = new ArrayList<>();
        int currentParticleId = 0;
        InputParams inputParams = InputParams.getInstance();
        double interactionRadius = inputParams.getInteractionRadius();

        try {
            System.out.println("Parsing dynamic file...");

            File file = new File(fileName);
            input = new Scanner(file);

            input.nextLine(); // skip timestamp

            while(input.hasNext()) {
                String line = input.nextLine();
                if (Objects.equals(line, "---")) {

                    input.nextLine(); // skip timestamp
                    particles.clear();
                }
                List<Double> positionAndVelocity = Arrays.stream(input.nextLine().split("\\s\\s\\s")).filter(s -> !s.isEmpty()).map(Double::valueOf).collect(Collectors.toList());

                // TODO: this takes for granted a constructor like the following one. Once created, uncomment line below
                // SimpleParticle2D(BigInteger id, Double positionX, Double positionY, Double velocityX, Double velocityY, Double radius)
                // particles.add(new SimpleParticle2D(currentParticleId++, positionAndVelocity.get(0), positionAndVelocity.get(1), positionAndVelocity.get(2), positionAndVelocity.get(3), interactionRadius));

                return particles;
            }
        } catch(FileNotFoundException e) {
            System.out.println("Error parsing dynamic file " + fileName);
        } finally {
            if(input != null)
                input.close();

            System.out.println("Dynamic file parsed successfully");
        }
        return new ArrayList<>();
    }

    public void writeFile (int t, List<SimpleParticle2D> particles, String fileName) {
        System.out.println("Writing dynamic file...");

        try {
            File file = new File(fileName + ".txt");
            if(!file.exists())
                file.createNewFile();
        } catch(IOException e) {
            System.out.println("Error creating file " + fileName + ".txt");
        }


        try {
            FileWriter fileWriter = new FileWriter(fileName + ".txt", false);

            if(t != 0)
                fileWriter.write("---\n");

            fileWriter.write("t" + t + "\n");
            for(SimpleParticle2D particle : particles) {
                // TODO: need getXVelocity and getYVelocity. Once created, uncomment
                // fileWriter.write(particle.getX() + " " + particle.getY() + " " + particle.getXVelocity() + " " + particle.getYVelocity() + "\n");
            }

            fileWriter.close();

            System.out.println("Wrote to file " + fileName + ".txt");
        } catch (IOException e) {
            System.out.println("Error writing to file " + fileName + ".txt");
        }
    }
}
