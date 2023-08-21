package org.example.parsers;

import java.io.*;

public class OutputWriter {
    public void writeFile (double va, int t, String fileName) {
        System.out.println("Writing output file...");

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
            fileWriter.write("Va" + va + "\n");

            fileWriter.close();

            System.out.println("Wrote to file " + fileName + ".txt");
        } catch (IOException e) {
            System.out.println("Error writing to file " + fileName + ".txt");
        }
    }
}
