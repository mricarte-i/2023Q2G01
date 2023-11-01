package org.example;

import org.example.integrators.Integrator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Writer {
    private final FileWriter fileWriter, exitingParticlesFileWriter;
    private final String fileName, exitingParticlesFileName;
    public Writer(String filename, String exitingParticlesFileName) {
        this.fileName = filename;
        this.exitingParticlesFileName = exitingParticlesFileName;

        try {
            File file = new File(fileName);
            if(file.exists())
                file.delete();
            file.createNewFile();
            this.fileWriter = new FileWriter(fileName, true);

            File exitingParticlesFile = new File(exitingParticlesFileName);
            if(exitingParticlesFile.exists())
                exitingParticlesFile.delete();
            file.createNewFile();
            this.exitingParticlesFileWriter = new FileWriter(exitingParticlesFileName, true);
        } catch (IOException e) {
            throw new RuntimeException("Error creating file " + fileName + " or " + exitingParticlesFileName);
        }
    }


    public void writeStatic(String staticFile, double L,  double W, double D, double omega, List<Particle> particles) {
        FileWriter staticfileWriter;
        try {
            File file = new File(staticFile);
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            staticfileWriter = new FileWriter(staticFile, true);
        }catch (IOException e) {
            throw new RuntimeException("Error creating file " + staticFile);
        }

        try{
            staticfileWriter.write(particles.size() + "\n");
            staticfileWriter.write(W + "\n");
            staticfileWriter.write(L + "\n");
            staticfileWriter.write(D + "\n");
            staticfileWriter.write(omega + "\n");
            for(Particle particle: particles) {
                staticfileWriter.write(particle.getMass() + " " + particle.getRadius() + "\n");
            }
        }catch (IOException e){
            throw new RuntimeException("Error writing to file " + staticFile);
        }

        try{
            staticfileWriter.close();
        }catch (IOException e){
            throw new RuntimeException("Error closing file " + staticFile);
        }
        System.out.println("Successfully written to " + staticFile);
    }


    public void writeState(double timestamp, List<Particle> particles){
        try{
            this.fileWriter.write(timestamp + "\n");
            for(Particle particle: particles) {
                this.fileWriter.write(particle.getPositionX() + " " + particle.getPositionY() + " " + particle.getVelocityX() + " " + particle.getVelocityY() + "\n");
            }
        }catch (IOException e){
            throw new RuntimeException("Error writing to file " + fileName);
        }
    }

    public void writeExitingParticle(double timestamp){
        try{
            this.exitingParticlesFileWriter.write(timestamp + "\n");
        }catch (IOException e){
            throw new RuntimeException("Error writing to file " + exitingParticlesFileWriter);
        }
    }

    public void close() {
        try{
            this.fileWriter.close();
            this.exitingParticlesFileWriter.close();
        }catch (IOException e){
            throw new RuntimeException("Error closing files");
        }
        System.out.println("Successfully written files");
    }
}
