package org.example.parsers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ResultParser {
    public void parseAndWriteToFile (Map<String, List<String>> neighborsPerParticleId, String fileName) {
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

    private void writeFile(Map<String, List<String>> neighborsPerParticleId, String fileName){
        try {
            System.out.println("Writing output file...");

            FileWriter fileWriter = new FileWriter(fileName + ".csv");

            for (String particleId : neighborsPerParticleId.keySet()) {
                StringBuilder line = new StringBuilder();
                int i = 0;
                for (String neighborId : neighborsPerParticleId.get(particleId)) {
                    if (i == 0) {
                        i++;
                        line.append(neighborId);
                    } else {
                        line.append("    ").append(neighborId);
                    }
                }
                fileWriter.write("[" + particleId + "    " + line + "]\n");
            }

            fileWriter.close();

            System.out.println("Result parsed successfully and wrote to file " + fileName + ".txt");
        } catch (IOException e) {
            System.out.println("Error writing to file " + fileName + ".txt");
        }
    }

}
