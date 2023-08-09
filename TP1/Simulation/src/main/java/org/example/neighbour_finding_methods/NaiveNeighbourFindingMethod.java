package org.example.neighbour_finding_methods;

import org.example.distance_methods.DistanceMethod;
import org.example.particles.Particle;
import org.example.points.Point;

import java.util.*;

public class NaiveNeighbourFindingMethod<T extends Point, P extends Particle<T>> implements NeighbourFindingMethod<T, P> {
    private final DistanceMethod<T> distanceMethod;

    public NaiveNeighbourFindingMethod(DistanceMethod<T> distanceMethod){
        this.distanceMethod = distanceMethod;
    }

    @Override
    public Map<P, Collection<P>> calculateNeighbours(Collection<P> particles, Double neighbourhoodRadius) {
        Map<P, Collection<P>> neighbourMap = new HashMap<>();
        Set<P> possibleNeighbours = new HashSet<>(particles);
        for (P particle : particles) {
            Set<P> particleNeighbours = new HashSet<>(particles.size());
            neighbourMap.put(particle, particleNeighbours);
        }
        for (P particle : particles) {
            for (P possibleNeighbour : possibleNeighbours){
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
