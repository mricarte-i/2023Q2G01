package org.example.parsers;

import jdk.internal.net.http.common.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class StaticParser {
    public static List<Pair<Double, Double>> parse (String path){
        Scanner input = null;
        List<Pair<Double, Double>> list = new ArrayList<>();

        try {
            System.out.println("Parsing static file...");

            File file = new File(path);
            input = new Scanner(file);

            Double N = Arrays.stream(input.nextLine().split("\\s\\s\\s\\s")).filter(s -> !s.isEmpty()).map(Double::valueOf).collect(Collectors.toList()).get(0);
            Double L = Arrays.stream(input.nextLine().split("\\s\\s\\s\\s")).filter(s -> !s.isEmpty()).map(Double::valueOf).collect(Collectors.toList()).get(0);
            Pair<Double, Double> p = new Pair<>(N,L);
            list.add(p);

            while(input.hasNext()) {
                List<Double> numbers = Arrays.stream(input.nextLine().split("\\s\\s\\s\\s")).filter(s -> !s.isEmpty()).map(Double::valueOf).collect(Collectors.toList());
                Pair<Double, Double> pair = new Pair<>(numbers.get(0), numbers.get(1));
                list.add(pair);
            }

        } catch(FileNotFoundException e) {
            System.out.println("Error parsing static file " + path);
        } finally {
            if(input != null)
                input.close();

            System.out.println("Static file parsed successfully");
        }
        return list;
    }
}
