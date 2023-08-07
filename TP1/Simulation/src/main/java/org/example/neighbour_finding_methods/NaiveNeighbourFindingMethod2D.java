package org.example.neighbour_finding_methods;

import org.example.distance_methods.DistanceMethod;
import org.example.particles.Particle;
import org.example.points.Point2D;

import java.util.*;

public class NaiveNeighbourFindingMethod2D extends NaiveNeighbourFindingMethod<Point2D> {

    public NaiveNeighbourFindingMethod2D(DistanceMethod<Particle<Point2D>> distanceMethod) {
        super(distanceMethod);
    }

    @Override
    public Map<Particle<Point2D>, Collection<Particle<Point2D>>> calculateNeighbours(Collection<Particle<Point2D>> particles, Double neighbourhoodRadius) {
        return super.calculateNeighbours(particles, neighbourhoodRadius);
    }
}
