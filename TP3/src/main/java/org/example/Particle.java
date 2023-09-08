package org.example;

import java.math.BigInteger;
import java.util.Objects;

public class Particle {
    // TODO: implement
    private Integer id;
    private double radius, mass, x, y, vx, vy;
    private int collisionCount = 0;

    // NOTE: initial speed is 0.01, random direction, vx vy are the components of v
    // initial positions should be within the first container
    public Particle(double rx, double ry, double vx, double vy, double mass, double radius) {
        this.x = rx;
        this.y = ry;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
        this.radius = radius;
    }

    public Particle(int id, double rx, double ry, double vx, double vy, double mass, double radius) {
        this(rx, ry, vx, vy, mass, radius);
        this.id = id;
    }

    private double getYWhenChangingContainer(Container c) {
        if (this.x > c.getW() && this.vx > 0) return Double.NaN;
        if (this.x < c.getW() && this.vx < 0) return Double.NaN;
        double tL = Math.abs((c.getW() - this.x) / this.vx);
        return this.y + this.vy * tL;
    }

    private boolean willChangeContainer(Container c) {
        double yL = getYWhenChangingContainer(c);
        if (Double.isNaN(yL)) return false;
        return yL > c.getR2LowerBound() &&
                yL < c.getR2UpperBound();
    }

    private double collidesWall(Container c, double v, double pos,
                                double leftLowerBound, double leftUpperBound,
                                double rightLowerBound, double rightUpperBound) {
        double upperBound = leftUpperBound;
        double lowerBound = leftLowerBound;
        if (willChangeContainer(c)) {
            upperBound = rightUpperBound;
            lowerBound = rightLowerBound;
        }
        if (v > 0) {
            return (upperBound - this.radius - pos) / v;
        } else if (v < 0) {
            return (lowerBound + this.radius - pos) / v;
        }
        return Double.POSITIVE_INFINITY;
    }

    // collidesX|Y detects wall collisions
    public double collidesX() {
        Container c = Container.getInstance();
        return collidesWall(c, this.vx, this.x,
                            0, c.getW(),
                            0, 2*c.getW());
    }

    public double collidesY() {
        Container c = Container.getInstance();
        return collidesWall(c, this.vy, this.y,
                            0, c.getH(),
                            c.getR2LowerBound(), c.getR2UpperBound());
    }

    // detects particle collisions
    public double collides(Particle b) {
        return Double.POSITIVE_INFINITY;
    }

    // applies velocity changes to this particle (vs walls or vs other particle)
    public void bounceX() {
        this.vx = -this.vx;
        this.collisionCount += 1;
    }

    public void bounceY() {
        this.vy = -this.vy;
        this.collisionCount += 1;
    }

    private double deltaR(){
        return 0;
    }

    public void bounce(Particle b) {
    }

    public int getCollisionCount() {
        return collisionCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this.id == null) return super.equals(o);
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Particle particle = (Particle) o;
        return Objects.equals(id, particle.id);
    }

    @Override
    public int hashCode() {
        if (this.id == null) return super.hashCode();
        return Objects.hash(id);
    }
}

class Container {
    private final double  L, w, h,
                    r2UpperBound, r2LowerBound;
    private static Container container = null;

    private Container(double L, double w, double h) {
        this.L = L;
        this.w = w;
        this.h = h;
        this.r2LowerBound = getR2LowerBound(L, h);
        this.r2UpperBound = getR2UpperBound(r2LowerBound, L);
    }

    double getL() {
        return L;
    }

    double getH() {
        return h;
    }

    double getW() {
        return w;
    }

    double getR2LowerBound() {
        return r2LowerBound;
    }

    double getR2UpperBound() {
        return r2UpperBound;
    }

    static Container getInstance() {
        if (container == null){
            ParamsParser paramsParser = ParamsParser.getInstance();
            double L = paramsParser.getL();
            double h = paramsParser.getH();
            double w = paramsParser.getW();
            container = new Container(L, w, h);
        }
        return container;
    }

    private double getR2LowerBound(double L, double h) {
        double r2LowerBound = (h - L) / 2;
        if (r2LowerBound < 0){
            return 0;
        }
        return r2LowerBound;
    }

    private double getR2UpperBound(double r2LowerBound, double L) {
        double r2UpperBound = r2LowerBound + L;
        if (r2UpperBound < 0){
            return 0;
        }
        return r2UpperBound;
    }
}
