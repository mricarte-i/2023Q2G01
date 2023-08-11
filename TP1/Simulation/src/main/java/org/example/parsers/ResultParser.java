package org.example.parsers;

import org.example.particles.Particle2D;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ResultParser {
    public void parseAndWriteToFile (Map<Particle2D, Collection<Particle2D>> neighborsPerParticleId, String fileName) {
        try {
            System.out.println("Parsing result...");

            File file = new File(fileName + ".txt");
            if(file.exists())
                file.delete();
            file.createNewFile();
            writeFile(neighborsPerParticleId, fileName);
        } catch(IOException e) {
            System.out.println("Error creating file " + fileName + ".txt");
        }
    }

    private void writeFile(Map<Particle2D, Collection<Particle2D>> neighborsPerParticleId, String fileName){
        try {
            System.out.println("Writing output file...");

            FileWriter fileWriter = new FileWriter(fileName + ".txt", false);

            for (Particle2D particle : neighborsPerParticleId.keySet()) {
                StringBuilder line = new StringBuilder();
                int i = 0;
                for (Particle2D neighbor : neighborsPerParticleId.get(particle)) {
                    if (i == 0) {
                        i++;
                        line.append(neighbor.getId().toString());
                    } else {
                        line.append("    ").append(neighbor.getId().toString());
                    }
                }
                fileWriter.write("[" + particle.getId().toString() + "    " + line + "]\n");
            }

            fileWriter.close();

            System.out.println("Result parsed successfully and wrote to file " + fileName + ".txt");
        } catch (IOException e) {
            System.out.println("Error writing to file " + fileName + ".txt");
        }
    }

}
