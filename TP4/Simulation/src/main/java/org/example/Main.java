package org.example;

public class Main {
    public static void main(String[] args) {
        ParamsParser parser = ParamsParser.parse(args);
        if (parser != null) {
            parser.getSimulation().simulate();
        }
    }
}
