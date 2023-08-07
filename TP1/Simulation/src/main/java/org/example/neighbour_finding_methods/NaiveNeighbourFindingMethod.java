package org.example.neighbour_finding_methods;

import org.example.distance_methods.DistanceMethod;
import org.example.particles.Particle;
import org.example.points.Point;

import java.util.*;

public abstract class NaiveNeighbourFindingMethod<P extends Point> implements NeighbourFindingMethod<P> {
    private final DistanceMethod<P> distanceMethod;

    public NaiveNeighbourFindingMethod(DistanceMethod<P> distanceMethod){
        this.distanceMethod = distanceMethod;
    }

    @Override
    public Map<Particle<P>, Collection<Particle<P>>> calculateNeighbours(Collection<Particle<P>> particles, Double neighbourhoodRadius) {
        Map<Particle<P>, Collection<Particle<P>>> neighbourMap = new HashMap<>();
        Set<Particle<P>> possibleNeighbours = new HashSet<>(particles);
        for (Particle<P> particle : particles) {
            Set<Particle<P>> particleNeighbours = new HashSet<>(particles.size());
            neighbourMap.put(particle, particleNeighbours);
        }
        for (Particle<P> particle : particles) {
            for (Particle<P> possibleNeighbour : possibleNeighbours){
                Set<Particle<P>> particleNeighbours = (Set<Particle<P>>) neighbourMap.get(particle);
                if (!particle.equals(possibleNeighbour) && particle.distanceTo(possibleNeighbour, distanceMethod) < neighbourhoodRadius) {
                    neighbourMap.get(particle).add(possibleNeighbour);
                    neighbourMap.get(possibleNeighbour).add(particle);
                }
            }
            possibleNeighbours.remove(particle);
        }
        return neighbourMap;
    }
}
