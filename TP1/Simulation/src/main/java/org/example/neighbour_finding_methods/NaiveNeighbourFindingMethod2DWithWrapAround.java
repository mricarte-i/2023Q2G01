package org.example.neighbour_finding_methods;

import org.example.distance_methods.DistanceMethod;
import org.example.particles.Particle2D;
import org.example.particles.SimpleVirtualParticle2D;
import org.example.particles.VirtualParticle2D;
import org.example.points.Point2D;

import java.util.*;

public class NaiveNeighbourFindingMethod2DWithWrapAround implements NeighbourFindingMethod<Point2D, Particle2D> {
    private final NeighbourFindingMethod<Point2D, VirtualParticle2D<Particle2D>> neighbourFindingMethod;
    private final double l;

    public NaiveNeighbourFindingMethod2DWithWrapAround(DistanceMethod<Point2D> distanceMethod, double l){
        this.neighbourFindingMethod = new NaiveNeighbourFindingMethod<>(distanceMethod);
        this.l = l;
    }

    @Override
    public Map<Particle2D, Collection<Particle2D>> calculateNeighbours(Collection<Particle2D> particles, double neighbourhoodRadius) {
        Map<VirtualParticle2D<Particle2D>, Collection<VirtualParticle2D<Particle2D>>> virtualNeighboursMap = calculateNeighboursWithVirtualParticles(particles, neighbourhoodRadius);
        return convertVirtualToRealNeighbourhood(virtualNeighboursMap);
    }

    private Map<VirtualParticle2D<Particle2D>, Collection<VirtualParticle2D<Particle2D>>> calculateNeighboursWithVirtualParticles(Collection<Particle2D> particles, double neighbourhoodRadius) {
        List<VirtualParticle2D<Particle2D>> realAndVirtualParticles = new ArrayList<>(particles.size()*3);
        for (Particle2D particle : particles) {

            if        (particle.getPosition().getX() <= l / 2) {
                realAndVirtualParticles.add(new SimpleVirtualParticle2D<>(particle, l, 0.0d));
            } else if (particle.getPosition().getX() > l / 2) {
                realAndVirtualParticles.add(new SimpleVirtualParticle2D<>(particle, -l, 0.0d));
            }

            if        (particle.getPosition().getY() <= l / 2) {
                realAndVirtualParticles.add(new SimpleVirtualParticle2D<>(particle, 0.0d, l));
            } else if (particle.getPosition().getY() > l / 2) {
                realAndVirtualParticles.add(new SimpleVirtualParticle2D<>(particle, 0.0d, -l));
            }

            if        (particle.getPosition().getX() <= l / 2   && particle.getPosition().getY() <= l / 2) {
                realAndVirtualParticles.add(new SimpleVirtualParticle2D<>(particle, l, l));
            } else if (particle.getPosition().getX() > l / 2    && particle.getPosition().getY() <= l / 2) {
                realAndVirtualParticles.add(new SimpleVirtualParticle2D<>(particle, -l, l));
            } else if (particle.getPosition().getX() <= l / 2   && particle.getPosition().getY() > l / 2) {
                realAndVirtualParticles.add(new SimpleVirtualParticle2D<>(particle, l, -l));
            } else if (particle.getPosition().getX() > l / 2    && particle.getPosition().getY() > l / 2) {
                realAndVirtualParticles.add(new SimpleVirtualParticle2D<>(particle, -l, -l));
            }

            realAndVirtualParticles.add(new SimpleVirtualParticle2D<>(particle, 0d, 0d));
        }
        return neighbourFindingMethod.calculateNeighbours(realAndVirtualParticles, neighbourhoodRadius);
    }

    private Map<Particle2D, Collection<Particle2D>> convertVirtualToRealNeighbourhood(Map<VirtualParticle2D<Particle2D>, Collection<VirtualParticle2D<Particle2D>>> virtualNeighboursMap) {
        Map<Particle2D, Collection<Particle2D>> neighbourMap = new HashMap<>();
        for (VirtualParticle2D<Particle2D> virtualParticle2D : virtualNeighboursMap.keySet()){

            Particle2D realParticle = virtualParticle2D.getRealParticle();
            if (!neighbourMap.containsKey(realParticle)) neighbourMap.put(realParticle, new HashSet<>());

            Set<Particle2D> realParticleNeighbours = (HashSet<Particle2D>) neighbourMap.get(realParticle);
            for (VirtualParticle2D<Particle2D> virtualParticle2DNeighbour : virtualNeighboursMap.get(virtualParticle2D)) {
                realParticleNeighbours.add(virtualParticle2DNeighbour.getRealParticle());
            }
        }
        return neighbourMap;
    }
}
