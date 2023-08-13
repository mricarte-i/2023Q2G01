package org.example.printers;

import org.example.parsers.DynamicParser;
import org.example.particles.Particle2D;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class DynamicPrinter {

    private final String fileName;

    public DynamicPrinter(String fileName) {
        this.fileName = fileName;
    }

    public void parseAndWriteToFile(Collection<Particle2D> particles) {
        try {
            System.out.println("Building dynamic file...");

            File file = new File(fileName + ".txt");
            if(file.exists())
                file.delete();
            file.createNewFile();
            writeFile(particles);
        } catch(IOException e) {
            System.out.println("Error creating file " + fileName + ".txt");
        }
    }

    public void writeFile(Collection<Particle2D> particles){
        try {
            System.out.println("Writing dynamic file...");

            FileWriter fileWriter = new FileWriter(fileName + ".txt", false);

            fileWriter.write(Integer.toString(0) + "\n");

            for (Particle2D particle : particles) {
                fileWriter.write( particle.getX() + " " + particle.getY() + "\n");
            }

            fileWriter.close();

            System.out.println("Dynamic file build successfully and wrote to file " + fileName + ".txt");
        } catch (IOException e) {
            System.out.println("Error writing to file " + fileName + ".txt");
        }
    }
}
