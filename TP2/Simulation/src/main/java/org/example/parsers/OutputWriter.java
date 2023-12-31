package org.example.parsers;

import java.io.*;

public class OutputWriter {

    private FileWriter fileWriter;
    private String fileName;

    public OutputWriter(String fileName) {
        this.fileName = fileName;
        try{
            File file = new File(fileName + ".txt");
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            this.fileWriter = new FileWriter(fileName + ".txt", true);
        }catch (IOException e) {
            System.out.println("Error creating file " + fileName + ".txt");
        }

    }
    public void writeFile (double va, int t) {
        try {
            this.fileWriter.write( t + "\n" + va + "\n");

            //System.out.println("Wrote to file " + fileName + ".txt");
        } catch (IOException e) {
            System.out.println("Error writing to file " + this.fileName + ".txt");
        }
    }

    public void close(){
        try {
            this.fileWriter.close();
        }catch (IOException e){
            System.out.println("Error closing file " + this.fileName + ".txt");
        }
    }
}
