package org.example;

import org.example.integrators.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

public class Particle {
  private final int id;
  private final BeemanIntegrator xIntegrator;
  private final BeemanIntegrator yIntegrator;
  private final double mass, radius;
  private List<Particle> prevContacts, nextContacts;

  private boolean prevLeftWallContact, prevRightWallContact, prevTopWallContact;
  private boolean prevLeftVertexContact, prevBaseContact, prevRightVertexContact;
  private boolean nextLeftWallContact, nextRightWallContact, nextTopWallContact;
  private boolean nextLeftVertexContact, nextBaseContact, nextRightVertexContact;

  private static final int K = 250;
  private static final double GAMMA = 2.50;
  private static final int L = 70; // TODO: ver si se puede obtener el parametro de otro lado aunque valga siempre lo mismo
  private static final int W = 20; // TODO: ver si se puede obtener el parametro de otro lado aunque valga siempre lo mismo

  /*
  TODO
    for use in tangential force t1_2
  private double sumRelVel = 0;
   */

  private Particle(int id, double radius, double mass, double x, double y, double dT, double vx, double vy) {
    this.id = id;
    this.xIntegrator = new BeemanIntegrator(dT, x, vx, mass, (pos, vel) -> 0);
    this.yIntegrator = new BeemanIntegrator(dT, y, vy, mass, (pos, vel) -> 0);
    this.radius = radius;
    this.mass = mass;
  }

  public Particle(int id, double radius, double mass, double x, double y, double dT, double vx, double vy, Integer totalParticles) {
    this(id, radius, mass, x, y, dT, vx, vy);
    if (totalParticles != null) {
      this.prevContacts = new ArrayList<>(totalParticles);
      this.nextContacts = new ArrayList<>(totalParticles);
    } else {
      this.prevContacts = new LinkedList<>();
      this.nextContacts = new LinkedList<>();
    }
  }

  public void initialize() {
    xIntegrator.initPrevForce((prevX, prevVx) -> 0);
    yIntegrator.initPrevForce((prevY, prevVy) -> 0);

    prevContacts.clear();

    prevLeftWallContact    = false;
    prevRightWallContact   = false;
    prevTopWallContact     = false;

    prevLeftVertexContact  = false;
    prevBaseContact        = false;
    prevRightVertexContact = false;
  }

  private static boolean checkContactParticle(double x, double y, double r, double otherX, double otherY, double otherR) {
    double euclideanDistance = Math.sqrt(Math.pow(x - otherX, 2) + Math.pow(y - otherY, 2));
    return euclideanDistance <= r + otherR;
  }

  private static boolean checkContactWall(double position, double r, double wallPosition) {
    if (position < wallPosition)
      return wallPosition - position <= r;
    return position - wallPosition <= r;
  }

  private static boolean checkContactVerticalWall(double x, double r, double wallX) {
    return checkContactWall(x, r, wallX);
  }

  private static boolean checkContactHorizontalWall(double y, double r, double wallY) {
    return checkContactWall(y, r, wallY);
  }

  private static boolean checkContactObstacle(double x, double y, double r, double obsX, double obsY) {
    return checkContactParticle(x, y, r, obsX, obsY, 0);
  }

  public void checkNextStepContact(Particle p) {
    double nextX = this.xIntegrator.getNextPosition();
    double nextY = this.yIntegrator.getNextPosition();

    double nextOtherX = p.xIntegrator.getNextPosition();
    double nextOtherY = p.yIntegrator.getNextPosition();

    if (checkContactParticle(nextX, nextY, radius, nextOtherX, nextOtherY, p.radius))
      nextContacts.add(p);
  }

  public void checkNextStepContactLeftWall(double leftWallX) {
    double nextX = this.xIntegrator.getNextPosition();

    if (checkContactVerticalWall(nextX, radius, leftWallX)) {
      nextLeftWallContact = true;
    }
  }

  public void checkNextStepContactRightWall(double rightWallX) {
    double nextX = this.xIntegrator.getNextPosition();

    if (checkContactVerticalWall(nextX, radius, rightWallX)) {
      nextRightWallContact = true;
    }
  }

  public void checkNextStepContactTopWall(double topWallY) {
    double nextY = this.yIntegrator.getNextPosition();

    if (checkContactHorizontalWall(nextY, radius, topWallY)) {
      nextTopWallContact = true;
    }
  }

  public void checkNextStepContactBase(double baseY) {
    double nextY = this.yIntegrator.getNextPosition();

    if (checkContactHorizontalWall(nextY, radius, baseY)) {
      nextBaseContact = true;
    }
  }

  public void checkNextStepContactLeftVertex(double leftVertexX, double leftVertexY) {
    double nextX = this.xIntegrator.getNextPosition();
    double nextY = this.yIntegrator.getNextPosition();

    if (checkContactObstacle(nextX, nextY, radius, leftVertexX, leftVertexY)) {
      nextLeftVertexContact = true;
    }
  }

  public void checkNextStepContactRightVertex(double rightVertexX, double rightVertexY) {
    double nextX = this.xIntegrator.getNextPosition();
    double nextY = this.yIntegrator.getNextPosition();

    if (checkContactObstacle(nextX, nextY, radius, rightVertexX, rightVertexY)){
      nextRightVertexContact = true;
    }
  }

  public void checkPrevStepContact(Particle p) {
    double prevX = this.xIntegrator.getPreviousPosition();
    double prevY = this.yIntegrator.getPreviousPosition();

    double prevOtherX = p.xIntegrator.getPreviousPosition();
    double prevOtherY = p.yIntegrator.getPreviousPosition();

    if (checkContactParticle(prevX, prevY, radius, prevOtherX, prevOtherY, p.radius))
      prevContacts.add(p);
  }

  public void checkPrevStepContactLeftWall(double leftWallX) {
    double prevX = this.xIntegrator.getPreviousPosition();

    if (checkContactVerticalWall(prevX, radius, leftWallX)) {
      prevLeftWallContact = true;
    }
  }

  public void checkPrevStepContactRightWall(double rightWallX) {
    double prevX = this.xIntegrator.getPreviousPosition();

    if (checkContactVerticalWall(prevX, radius, rightWallX)) {
      prevRightWallContact = true;
    }
  }

  public void checkPrevStepContactTopWall(double topWallY) {
    double prevY = this.yIntegrator.getPreviousPosition();

    if (checkContactHorizontalWall(prevY, radius, topWallY)) {
      prevTopWallContact = true;
    }
  }

  public void checkPrevStepContactBase(double baseY) {
    double prevY = this.yIntegrator.getPreviousPosition();

    if (checkContactHorizontalWall(prevY, radius, baseY)) {
      prevBaseContact = true;
    }
  }

  public void checkPrevStepContactLeftVertex(double leftVertexX, double leftVertexY) {
    double prevX = this.xIntegrator.getPreviousPosition();
    double prevY = this.yIntegrator.getPreviousPosition();

    if (checkContactObstacle(prevX, prevY, radius, leftVertexX, leftVertexY)) {
      prevLeftVertexContact = true;
    }
  }

  public void checkPrevStepContactRightVertex(double rightVertexX, double rightVertexY) {
    double prevX = this.xIntegrator.getPreviousPosition();
    double prevY = this.yIntegrator.getPreviousPosition();

    if (checkContactObstacle(prevX, prevY, radius, rightVertexX, rightVertexY)){
      prevRightVertexContact = true;
    }
  }

  public double getRadius() {
    throw new NotImplementedException();
  }

  public double getMass() {
    throw new NotImplementedException();
  }

  public void evaluateNextForces() {
    xIntegrator.evaluateForce((nextX, predVx) -> 0);
    yIntegrator.evaluateForce((nextY, predVy) -> 0);
  }

  public void advanceStep() {
    xIntegrator.advanceStep();
    yIntegrator.advanceStep();

    nextContacts.clear();

    nextLeftWallContact    = false;
    nextRightWallContact   = false;
    nextTopWallContact     = false;

    nextLeftVertexContact  = false;
    nextBaseContact        = false;
    nextRightVertexContact = false;
  }

  private static double[] calculateNormalVersor(double x, double y, double oX, double oY) {
    double rDiff = Math.abs(Math.sqrt(oX*oX + oY*oY) - Math.sqrt(x*x + y*y));
    double enx = (oX-x)/rDiff;
    double eny = (oY-y)/rDiff;
    double[] out = {enx, eny};

    return out;
  }

  private static double[] calculateTangentialVersor(double x, double y, double oX, double oY) {
    double[] normalVersor = calculateNormalVersor(x,y,oX,oY);
    double[] out = {-normalVersor[1], normalVersor[0]};

    return out;
  }


  private static double sign(double value) {
    return value < 0 ? -1 : value == 0 ? 0 : 1;
  }

  /*
  TODO tangential force is static (probably because of how integrators work)
    but using t1_2 would need to have a way to keep in memory previous v_rel_t*DT to sum up
    any way around this?
  private double sumRelativeVelocities(double newValue) {
    sumRelVel += newValue;
    return sumRelVel;
  }
  */

  private static double calculateTangentialForce(double x, double y, double r, double[] v, double oX, double oY, double oR, double[] oV, double MU, double KT) {
    double[] tangentialVersor = calculateTangentialVersor(x,y,oX,oY);
    double[] relativeVelocity = {v[0]-oV[0], v[1] -oV[1]};
    //dot product of v_rel on t
    double dotProduct = relativeVelocity[0] * tangentialVersor[0] + relativeVelocity[1] * tangentialVersor[1];
    //magnitude of t
    double magT = Math.sqrt(tangentialVersor[0] * tangentialVersor[0] + tangentialVersor[1] * tangentialVersor[1]);
    //magnitude of the projection of v_rel on t
    double v_rel_t = dotProduct / magT;

    double normalForce = calculateNormalForce(x,y,v,oX,oY,oV);
    double t1_1 = -MU * Math.abs(normalForce) * sign(v_rel_t);
    //TODO WHY STATIC???
    //sumRelativeVelocities needs a way to add up these velocities throughout the duration of the overlap
    //that value cannot be shared between all instances
    //double t1_2 = -KT * sumRelativeVelocities(DT*v_rel_t);

    //WORKAROUND: dijieron en clase que si t1_2 suponia problemas, podia usarse t_3
    double[] s = {r + oR - Math.abs(oX - x), r + oR - Math.abs(oY - y)};
    double dotProductSonT = s[0] * tangentialVersor[0] + s[1] * tangentialVersor[1];
    double t3 = -KT * (dotProductSonT / magT) * v_rel_t;

    return Math.min(t1_1, t3);// t1_2);
  }

  private static double calculateTangentialForceObstacle(double x, double y, double r, double[] v, double obsX, double obsY, double MU, double KT) {
    double[] obsV = {0, 0};
    return calculateTangentialForce(x, y, r, v, obsX, obsY, 0, obsV, MU, KT);
  }

  private static double calculateTangentialForceHorizontalWall(double x, double y, double MU, double KT) {
    throw new NotImplementedException();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Particle)) {
      return false;
    }
    Particle particle = (Particle) o;
    return id == particle.id;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(id);
  }

  public double getPosition() {
    throw new NotImplementedException();
  }

  public double getPositionX() {
    return this.xIntegrator.getPosition();
  }
  public double getPositionY() {
    return this.yIntegrator.getPosition();
  }

  public double getVelocity() {
    throw new NotImplementedException();
  }

  public double getVelocityX() {
    return this.xIntegrator.getVelocity();
  }
  public double getVelocityY() {
    return this.yIntegrator.getVelocity();
  }


  private double calculateNormalForce(double x, double y) {

  }

  private double calculateNormalForce(double x, double y, double[] v, double otherX, double otherY, double[] otherV) {
    double[] normalVersor = calculateNormalVersor(x, y, otherX, otherY);
    double[] relativeVelocity = {v[0]-otherV[0], v[1] -otherV[1]};
    //dot product of relativeVelocity on normal direction
    double dotProduct = relativeVelocity[0] * normalVersor[0] + relativeVelocity[1] * normalVersor[1];
    //magnitude of normal
    double magN = Math.sqrt(normalVersor[0] * normalVersor[0] + normalVersor[1] * normalVersor[1]);
    //magnitude of the projection of relativeVelocity on normal direction
    double relativeSpeed = dotProduct / magN;


    double overlap = 2 * radius - Math.abs(Math.sqrt(x*x + y*y) - Math.sqrt(otherX*otherX + otherY*otherY));

    if (overlap < 0)
      return 0;

    return -K * overlap - GAMMA * relativeSpeed;
  }

  private double calculateNormalForceVerticalWall(double x, double y, double[] v, double wallX) {
    double[] normalVersor = calculateNormalVersor(x, y, wallX, y);
    double[] relativeVelocity = {v[0] - 0, v[1] - 0};
    //dot product of relativeVelocity on normal direction
    double dotProduct = relativeVelocity[0] * normalVersor[0] + relativeVelocity[1] * normalVersor[1];
    //magnitude of normal
    double magN = Math.sqrt(normalVersor[0] * normalVersor[0] + normalVersor[1] * normalVersor[1]);
    //magnitude of the projection of relativeVelocity on normal direction
    double relativeSpeed = dotProduct / magN;

    double overlap = 0;
    if (x > wallX && x < wallX + radius)
      overlap = wallX - (x - radius);
    else if (x < wallX && x > wallX - radius)
      overlap = -wallX + x + radius;

    return -K * overlap - GAMMA * relativeSpeed;
  }

  private double calculateNormalForceHorizontalWall(double x, double y, double[] v, double wallY) {
    double[] normalVersor = calculateNormalVersor(x, y, x, wallY);
    double[] relativeVelocity = {v[0] - 0, v[1] - 0};
    //dot product of relativeVelocity on normal direction
    double dotProduct = relativeVelocity[0] * normalVersor[0] + relativeVelocity[1] * normalVersor[1];
    //magnitude of normal
    double magN = Math.sqrt(normalVersor[0] * normalVersor[0] + normalVersor[1] * normalVersor[1]);
    //magnitude of the projection of relativeVelocity on normal direction
    double relativeSpeed = dotProduct / magN;

    double overlap = 0;
    if (y > wallY && y < wallY + radius)
      overlap = wallY - (y - radius);
    else if (y < wallY && y > wallY - radius)
      overlap = -wallY + y + radius;

    return -K * overlap - GAMMA * relativeSpeed;
  }

  private double calculateNormalForceObstacle(double x, double y, double[] v, double obstacleX, double obstacleY) {
    double[] obstacleV = {0, 0};

    return calculateNormalForce(x, y, v, obstacleX, obstacleY, obstacleV);
  }

  private double calculatePrevNormalForce(Particle p) {

  }

  private double calculatePrevNormalForceVerticalWall(double wallX) {

  }

  private double calculatePrevNormalForceHorizontalWall(double wallY) {

  }

  private double calculatePrevNormalForceObstacle(double obstacleX, double obstacleY) {

  }

  private double calculateNextNormalForce(Particle p) {

  }

  private double calculateNextNormalForceVerticalWall(double wallX) {

  }

  private double calculateNextNormalForceHorizontalWall(double wallY) {

  }

  private double calculateNextNormalForceObstacle(double obstacleX, double obstacleY) {

  }

  private double proyectX(double normalForce, double tangencialForce) {

  }

  private double proyectY(double normalForce, double tangencialForce) {

  }

  private double calculatePrevXForce() {

  }

  private double calculatePrevYForce() {

  }

  private double calculateNextXForce() {

  }

  private double calculateNextYForce() {

  }

  public boolean needsReinsertion(double lowerBound) {
    return yIntegrator.getPosition() < lowerBound - L / 10;
  }

  public void reinsert() {
    xIntegrator.reinsert(Math.random() * W, 0, (x, vx) -> 0 );
    xIntegrator.reinsert(Math.random() * 30 + 40, 0, (y, vy) -> 0 );
  }
}



































