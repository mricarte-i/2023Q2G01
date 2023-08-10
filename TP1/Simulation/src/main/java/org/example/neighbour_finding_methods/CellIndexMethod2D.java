package org.example.neighbour_finding_methods;

import org.example.distance_methods.DistanceMethod;
import org.example.particles.Particle2D;
import org.example.points.Point2D;
import org.example.utils.Pair;

import java.util.*;

public class CellIndexMethod2D implements NeighbourFindingMethod<Point2D, Particle2D> {

    private final DistanceMethod<Point2D> distanceMethod;
    private final double l;
    private final int m;

    public CellIndexMethod2D(DistanceMethod<Point2D> distanceMethod, double l, int m){
        this.distanceMethod = distanceMethod;
        this.l              = l;
        this.m              = m;
    }

    @Override
    public Map<Particle2D, Collection<Particle2D>> calculateNeighbours(Collection<Particle2D> particles, Double neighbourhoodRadius) {
        int          n              = particles.size();
        double       cellSide       = l / m;
        Integer[][]  heads          = new Integer[m][m];
        Integer[]    list           = new Integer[n];
        Particle2D[] particleInfo   = new Particle2D[n];
        loadParticlesIntoCells(particles, cellSide, heads, list, particleInfo);
        return calculateNeighboursFromCells(heads, list, particleInfo, neighbourhoodRadius);
    }

    private void loadParticlesIntoCells(Collection<Particle2D> particles, double cellSide, Integer[][] heads, Integer[] list, Particle2D[] particleInfo) {
        int i = 0;
        for (Particle2D particle : particles) {
            int row = (int) Math.floor(particle.getY() / cellSide);
            int col = (int) Math.floor(particle.getX() / cellSide);
            Integer currHeadParticleIndex = heads[row][col];
            list[i]         = currHeadParticleIndex;
            heads[row][col] = i;
            particleInfo[i] = particle;
            i++;
        }
    }

    private Map<Particle2D, Collection<Particle2D>> calculateNeighboursFromCells(Integer[][] heads, Integer[] list, Particle2D[] particleInfo, Double neighbourhoodRadius) {
        Map<Particle2D, Collection<Particle2D>> neighbourMap = new HashMap<>();
        for (Particle2D particle : particleInfo) {
            neighbourMap.put(particle, new HashSet<>());
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

    private void calculateNeighboursBetween2Cells(int cell1Row, int cell1Col, int cell2Row, int cell2Col, Integer[][] heads, Integer[] list, Particle2D[] particleInfo, Double neighbourhoodRadius, Map<Particle2D, Collection<Particle2D>> neighbourMap) {
        Integer currCell1ListIndex = heads[cell1Row][cell1Col];
        while (currCell1ListIndex != null) {
            Integer currCell2ListIndex = heads[cell2Row][cell2Col];
            while (currCell2ListIndex != null) {
                if (particleInfo[currCell1ListIndex].distanceTo(particleInfo[currCell2ListIndex], distanceMethod) <= neighbourhoodRadius) {
                    ((Set<Particle2D>) neighbourMap.get(particleInfo[currCell1ListIndex])).add(particleInfo[currCell2ListIndex]);
                    ((Set<Particle2D>) neighbourMap.get(particleInfo[currCell2ListIndex])).add(particleInfo[currCell1ListIndex]);
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

    private boolean isValidCell(int row, int col) {
        if (row < 0   || col < 0) return false;
        if (row > m-1 || col > m-1) return false;
        return true;
    }
}
