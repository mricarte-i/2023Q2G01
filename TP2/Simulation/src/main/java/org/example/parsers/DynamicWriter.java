package org.example.parsers;

import org.example.particles.OffLaticeParticle2D;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class DynamicWriter {

    private FileWriter fileWriter;
    private String fileName;

    public DynamicWriter(String fileName) {
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
    public void writeFile (Collection<OffLaticeParticle2D> particles, int t) {
        try {
            this.fileWriter.write( t + "\n");
            for(OffLaticeParticle2D p: particles){
                this.fileWriter.write(p.getX() + " " + p.getY() + " " + p.getVelocityMagnitude() + " "
                        + p.getVelocity().xAxisProjectionAngle() + " "
                        + p.getVelocity().yAxisProjectionAngle() + "\n");
            }

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
