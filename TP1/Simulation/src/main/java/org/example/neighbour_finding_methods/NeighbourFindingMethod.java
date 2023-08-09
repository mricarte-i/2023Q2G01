package org.example.neighbour_finding_methods;

import org.example.particles.Particle;
import org.example.points.Point;

import java.util.Collection;
import java.util.Map;

public interface NeighbourFindingMethod<T extends Point, P extends Particle<T>> {
    Map<P, Collection<P>> calculateNeighbours(Collection<P> particles, Double neighbourhoodRadius);
}
