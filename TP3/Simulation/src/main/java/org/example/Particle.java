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

    public void updatePosition(double t) {
        this.x += vx * t;
        this.y += vy * t;
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

    private double getYWhenChangingContainer(@Nonnull Container c) {
        if (this.x > c.getW() && this.vx > 0)
            return Double.NaN;
        if (this.x < c.getW() && this.vx < 0)
            return Double.NaN;
        double tL = Math.abs((c.getW() - this.x) / this.vx);
        return this.y + this.vy * tL;
    }

    private boolean willChangeContainer(@Nonnull Container c) {
        double yL = getYWhenChangingContainer(c);
        if (Double.isNaN(yL))
            return false;
        return yL - this.radius > c.getR2LowerBound() &&
                yL + this.radius < c.getR2UpperBound();
    }

    private double collidesWall(@Nonnull Container c, double v, double pos,
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
                0, c.getR2RightBound());
    }

    public double collidesY() {
        Container c = Container.getInstance();
        return collidesWall(c, this.vy, this.y,
                0, c.getH(),
                c.getR2LowerBound(), c.getR2UpperBound());
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
        double cn = -1;

        double ady = Math.abs(this.x - x);
        double opp = Math.abs(this.y - y);
        double hip = Math.sqrt(Math.pow(ady, 2) + Math.pow(opp, 2));

        double cos = ady / hip;
        double sin = opp / hip;

        double cosPow2 = Math.pow(cos, 2);
        double sinPow2 = Math.pow(sin, 2);

        double fixedV = (-(cn + ct) * sin * cos);

        this.vx = (-cn * cosPow2 + ct * sinPow2) * this.vx + fixedV * this.vy;
        this.vy = fixedV * this.vx + (-cn * sinPow2 + ct * cosPow2) * this.vy;
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
    private static Container container = null;

    private Container(double L, double w, double h) {
        this.L = L;
        this.w = w;
        this.h = h;
        this.r2LowerBound = getR2LowerBound(L, h);
        this.r2UpperBound = getR2UpperBound(r2LowerBound, L);
        this.r2RightBound = w * 2;
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

    public double getR2RightBound() {
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
}
