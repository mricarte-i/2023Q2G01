package org.example.neighbour_finding_methods;

import org.example.particles.Particle;
import org.example.points.Point;

import java.util.Collection;
import java.util.Map;

public interface NeighbourFindingMethod<P extends Point> {
    Map<Particle<P>, Collection<Particle<P>>> calculateNeighbours(Collection<Particle<P>> particles);
}
