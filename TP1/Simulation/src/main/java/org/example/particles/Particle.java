package org.example.particles;

import org.example.points.Point;

import java.math.BigInteger;

public interface Particle<P extends Point> {
    BigInteger getId();
    P getPosition();
    Double getRadius();
}
