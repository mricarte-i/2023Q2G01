package org.example.parsers;

import org.example.utils.Pair;

import java.io.File;
import java.io.FileNotFoundException;
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
}
