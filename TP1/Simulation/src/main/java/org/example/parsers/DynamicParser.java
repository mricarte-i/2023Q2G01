package org.example.parsers;

import org.example.utils.Pair;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class DynamicParser {
    public static List<Pair<Double>> parse (String path){
        Scanner input = null;
        List<Pair<Double>> list = new ArrayList<>();

        try {
            System.out.println("Parsing dynamic file...");

            File file = new File(path);
            input = new Scanner(file);

            input.nextLine();

            while(input.hasNext()) {
                List<Double> numbers = Arrays.stream(input.nextLine().split("\\s\\s\\s")).filter(s -> !s.isEmpty()).map(Double::valueOf).collect(Collectors.toList());
                Pair<Double> pair = new Pair<>(numbers.get(0), numbers.get(1));
                list.add(pair);
            }
        } catch(FileNotFoundException e) {
            System.out.println("Error parsing dynamic file " + path);
        } finally {
            if(input != null)
                input.close();

            System.out.println("Dynamic file parsed successfully");
        }
        return list;
    }
}
