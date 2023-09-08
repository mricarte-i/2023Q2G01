package org.example;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        ParamsParser paramsParser = ParamsParser.getInstance();
        paramsParser.parse(args);

        ParticleCollisionSystem pcs = new ParticleCollisionSystem();

        pcs.simulate();
    }
}