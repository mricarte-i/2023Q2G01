package org.example.neighbour_finding_methods;

import org.example.distance_methods.DistanceMethod;
import org.example.exceptions.InvalidNeighbourhoodRadiusException;
import org.example.particles.Particle2D;
import org.example.points.Point2D;
import org.example.utils.Pair;

import java.util.*;

public class CellIndexMethod2D<P extends Particle2D> implements NeighbourFindingMethod<Point2D, P> {

    protected final DistanceMethod<Point2D> distanceMethod;
    protected final double l;
    protected final int m;


    public CellIndexMethod2D(DistanceMethod<Point2D> distanceMethod, double l, int m){
        this.distanceMethod = distanceMethod;
        this.l              = l;
        this.m              = m;
    }

    @Override
    public Map<P, Collection<P>> calculateNeighbours(Collection<P> particles, double neighbourhoodRadius) {
        int          n              = particles.size();
        double       cellSide       = l / m;
        Pair<Double> top2Radii = biggest2ParticleRadii(particles);
        if (cellSide <= neighbourhoodRadius + top2Radii.getFirst() + top2Radii.getSecond()) {
           throw new InvalidNeighbourhoodRadiusException(cellSide, neighbourhoodRadius, top2Radii.getFirst(), top2Radii.getSecond());
        }
        Integer[][]  heads          = new Integer[m][m];
        Integer[]    list           = new Integer[n];
        P[]          particleInfo   = (P[]) new Particle2D[n];
        loadParticlesIntoCells(particles, cellSide, heads, list, particleInfo);
        return calculateNeighboursFromCells(heads, list, particleInfo, neighbourhoodRadius);
    }

    public void loadParticlesIntoCells(Collection<P> particles, double cellSide, Integer[][] heads, Integer[] list, P[] particleInfo) {
        int i = 0;
        for (P particle : particles) {
            int row = (int) Math.floor(particle.getY() / cellSide);
            int col = (int) Math.floor(particle.getX() / cellSide);
            if (isValidCell(row, col)){
                Integer currHeadParticleIndex = heads[row][col];
                list[i]         = currHeadParticleIndex;
                heads[row][col] = i;
                particleInfo[i] = particle;
                i++;
            }
        }
    }

    public Map<P, Collection<P>> calculateNeighboursFromCells(Integer[][] heads, Integer[] list, P[] particleInfo, double neighbourhoodRadius) {
        Map<P, Collection<P>> neighbourMap = new HashMap<>();
        for (P particle : particleInfo) {
            if (particle != null) neighbourMap.put(particle, new HashSet<>());
        }
        for (int row = 0; row < heads.length; row++) {
            for (int col = 0; col < heads[row].length; col++) {
                calculateNeighboursBetween2Cells(row, col, row, col, heads, list, particleInfo, neighbourhoodRadius, neighbourMap);
                Iterable<Pair<Integer>> neighbouringCells = getNeighbouringCells(row, col);
                for (Pair<Integer> neighbourCell : neighbouringCells) {
                    calculateNeighboursBetween2Cells(row, col, neighbourCell.getFirst(), neighbourCell.getSecond(), heads, list, particleInfo, neighbourhoodRadius, neighbourMap);
                }
            }
        }
        return neighbourMap;
    }

    protected Pair<Double> biggest2ParticleRadii(Collection<P> particles) {
        double maxRadius       = 0d;
        double secondMaxRadius = 0d;
        for (P particle : particles) {
            if(particle.getRadius() > maxRadius) {
                secondMaxRadius = maxRadius;
                maxRadius       = particle.getRadius();
            }
        }
        return new Pair<>(maxRadius, secondMaxRadius);
    }

    private void calculateNeighboursBetween2Cells(int cell1Row, int cell1Col, int cell2Row, int cell2Col, Integer[][] heads, Integer[] list, P[] particleInfo, double neighbourhoodRadius, Map<P, Collection<P>> neighbourMap) {
        Integer currCell1ListIndex = heads[cell1Row][cell1Col];
        while (currCell1ListIndex != null) {
            Integer currCell2ListIndex = heads[cell2Row][cell2Col];
            while (currCell2ListIndex != null) {
                if (particleInfo[currCell1ListIndex].distanceTo(particleInfo[currCell2ListIndex], distanceMethod) <= neighbourhoodRadius && !particleInfo[currCell1ListIndex].equals(particleInfo[currCell2ListIndex])) {
                    ((Set<P>) neighbourMap.get(particleInfo[currCell1ListIndex])).add(particleInfo[currCell2ListIndex]);
                    ((Set<P>) neighbourMap.get(particleInfo[currCell2ListIndex])).add(particleInfo[currCell1ListIndex]);
                }
                currCell2ListIndex = list[currCell2ListIndex];
            }
            currCell1ListIndex = list[currCell1ListIndex];
        }
    }

    private Iterable<Pair<Integer>> getNeighbouringCells(int row, int col) {
        List<Pair<Integer>> neighbouringCells = new ArrayList<>(4);
        if (isValidCell(row - 1, col)) {
            neighbouringCells.add(new Pair<>(row - 1, col));
        }
        if (isValidCell(row - 1, col + 1)) {
            neighbouringCells.add(new Pair<>(row - 1, col + 1));
        }
        if (isValidCell(row, col + 1)) {
            neighbouringCells.add(new Pair<>(row, col + 1));
        }
        if (isValidCell(row + 1, col + 1)) {
            neighbouringCells.add(new Pair<>(row + 1, col + 1));
        }
        return neighbouringCells;
    }

    protected boolean isValidCell(int row, int col) {
        if (row < 0   || col < 0) return false;
        if (row > m-1 || col > m-1) return false;
        return true;
    }
}
