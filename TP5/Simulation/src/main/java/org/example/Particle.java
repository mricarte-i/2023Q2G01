package org.example;

import org.example.integrators.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

public class Particle {
  private final int id;
  private final double deltaT;
  private final BeemanIntegrator xIntegrator;
  private final BeemanIntegrator yIntegrator;
  private final double mass, radius;
  private List<Particle> prevContacts, nextContacts;
  private Set<Integer> nextContactsIds;

  private boolean prevLeftWallContact, prevRightWallContact, prevTopWallContact;
  private boolean prevLeftVertexContact, prevBaseContact, prevRightVertexContact;
  private boolean prevLeftBaseContact, prevRightBaseContact;
  private boolean nextLeftWallContact, nextRightWallContact, nextTopWallContact;
  private boolean nextLeftVertexContact, nextBaseContact, nextRightVertexContact;
  private boolean nextLeftBaseContact, nextRightBaseContact;

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
    this.deltaT = dT;
  }

  public Particle(int id, double radius, double mass, double x, double y, double dT, double vx, double vy, Integer totalParticles) {
    this(id, radius, mass, x, y, dT, vx, vy);
    if (totalParticles != null) {
      this.prevContacts = new ArrayList<>(totalParticles);
      this.nextContacts = new ArrayList<>(totalParticles);
      this.nextContactsIds = new HashSet<>(totalParticles, 1.0f);
    } else {
      this.prevContacts = new LinkedList<>();
      this.nextContacts = new LinkedList<>();
      this.nextContactsIds = new HashSet<>();
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
    prevRightVertexContact = false;

    prevLeftBaseContact    = false;
    prevRightBaseContact   = false;
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

    if (checkContactParticle(nextX, nextY, radius, nextOtherX, nextOtherY, p.radius)) {
      nextContacts.add(p);
      nextContactsIds.add(p.id);
    }
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

  public void checkNextStepContactBase(double baseY, double leftBoundX, double rightBoundX) {
    checkNextStepContactLeftBase(baseY, leftBoundX);
    checkNextStepContactRightBase(baseY, rightBoundX);
  }

  private void checkNextStepContactLeftBase(double baseY, double vertexX) {
    double nextX = this.xIntegrator.getNextPosition();
    double nextY = this.yIntegrator.getNextPosition();

    checkNextStepContactLeftVertex(vertexX, baseY);

    if (!nextLeftVertexContact && nextX <= vertexX && checkContactHorizontalWall(nextY, radius, baseY)) {
      nextLeftBaseContact = true;
    }
  }

  private void checkNextStepContactRightBase(double baseY, double vertexX) {
    double nextX = this.xIntegrator.getNextPosition();
    double nextY = this.yIntegrator.getNextPosition();

    checkNextStepContactRightVertex(vertexX, baseY);

    if (!nextRightVertexContact && nextX >= vertexX && checkContactHorizontalWall(nextY, radius, baseY)) {
      nextRightBaseContact = true;
    }
  }

  private void checkNextStepContactLeftVertex(double leftVertexX, double leftVertexY) {
    double nextX = this.xIntegrator.getNextPosition();
    double nextY = this.yIntegrator.getNextPosition();

    if (checkContactObstacle(nextX, nextY, radius, leftVertexX, leftVertexY)) {
      nextLeftVertexContact = true;
    }
  }

  private void checkNextStepContactRightVertex(double rightVertexX, double rightVertexY) {
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

  public void checkPrevStepContactBase(double baseY, double leftBoundX, double rightBoundX) {
    checkPrevStepContactLeftBase(baseY, leftBoundX);
    checkPrevStepContactRightBase(baseY, rightBoundX);
  }

  private void checkPrevStepContactLeftBase(double baseY, double vertexX) {
    double prevX = this.xIntegrator.getPreviousPosition();
    double prevY = this.yIntegrator.getPreviousPosition();

    checkPrevStepContactLeftVertex(vertexX, baseY);

    if (!nextLeftVertexContact && prevX <= vertexX && checkContactHorizontalWall(prevY, radius, baseY)) {
      nextLeftBaseContact = true;
    }
  }

  private void checkPrevStepContactRightBase(double baseY, double vertexX) {
    double prevX = this.xIntegrator.getPreviousPosition();
    double prevY = this.yIntegrator.getPreviousPosition();

    checkPrevStepContactRightVertex(vertexX, baseY);

    if (!prevRightVertexContact && prevX >= vertexX && checkContactHorizontalWall(prevY, radius, baseY)) {
      prevRightBaseContact = true;
    }
  }

  private void checkPrevStepContactLeftVertex(double leftVertexX, double leftVertexY) {
    double prevX = this.xIntegrator.getPreviousPosition();
    double prevY = this.yIntegrator.getPreviousPosition();

    if (checkContactObstacle(prevX, prevY, radius, leftVertexX, leftVertexY)) {
      prevLeftVertexContact = true;
    }
  }

  private void checkPrevStepContactRightVertex(double rightVertexX, double rightVertexY) {
    double prevX = this.xIntegrator.getPreviousPosition();
    double prevY = this.yIntegrator.getPreviousPosition();

    if (checkContactObstacle(prevX, prevY, radius, rightVertexX, rightVertexY)){
      prevRightVertexContact = true;
    }
  }

  public double getRadius() {
    return radius;
  }

  public double getMass() {
    return mass;
  }

  public void evaluateNextForces() {
    for (Particle particle : prevContacts) {
      if (!nextContactsIds.contains(particle.id)) {
        // TODO: clear memory forces for id
      }
    }

    xIntegrator.evaluateForce((nextX, predVx) -> 0);
    yIntegrator.evaluateForce((nextY, predVy) -> 0);

    prevLeftWallContact    = nextLeftWallContact;
    prevRightWallContact   = nextRightWallContact;
    prevTopWallContact     = nextTopWallContact;

    prevLeftVertexContact  = nextLeftVertexContact;
    prevRightVertexContact = nextRightVertexContact;

    prevLeftBaseContact    = nextLeftBaseContact;
    prevRightBaseContact   = nextRightBaseContact;

    nextContactsIds.clear();
  }

  public void advanceStep() {
    xIntegrator.advanceStep();
    yIntegrator.advanceStep();

    prevContacts.clear();
    prevContacts.addAll(nextContacts);

    nextContacts.clear();

    nextLeftWallContact    = false;
    nextRightWallContact   = false;
    nextTopWallContact     = false;

    nextLeftVertexContact  = false;
    nextBaseContact        = false;
    nextRightVertexContact = false;
  }

  private static double[] calculateNormalVersor(double x, double y, double oX, double oY) {
    double rDiff = Math.sqrt(Math.pow(oX-x, 2) + Math.pow(oY-y, 2));
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

  /*
  TODO tangential force is static (probably because of how integrators work)
    but using t1_2 would need to have a way to keep in memory previous v_rel_t*DT to sum up
    any way around this?
  private double sumRelativeVelocities(double newValue) {
    sumRelVel += newValue;
    return sumRelVel;
  }
  */

  private static double calculateT1_1(double normalForce, double relVt, double MU){
    double signRelVt = Math.signum(relVt);
    //double normalForce = calculateNormalForce(x,y,v,oX,oY,oV);
    return -MU * Math.abs(normalForce) * signRelVt;
  }

  private static double calculateT1_2(double relVt, double deltaT, double KT, List<Double> forceMemory) {
    double relVtMag = Math.abs(relVt);

    double currS = deltaT * relVtMag;
    forceMemory.add(currS);

    double sumS = 0;
    for (Double force : forceMemory){
      sumS += force;
    }

    return -KT * sumS;
  }

  private static double calculateT3(double r, double oR, double x, double oX, double y, double oY, double relVt, double KT){
    double relVtMag = Math.abs(relVt);

    double[] relativePos = {x-oX, y-oY};
    double s = r + oR - Math.sqrt(Math.pow(relativePos[0], 2) + Math.pow(relativePos[1], 2));

      return -KT * s / relVtMag;
  }

  private static double[] calculateTangentialForce(double normalForce, double x, double y, double r, double[] v, double oX, double oY, double oR, double[] oV,
                                                 double MU, double KT, double deltaT, List<Double> forceMemory) {
    double[] tangentialVersor = calculateTangentialVersor(x,y,oX,oY);
    double[] relV = {v[0]-oV[0], v[1]-oV[1]};

    //dot product of v_rel on t
    double relVt = relV[0] * tangentialVersor[0] + relV[1] * tangentialVersor[1];

    double t1_1 = calculateT1_1(relVt, normalForce, MU);

    //TODO WHY STATIC???
    //sumRelativeVelocities needs a way to add up these velocities throughout the duration of the overlap
    //that value cannot be shared between all instances
    //double t1_2 = -KT * sumRelativeVelocities(DT*v_rel_t);
    double t1_2 = calculateT1_2(relVt, deltaT, KT, forceMemory);

    //WORKAROUND: dijieron en clase que si t1_2 suponia problemas, podia usarse t_3
    //double t3 = calculateT3(r, oR, x, oX, y, oY, relVt, KT);

    return new double[]{Math.min(t1_1, t1_2), tangentialVersor[0], tangentialVersor[1]};// t3);
  }

  private static double[] calculateTangentialForceObstacle(double normalForce, double x, double y, double r, double[] v,
                                                         double obsX, double obsY, double[] obsV, double MU, double KT, double deltaT, List<Double> forceMemory) {
    return calculateTangentialForce(normalForce, x, y, r, v, obsX, obsY, 0, obsV, MU, KT, deltaT, forceMemory);
  }

  private static double[] calculateTangentialForceHorizontalWall(double normalForce, double x, double y, double r, double[] v,
                                                               double wallY, double wallVy, double MU, double KT, double deltaT, List<Double> forceMemory) {
    double[] obsV = {0, wallVy};
    return calculateTangentialForce(normalForce, x, y, r, v, x, wallY, 0, obsV, MU, KT, deltaT, forceMemory);
  }

  private static double[] calculateTangentialForceVerticalWall(double normalForce, double x, double y, double r, double[] v,
                                                               double wallX, double wallVx, double MU, double KT, double deltaT, List<Double> forceMemory) {
    double[] obsV = {wallVx, 0};
    return calculateTangentialForce(normalForce, x, y, r, v, wallX, y, 0, obsV, MU, KT, deltaT, forceMemory);
  }

  private List<Double> nextTopWallMemory = new LinkedList<>();

  private double[] calculateNextTangentialForceTopWall(double normalForce, double wallY, double MU, double KT) {
    double x = xIntegrator.getNextPosition();
    double y = yIntegrator.getNextPosition();
    double[] v = {xIntegrator.getPredictedVelocity(), yIntegrator.getPredictedVelocity()};

    return calculateTangentialForceHorizontalWall(normalForce, x, y, radius, v, wallY, 0, MU, KT, deltaT, nextTopWallMemory);
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

  private double[] calculateNormalForce(double x, double y, double[] v, double otherX, double otherY, double[] otherV) {
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
      return new double[]{0, normalVersor[0], normalVersor[1]};

    return new double[]{-K * overlap - GAMMA * relativeSpeed, normalVersor[0], normalVersor[1]};
  }

  private double[] calculateNormalForceVerticalWall(double x, double y, double[] v, double wallX) {
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

    return new double[]{-K * overlap - GAMMA * relativeSpeed, normalVersor[0], normalVersor[1]};
  }

  private double[] calculateNormalForceHorizontalWall(double x, double y, double[] v, double wallY) {
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

    return new double[]{-K * overlap - GAMMA * relativeSpeed, normalVersor[0], normalVersor[1]};
  }

  private double[] calculateNormalForceObstacle(double x, double y, double[] v, double obstacleX, double obstacleY) {
    double[] obstacleV = {0, 0};

    return calculateNormalForce(x, y, v, obstacleX, obstacleY, obstacleV);
  }

  private double[] calculatePrevNormalForce(Particle p) {

  }

  private double[] calculatePrevNormalForceVerticalWall(double wallX) {

  }

  private double[] calculatePrevNormalForceHorizontalWall(double wallY) {

  }

  private double[] calculatePrevNormalForceObstacle(double obstacleX, double obstacleY) {

  }

  private double[] calculateNextNormalForce(Particle p) {

  }

  private double[] calculateNextNormalForceVerticalWall(double wallX) {

  }

  private double[] calculateNextNormalForceHorizontalWall(double wallY) {

  }

  private double[] calculateNextNormalForceObstacle(double obstacleX, double obstacleY) {

  }

  private double proyectX(double normalForce, double tangencialForce, double[] normalVersor) {
    return normalForce * normalVersor[0] - tangencialForce * normalVersor[1];
  }

  private double proyectY(double normalForce, double tangencialForce, double[] normalVersor) {
    return normalForce * normalVersor[1] + tangencialForce * normalVersor[0];

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
    return yIntegrator.getPosition() < lowerBound - L / 10.0;
  }

  public void reinsert(double newX, double newY) {
    xIntegrator.reinsert(newX, 0, (x, vx) -> 0 );
    xIntegrator.reinsert(newY, 0, (y, vy) -> 0 );
  }
}



































