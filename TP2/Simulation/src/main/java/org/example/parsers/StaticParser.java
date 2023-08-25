package org.example.parsers;

import org.example.particles.OffLaticeParticle2D;
import org.example.utils.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class StaticParser {
    public static void parse (String path){
        Scanner input = null;
        InputParams inputParams = InputParams.getInstance();

        try {
            System.out.println("Parsing static file...");

            File file = new File(path);
            input = new Scanner(file);

            Double particleNumber = Arrays.stream(input.nextLine().split("\\s\\s\\s\\s")).filter(s -> !s.isEmpty()).map(Double::valueOf).collect(Collectors.toList()).get(0);
            Double sideLength = Arrays.stream(input.nextLine().split("\\s\\s\\s\\s")).filter(s -> !s.isEmpty()).map(Double::valueOf).collect(Collectors.toList()).get(0);

            inputParams.setParticleNumber(particleNumber.intValue());
            inputParams.setSideLength(sideLength);

            while(input.hasNext()) {
                List<Double> numbers = Arrays.stream(input.nextLine().split("\\s\\s\\s\\s")).filter(s -> !s.isEmpty()).map(Double::valueOf).collect(Collectors.toList());

                // This assumes all particles have same interaction radius and initial velocity
                inputParams.setInteractionRadius(numbers.get(0));
                inputParams.setInitialParticleVelocity(numbers.get(1));
            }

        } catch(FileNotFoundException e) {
            System.out.println("Error parsing static file " + path);
        } finally {
            if(input != null)
                input.close();

            System.out.println("Static file parsed successfully");
        }
    }

    public static void writeFile() {
        InputParams ip = InputParams.getInstance();
        try {
            File file = new File(ip.getStaticPath() + ".txt");
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error creating file " + ip.getStaticPath() + ".txt");
        }

        try {
            FileWriter fileWriter = new FileWriter(ip.getStaticPath() + ".txt", false);

            fileWriter.write(ip.getParticleNumber() + "\n");
            fileWriter.write(ip.getSideLength() + "\n");
            for(OffLaticeParticle2D p : ip.getParticles()){
                fileWriter.write(ip.getInteractionRadius() + " " + ip.getInitialParticleVelocity() + "\n");
            }

            fileWriter.close();

            System.out.println("Wrote to file " + ip.getStaticPath() + ".txt");
        } catch (IOException e) {
            System.out.println("Error writing to file " + ip.getStaticPath() + ".txt");
        }

    }
}
