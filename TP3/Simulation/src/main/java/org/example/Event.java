package org.example;

public class Event {
    private double t;
    private Particle p1, p2;
    public Event(double t, Particle a, Particle b){

    }

    public double getTime(){return 0;}
    public Particle getParticle1(){return this.p1;}
    public Particle getParticle2(){return this.p2;}

    public int compareTo(Object o){return 0;}
    public boolean wasSuperveningEvent(){return false;}

}
