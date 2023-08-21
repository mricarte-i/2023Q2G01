package org.example.off_latice;

import java.util.Collection;
import java.util.Map;

import org.example.particles.Particle;
import org.example.points.Point;

public interface Evolver<T extends Point, P extends Particle<T>> {
  Collection<P> update(Collection<P> particles, Map<P, Collection<P>> neighboursData);
}
