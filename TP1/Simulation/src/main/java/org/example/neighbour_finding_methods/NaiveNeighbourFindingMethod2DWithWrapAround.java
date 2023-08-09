package org.example.neighbour_finding_methods;

import org.example.distance_methods.DistanceMethod;
import org.example.particles.Particle;
import org.example.particles.Particle2D;
import org.example.particles.VirtualParticle2D;
import org.example.points.Point;
import org.example.points.Point2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class NaiveNeighbourFindingMethod2DWithWrapAround implements NeighbourFindingMethod<Point2D, Particle2D> {
    private final NeighbourFindingMethod<Point2D, Particle2D> neighbourFindingMethod;
    private final Double l;

    public NaiveNeighbourFindingMethod2DWithWrapAround(DistanceMethod<Point2D> distanceMethod, Double l){
        this.neighbourFindingMethod = new NaiveNeighbourFindingMethod<>(distanceMethod);
        this.l = l;
    }

    @Override
    public Map<Particle2D, Collection<Particle2D>> calculateNeighbours(Collection<Particle2D> particles, Double neighbourhoodRadius) {
        List<Particle2D> realAndVirtualParticles = new ArrayList<>(particles.size()*3);
        for (Particle2D particle : particles) {

            if        (particle.getPosition().getX() <= l / 2) {
                realAndVirtualParticles.add(new VirtualParticle2D(particle, l, 0.0d));
            } else if (particle.getPosition().getX() > l / 2) {
                realAndVirtualParticles.add(new VirtualParticle2D(particle, -l, 0.0d));
            }

            if        (particle.getPosition().getY() <= l / 2) {
                realAndVirtualParticles.add(new VirtualParticle2D(particle, 0.0d, l));
            } else if (particle.getPosition().getY() > l / 2) {
                realAndVirtualParticles.add(new VirtualParticle2D(particle, 0.0d, -l));
            }

            if        (particle.getPosition().getX() <= l / 2   && particle.getPosition().getY() <= l / 2) {
                realAndVirtualParticles.add(new VirtualParticle2D(particle, l, l));
            } else if (particle.getPosition().getX() > l / 2    && particle.getPosition().getY() <= l / 2) {
                realAndVirtualParticles.add(new VirtualParticle2D(particle, -l, l));
            } else if (particle.getPosition().getX() <= l / 2   && particle.getPosition().getY() > l / 2) {
                realAndVirtualParticles.add(new VirtualParticle2D(particle, l, -l));
            } else if (particle.getPosition().getX() > l / 2    && particle.getPosition().getY() > l / 2) {
                realAndVirtualParticles.add(new VirtualParticle2D(particle, -l, -l));
            }

            realAndVirtualParticles.add(particle);
        }
        return neighbourFindingMethod.calculateNeighbours(realAndVirtualParticles, neighbourhoodRadius);
    }
}
