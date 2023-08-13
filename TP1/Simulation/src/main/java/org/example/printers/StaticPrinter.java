package org.example.printers;

import org.example.particles.Particle2D;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class StaticPrinter {

    private final String fileName;
    private final double l;
    private final double rc;

    public StaticPrinter(String fileName, double l, double rc) {
        this.fileName = fileName;
        this.l = l;
        this.rc = rc;
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
            System.out.println("Writing static file...");

            FileWriter fileWriter = new FileWriter(fileName + ".txt", false);

            fileWriter.write(Integer.toString(particles.size()) + "\n");
            fileWriter.write(Double.toString(l) + "\n");

            for (Particle2D particle : particles) {
                fileWriter.write( particle.getRadius() + "    " + rc + "\n");
            }

            fileWriter.close();

            System.out.println("Static file built successfully and wrote to file " + fileName + ".txt");
        } catch (IOException e) {
            System.out.println("Error writing to file " + fileName + ".txt");
        }
    }
}
