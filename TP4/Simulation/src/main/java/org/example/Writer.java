package org.example;

import org.example.integrators.Integrator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {
    private FileWriter fw;
    private String fn;
    public Writer(String filename) {
        this.fn = filename + ".txt";
        try {
            File file = new File(fn);
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            this.fw = new FileWriter(fn, true);
        }catch (IOException e) {
            throw new RuntimeException("Error creating file " + fn);
        }
    }

    public void writeState(double timestamp, Integrator integrator){
        try{
            this.fw.write(timestamp + "\n");
            this.fw.write(integrator.getPosition() + " " + integrator.getVelocity() + "\n");
        }catch (IOException e){
            throw new RuntimeException("Error writing to file " + fn);
        }
    }

    public void close() {
        try{
            this.fw.close();
        }catch (IOException e){
            throw new RuntimeException("Error closing file " + fn);
        }
        System.out.println("Successfully written to " + fn);
    }
}
