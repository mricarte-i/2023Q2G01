package org.example;

import javax.annotation.Nonnull;
import java.util.Objects;

public class Particle {
    // TODO: implement
    private Integer id;
    private double radius, mass, x, y, vx, vy;
    private int collisionCount = 0;

    public void setPosition(double newX, double newY) {
        this.x = newX;
        this.y = newY;
    }

    public double getPositionX() {
        return x;
    }

    public double getPositionY() {
        return y;
    }

    private double getXPositionAt(double t) {
        return this.x + vx * t;
    }

    private double getYPositionAt(double t) {
        return this.y + vy * t;
    }

    public void updatePosition(double t) {
        this.x = getXPositionAt(t);
        this.y = getYPositionAt(t);
    }

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

    // collidesX|Y detects wall collisions
    public double collidesX() {
        Container c = Container.getInstance();
        if (this.vx < 0) {
            return (this.radius - this.x) / this.vx;
        }
        if (this.vx > 0) {
            double tColSep     = (c.getW() - this.radius - this.x) / this.vx;
            double tColR2Right = (c.getR2RightBound() - this.radius - this.x) / this.vx;
            if (tColSep < 0)
                return tColR2Right;
            double yAtSep = getYPositionAt(tColSep);
            if (yAtSep < c.getR2UpperBound() && yAtSep > c.getR2LowerBound()){
                return tColR2Right;
            }
            return tColSep;
        }
        return Double.POSITIVE_INFINITY;
    }

    public double getRadius() {
        return radius;
    }

    public double getMass() {
        return mass;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public double collidesY() {
        Container c = Container.getInstance();
        if (this.vy > 0) {
            double tColR1 = (c.getH() - this.radius - this.y) / this.vy;
            double tColR2 = (c.getR2UpperBound() - this.radius - this.y) / this.vy;
            if (tColR2 < 0)
                return tColR1;
            double xAtColR2 = getXPositionAt(tColR2);
            if (xAtColR2 > c.getW()) {
                return tColR2;
            }
            return tColR1;
        }
        if (this.vy < 0) {
            double tColR1 = (this.radius - this.y) / this.vy;
            double tColR2 = (c.getR2LowerBound() + this.radius - this.y) / this.vy;
            if (tColR2 < 0)
                return tColR1;
            double xAtColR2 = getXPositionAt(tColR2);
            if (xAtColR2 > c.getW()) {
                return tColR2;
            }
            return tColR1;
        }
        return Double.POSITIVE_INFINITY;
    }

    private double getSigma(@Nonnull Particle b) {
        return b.radius + this.radius;
    }

    private double getDeltaRx(@Nonnull Particle b) {
        return b.x - this.x;
    }

    private double getDeltaRy(@Nonnull Particle b) {
        return b.y - this.y;
    }

    private double getDeltaVx(@Nonnull Particle b) {
        return b.vx - this.vx;
    }

    private double getDeltaVy(@Nonnull Particle b) {
        return b.vy - this.vy;
    }

    private double getDeltaRPow2(@Nonnull Particle b) {
        return Math.pow(getDeltaRx(b), 2) + Math.pow(getDeltaRy(b), 2);
    }

    private double getDeltaVPow2(@Nonnull Particle b) {
        return Math.pow(getDeltaVx(b), 2) + Math.pow(getDeltaVy(b), 2);
    }

    private double getDotDeltaVR(@Nonnull Particle b) {
        return getDeltaVx(b) * getDeltaRx(b) + getDeltaVy(b) * getDeltaRy(b);
    }

    // detects particle collisions
    public double collides(@Nonnull Particle b) {
        double dotDeltaVR = getDotDeltaVR(b);
        if (dotDeltaVR >= 0)
            return Double.POSITIVE_INFINITY;

        double deltaVPow2 = getDeltaVPow2(b);
        double deltaRPow2 = getDeltaRPow2(b);
        double sigmaPow2 = Math.pow(getSigma(b), 2);
        double d = Math.pow(dotDeltaVR, 2) - deltaVPow2 * (deltaRPow2 - sigmaPow2);
        if (d < 0)
            return Double.POSITIVE_INFINITY;

        return -(dotDeltaVR + Math.sqrt(d)) / deltaVPow2;
    }

    private void bounceRigid(double x, double y) {
        double ct = 1;
        double cn = 1;

        double ady = this.x - x;
        double opp = this.y - y;
        double alpha = Math.atan(opp / ady);

        double cos = Math.cos(alpha);
        double sin = Math.sin(alpha);

        double cosPow2 = Math.pow(cos, 2);
        double sinPow2 = Math.pow(sin, 2);

        double fixedV = (-(cn + ct) * sin * cos);

        double currVx = this.vx;
        double currVy = this.vy;

        this.vx = (-cn * cosPow2 + ct * sinPow2) * currVx + fixedV * currVy;
        this.vy = fixedV * currVx + (-cn * sinPow2 + ct * cosPow2) * currVy;

        this.collisionCount += 1;
    }

    private void bounceUpperVertex(@Nonnull Container c) {
        Particle upperVertex = c.getR2UpperVertex();
        bounceRigid(upperVertex.x, upperVertex.y);
    }

    private void bounceLowerVertex(@Nonnull Container c) {
        Particle lowerVertex = c.getR2LowerVertex();
        bounceRigid(lowerVertex.x, lowerVertex.y);
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

    private double getJ(@Nonnull Particle b, double sigma) {
        return (2 * this.mass * b.mass * getDotDeltaVR(b)) / (sigma * (this.mass + b.mass));
    }

    private double getJx(@Nonnull Particle b, double J, double sigma) {
        return (J * getDeltaRx(b)) / sigma;
    }

    private double getJy(@Nonnull Particle b, double J, double sigma) {
        return (J * getDeltaRy(b)) / sigma;
    }

    public void bounce(@Nonnull Particle b) {
        Container c = Container.getInstance();

        if (b == c.getR2UpperVertex()) {
            bounceUpperVertex(c);
            return;
        }

        if (b == c.getR2LowerVertex()) {
            bounceLowerVertex(c);
            return;
        }

        double sigma = getSigma(b);
        double J = getJ(b, sigma);
        double Jx = getJx(b, J, sigma);
        double Jy = getJy(b, J, sigma);

        this.vx = this.vx + Jx / this.mass;
        this.vy = this.vy + Jy / this.mass;
        this.collisionCount += 1;

        b.vx = b.vx - Jx / b.mass;
        b.vy = b.vy - Jy / b.mass;
        b.collisionCount += 1;
    }

    public Particle getUpperVertex() {
        return Container.getInstance().getR2UpperVertex();
    }

    public Particle getLowerVertex() {
        return Container.getInstance().getR2LowerVertex();
    }

    public int getCollisionCount() {
        return collisionCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this.id == null)
            return super.equals(o);
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Particle particle = (Particle) o;
        return Objects.equals(id, particle.id);
    }

    @Override
    public int hashCode() {
        if (this.id == null)
            return super.hashCode();
        return Objects.hash(id);
    }
}

class Container {
    private final double L, w, h,
            r2UpperBound, r2LowerBound, r2RightBound;
    private final Particle r2UpperVertex, r2LowerVertex;
    private static Container container = null;

    private Container(double L, double w, double h) {
        this.L = L;
        this.w = w;
        this.h = h;
        this.r2LowerBound = getR2LowerBound(L, h);
        this.r2UpperBound = getR2UpperBound(r2LowerBound, L);
        this.r2RightBound = w * 2;
        this.r2UpperVertex = new Particle(w, r2UpperBound, 0d, 0d, Double.POSITIVE_INFINITY, 0d);
        this.r2LowerVertex = new Particle(w, r2LowerBound, 0d, 0d, Double.POSITIVE_INFINITY, 0d);
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

    double getR2RightBound() {
        return r2RightBound;
    }

    static Container getInstance() {
        if (container == null) {
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
        if (r2LowerBound < 0) {
            return 0;
        }
        return r2LowerBound;
    }

    private double getR2UpperBound(double r2LowerBound, double L) {
        double r2UpperBound = r2LowerBound + L;
        if (r2UpperBound < 0) {
            return 0;
        }
        return r2UpperBound;
    }

    Particle getR2LowerVertex() {
        return r2LowerVertex;
    }

    Particle getR2UpperVertex() {
        return r2UpperVertex;
    }
}
