package org.example.neighbour_finding_methods;

import org.example.distance_methods.DistanceMethod;
import org.example.particles.Particle;
import org.example.points.Point;

import java.util.*;

public abstract class NaiveNeighbourFindingMethod<P extends Point> implements NeighbourFindingMethod<P> {
    private final DistanceMethod<Particle<P>> distanceMethod;

    public NaiveNeighbourFindingMethod(DistanceMethod<Particle<P>> distanceMethod){
        this.distanceMethod = distanceMethod;
    }

    @Override
    public Map<Particle<P>, Collection<Particle<P>>> calculateNeighbours(Collection<Particle<P>> particles, Double neighbourhoodRadius) {
        Map<Particle<P>, Collection<Particle<P>>> neighbourMap = new HashMap<>();
        for (Particle<P> particle : particles){
            Set<Particle<P>> particleNeighbours = new HashSet<>(particles.size());
            neighbourMap.put(particle, particleNeighbours);
            for (Particle<P> possibleNeighbour : particles){
                if (!particleNeighbours.contains(possibleNeighbour) && particle.distanceTo(possibleNeighbour, distanceMethod) < neighbourhoodRadius) {
                    particleNeighbours.add(possibleNeighbour);
                }
            }
        }
        return neighbourMap;
    }
}
