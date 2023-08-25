package org.example.parsers;

import org.example.particles.OffLaticeParticle2D;
import org.example.particles.SimpleOffLaticeParticle2D;
import org.example.suppliers.IncreasingBigIntegersSupplier;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DynamicParser {
    public static void read(String fileName) {
        Scanner input = null;
        List<OffLaticeParticle2D> particles = new ArrayList<>();
        Supplier<BigInteger> idSupp = new IncreasingBigIntegersSupplier();
        InputParams inputParams = InputParams.getInstance();

        double radius = inputParams.getRadius();
        int particleNumber = inputParams.getParticleNumber();

        try {
            System.out.println("Parsing dynamic file...");

            File file = new File(fileName);
            input = new Scanner(file);

            // TODO: skip to last timestamp, and then load those particles!
            input.nextLine(); // skip timestamp

            // reads until particles is full
            while (input.hasNext() && particles.size() < particleNumber) {
                String line = input.nextLine();
                if (Objects.equals(line, "---")) {

                    input.nextLine(); // skip timestamp
                    particles.clear();
                }

                // [x, y, vmag, vx, vy]
                // vx: xAxisProjectionAngle, vy: yAxisProjectionAngle

                List<Double> positionAndVelocity = Arrays.stream(input.nextLine().split("\\s\\s\\s"))
                        .filter(s -> !s.isEmpty()).map(Double::valueOf).collect(Collectors.toList());

                particles.add(new SimpleOffLaticeParticle2D(idSupp.get(), positionAndVelocity.get(0),
                        positionAndVelocity.get(1), positionAndVelocity.get(2),
                        positionAndVelocity.get(3), positionAndVelocity.get(4),
                        radius));
            }
            inputParams.setParticles(particles);
        } catch (FileNotFoundException e) {
            System.out.println("Error parsing dynamic file " + fileName);
        } finally {
            if (input != null)
                input.close();

            System.out.println("Dynamic file parsed successfully");
        }
        // return new ArrayList<>();
    }

    public void writeFile(int t, Collection<OffLaticeParticle2D> particles, String fileName) {
        System.out.println("Writing dynamic file...");

        try {
            File file = new File(fileName + ".txt");
            if (!file.exists())
                file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error creating file " + fileName + ".txt");
        }

        try {
            FileWriter fileWriter = new FileWriter(fileName + ".txt", false);

            if (t != 0)
                fileWriter.write("---\n");

            fileWriter.write("t" + t + "\n");
            for (OffLaticeParticle2D particle : particles) {
                // x y vmag vx vy
                fileWriter.write(
                        particle.getX() + " " + particle.getY() + " " + particle.getVelocityMagnitude() + " "
                                + particle.getVelocity().xAxisProjectionAngle() + " "
                                + particle.getVelocity().yAxisProjectionAngle());
            }

            fileWriter.close();

            // System.out.println("Wrote to file " + fileName + ".txt");
        } catch (IOException e) {
            System.out.println("Error writing to file " + fileName + ".txt");
        }
    }
}
