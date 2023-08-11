package org.example.neighbour_finding_methods;

import org.example.distance_methods.DistanceMethod;
import org.example.particles.Particle2D;
import org.example.particles.SimpleVirtualParticle2D;
import org.example.particles.VirtualParticle2D;
import org.example.points.Point2D;

import java.util.*;

public class CellIndexMethod2DWithWrapAround<P extends Particle2D> extends CellIndexMethod2D<P> {

    public CellIndexMethod2DWithWrapAround(DistanceMethod<Point2D> distanceMethod, double l, int m) {
        super(distanceMethod, l, m);
    }

    @Override
    public Map<P, Collection<P>> calculateNeighbours(Collection<P> particles, double neighbourhoodRadius) {
        int           n                = particles.size();
        double        cellSide         = l / m;
        Integer[][]   heads            = new Integer[m][m];
        Integer[]     list             = new Integer[n];
        P[]           particleInfo     = (P[]) new Particle2D[n];
        loadParticlesIntoCells(particles, cellSide, heads, list, particleInfo);
        Map<P, Collection<P>> neighbourMap = calculateNeighboursFromCells(heads, list, particleInfo, neighbourhoodRadius);
        addWrapAroundVirtualNeighbours(heads, list, particleInfo, neighbourhoodRadius, neighbourMap);
        return neighbourMap;
    }

    private void calculateNeighboursBetween2VirtualNeighbouringCells(int realCellRow, int realCellCol, int virtualCellRow, int virtualCellCol, double deltaX, double deltaY, Integer[][] heads, Integer[] list, P[] particleInfo, double neighbourhoodRadius, Map<P, Collection<P>> neighbourMap) {
        Integer currRealCellListIndex = heads[realCellRow][realCellCol];
        while (currRealCellListIndex != null) {
            Integer currVirtualCellListIndex = heads[virtualCellRow][virtualCellCol];
            while (currVirtualCellListIndex != null) {
                if (particleInfo[currRealCellListIndex].distanceTo(new SimpleVirtualParticle2D<>(particleInfo[currVirtualCellListIndex], deltaX, deltaY), distanceMethod) <= neighbourhoodRadius && !particleInfo[currRealCellListIndex].equals(particleInfo[currVirtualCellListIndex])) {
                    ((Set<P>) neighbourMap.get(particleInfo[currRealCellListIndex])).add(particleInfo[currVirtualCellListIndex]);
                    ((Set<P>) neighbourMap.get(particleInfo[currVirtualCellListIndex])).add(particleInfo[currRealCellListIndex]);
                }
                currVirtualCellListIndex = list[currVirtualCellListIndex];
            }
            currRealCellListIndex = list[currRealCellListIndex];
        }
    }

    private void addWrapAroundVirtualNeighbours(Integer[][] heads, Integer[] list, P[] particleInfo, double neighbourhoodRadius, Map<P, Collection<P>> neighbourMap) {
        for (int i = 0; i < m; i++) {
            calculateNeighboursBetween2VirtualNeighbouringCells(
                    0, i,
                    m-1, i,
                    0,-l,
                    heads, list, particleInfo, neighbourhoodRadius, neighbourMap
            );
            if (isValidCell(m-1, i+1))
                calculateNeighboursBetween2VirtualNeighbouringCells(
                        0, i,
                        m-1, i+1,
                        0,-l,
                        heads, list, particleInfo, neighbourhoodRadius, neighbourMap
                );
        }
        calculateNeighboursBetween2VirtualNeighbouringCells(
                0, m-1,
                m-1, 0,
                l,-l,
                heads, list, particleInfo, neighbourhoodRadius, neighbourMap
        );
        for (int i = 0; i < m; i++) {
            if (isValidCell(i-1, 0))
                calculateNeighboursBetween2VirtualNeighbouringCells(
                        i, m-1,
                        i-1, 0,
                        l,0,
                        heads, list, particleInfo, neighbourhoodRadius, neighbourMap
                );
            calculateNeighboursBetween2VirtualNeighbouringCells(
                    i, m-1,
                    i, 0,
                    l,0,
                    heads, list, particleInfo, neighbourhoodRadius, neighbourMap
            );
            if (isValidCell(i+1, 0))
                calculateNeighboursBetween2VirtualNeighbouringCells(
                        i, m-1,
                        i+1, 0,
                        l,0,
                        heads, list, particleInfo, neighbourhoodRadius, neighbourMap
                );
        }
        calculateNeighboursBetween2VirtualNeighbouringCells(
                m-1, m-1,
                0, 0,
                l,l,
                heads, list, particleInfo, neighbourhoodRadius, neighbourMap
        );
    }
}
