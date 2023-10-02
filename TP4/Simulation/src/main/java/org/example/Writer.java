package org.example;

import org.example.integrators.Integrator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Writer {
    private FileWriter fw;
    private String fn;
    public Writer(String filename) {
        this.fn = filename;
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
            this.fw.write(integrator.getPosition() + " 0 " + integrator.getVelocity() + " 0\n");
        }catch (IOException e){
            throw new RuntimeException("Error writing to file " + fn);
        }
    }

    public void writeStatic(String staticFile, double L,  List<Particle> particles) {
        FileWriter staticFW;
        try {
            File file = new File(staticFile);
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            staticFW = new FileWriter(staticFile, true);
        }catch (IOException e) {
            throw new RuntimeException("Error creating file " + staticFile);
        }

        try{
            staticFW.write(particles.size() + "\n");
            staticFW.write(L + "\n");
            for(Particle particle: particles) {
                staticFW.write(particle.getMass() + " " + particle.getRadius() + "\n");
            }
        }catch (IOException e){
            throw new RuntimeException("Error writing to file " + staticFile);
        }

        try{
            staticFW.close();
        }catch (IOException e){
            throw new RuntimeException("Error closing file " + staticFile);
        }
        System.out.println("Successfully written to " + staticFile);
    }

    public void writeState(double timestamp, List<Particle> particles){
        try{
            this.fw.write(timestamp + "\n");
            for(Particle particle: particles) {
                this.fw.write(particle.getPosition() + " 0 " + particle.getVelocity() + " 0\n");
            }
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
