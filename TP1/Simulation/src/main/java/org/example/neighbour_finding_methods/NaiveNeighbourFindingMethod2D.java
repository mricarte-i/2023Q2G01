package org.example.neighbour_finding_methods;

import org.example.distance_methods.DistanceMethod;
import org.example.particles.Particle;
import org.example.particles.Particle2D;
import org.example.points.Point2D;

import java.util.*;

public class NaiveNeighbourFindingMethod2D extends NaiveNeighbourFindingMethod<Point2D, Particle2D> {

    public NaiveNeighbourFindingMethod2D(DistanceMethod<Point2D> distanceMethod) {
        super(distanceMethod);
    }

    @Override
    public Map<Particle2D, Collection<Particle2D>> calculateNeighbours(Collection<Particle2D> particles, double neighbourhoodRadius) {
        return super.calculateNeighbours(particles, neighbourhoodRadius);
    }
}
