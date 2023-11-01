package org.example;

import org.example.integrators.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;
import java.util.stream.Collectors;

public class Particle {
  private final int id;
  private final double deltaT;
  private final BeemanIntegrator xIntegrator;
  private final BeemanIntegrator yIntegrator;
  private final double mass, radius;
  private boolean hasExited;

  private List<Particle> prevContacts, nextContacts;
  private Set<Integer> nextContactsIds;

  private final HashMap<Integer, List<Double>> nextParticlesContact;
  private final HashMap<Integer, List<Double>> prevParticlesContact;

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

  private Particle(int id, double radius, double mass, double gravity, double x, double y, double dT, double vx, double vy) {
    this.id = id;
    this.xIntegrator = new BeemanIntegrator(dT, x, vx, mass, (pos, vel) -> 0);
    this.yIntegrator = new BeemanIntegrator(dT, y, vy, mass, (pos, vel) -> mass * gravity);
    this.radius = radius;
    this.mass = mass;
    this.deltaT = dT;
    this.nextParticlesContact = new HashMap<>();
    this.prevParticlesContact = new HashMap<>();
    this.hasExited = false;
  }

  public Particle(int id, double radius, double mass, double gravity, double x, double y, double dT, double vx, double vy, Integer totalParticles) {
    this(id, radius, mass, gravity, x, y, dT, vx, vy);
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

  public void initialize(double gravity) {
    xIntegrator.initPrevForce((prevX, prevVx) -> 0);
    yIntegrator.initPrevForce((prevY, prevVy) -> mass * gravity);

    prevContacts.clear();

    prevLeftWallContact    = false;
    prevRightWallContact   = false;
    prevTopWallContact     = false;

    prevLeftVertexContact  = false;
    prevRightVertexContact = false;

    prevLeftBaseContact    = false;
    prevRightBaseContact   = false;

    prevParticlesContact.clear();
    nextParticlesContact.clear();
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

  public boolean hasExited() {
    return hasExited;
  }

  public void setHasExited(boolean hasExited) {
    this.hasExited = hasExited;
  }

  public void evaluateNextForces(double topWallY, double leftWallX, double rightWallX, double baseY, double leftVertexX, double rightVertexX, double baseVelY,
                                 double gravity, double MU, double KT) {
    for (Particle particle : prevContacts) {
      if (!nextContactsIds.contains(particle.id)) {
        nextParticlesContact.remove(particle.id);
      }
    }
    if(!nextLeftBaseContact) nextLeftBaseMemory.clear();
    if(!nextRightBaseContact) nextRightBaseMemory.clear();
    if(!nextLeftVertexContact) nextLeftVertexMemory.clear();
    if(!nextRightVertexContact) nextRightVertexMemory.clear();
    if(!nextLeftWallContact) nextLeftWallMemory.clear();
    if(!nextRightWallContact) nextRightWallMemory.clear();
    if(!nextTopWallContact) nextTopWallMemory.clear();

    double xForce = calculateNextXForce(topWallY, leftWallX, rightWallX, baseY, leftVertexX, rightVertexX, baseVelY, gravity, MU, KT);
    double yForce = calculateNextYForce(topWallY, leftWallX, rightWallX, baseY, leftVertexX, rightVertexX, baseVelY, gravity, MU, KT);

    xIntegrator.evaluateForce((nextX, predVx) -> xForce);
    yIntegrator.evaluateForce((nextY, predVy) -> yForce);
  }

  public void advanceStep() {
    xIntegrator.advanceStep();
    yIntegrator.advanceStep();

    prevLeftWallContact    = nextLeftWallContact;
    prevRightWallContact   = nextRightWallContact;
    prevTopWallContact     = nextTopWallContact;

    prevLeftVertexContact  = nextLeftVertexContact;
    prevRightVertexContact = nextRightVertexContact;

    prevLeftBaseContact    = nextLeftBaseContact;
    prevRightBaseContact   = nextRightBaseContact;

    nextContactsIds.clear();

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

  private List<Double> nextLeftBaseMemory = new LinkedList<>();

  private double[] calculateNextTangentialForceLeftBase(double normalForce, double wallY, double wallVy, double MU, double KT) {
    double x = xIntegrator.getNextPosition();
    double y = yIntegrator.getNextPosition();
    double[] v = {xIntegrator.getPredictedVelocity(), yIntegrator.getPredictedVelocity()};

    return calculateTangentialForceHorizontalWall(normalForce, x, y, radius, v, wallY, wallVy, MU, KT, deltaT, nextLeftBaseMemory);
  }

  private List<Double> nextRightBaseMemory = new LinkedList<>();

  private double[] calculateNextTangentialForceRightBase(double normalForce, double wallY, double wallVy, double MU, double KT) {
    double x = xIntegrator.getNextPosition();
    double y = yIntegrator.getNextPosition();
    double[] v = {xIntegrator.getPredictedVelocity(), yIntegrator.getPredictedVelocity()};

    return calculateTangentialForceHorizontalWall(normalForce, x, y, radius, v, wallY, wallVy, MU, KT, deltaT, nextRightBaseMemory);
  }

  private List<Double> nextLeftWallMemory = new LinkedList<>();

  private double[] calculateNextTangentialForceLeftWall(double normalForce, double wallX, double MU, double KT) {
    double x = xIntegrator.getNextPosition();
    double y = yIntegrator.getNextPosition();
    double[] v = {xIntegrator.getPredictedVelocity(), yIntegrator.getPredictedVelocity()};

    return calculateTangentialForceVerticalWall(normalForce, x, y, radius, v, wallX, 0, MU, KT, deltaT, nextLeftWallMemory);
  }

  private List<Double> nextRightWallMemory = new LinkedList<>();

  private double[] calculateNextTangentialForceRightWall(double normalForce, double wallX, double MU, double KT) {
    double x = xIntegrator.getNextPosition();
    double y = yIntegrator.getNextPosition();
    double[] v = {xIntegrator.getPredictedVelocity(), yIntegrator.getPredictedVelocity()};

    return calculateTangentialForceVerticalWall(normalForce, x, y, radius, v, wallX, 0, MU, KT, deltaT, nextRightWallMemory);
  }

  private double[] calculateNextTangentialForce(double normalForce, Particle p, double MU, double KT) {
    double x = xIntegrator.getNextPosition();
    double y = yIntegrator.getNextPosition();
    double[] v = {xIntegrator.getPredictedVelocity(), yIntegrator.getPredictedVelocity()};

    double oX = p.xIntegrator.getNextPosition();
    double oY = p.yIntegrator.getNextPosition();
    double[] oV = {p.xIntegrator.getPredictedVelocity(), yIntegrator.getPredictedVelocity()};

    if(!this.nextParticlesContact.containsKey(p.id)){
      this.nextParticlesContact.put(p.id, new LinkedList<>());
    }

    return calculateTangentialForce(normalForce, x, y, radius, v, oX, oY, p.getRadius(), oV, MU, KT, deltaT, nextParticlesContact.get(p.id));
  }

  private double[] calculateNextTangentialForceObstacle(double normalForce, double obsX, double obsY, double obsVY, double MU, double KT, List<Double> forceMemory) {
    double x = xIntegrator.getNextPosition();
    double y = yIntegrator.getNextPosition();
    double[] v = {xIntegrator.getPredictedVelocity(), yIntegrator.getPredictedVelocity()};

    double[] obsV = {0, obsVY};

    return calculateTangentialForceObstacle(normalForce, x, y, radius, v, obsX, obsY, obsV, MU, KT, deltaT, forceMemory);
  }

  private final List<Double> nextRightVertexMemory = new LinkedList<>();

  private double[] calculateNextTangentialForceRightVertex(double normalForce, double obsX, double obsY, double obsVY, double MU, double KT) {
    return calculateNextTangentialForceObstacle(normalForce, obsX, obsY, obsVY, MU, KT, nextRightVertexMemory);
  }

  private final List<Double> nextLeftVertexMemory = new LinkedList<>();

  private double[] calculateNextTangentialForceLeftVertex(double normalForce, double obsX, double obsY, double obsVY, double MU, double KT) {
    return calculateNextTangentialForceObstacle(normalForce, obsX, obsY, obsVY, MU, KT, nextLeftVertexMemory);
  }

  private double[] calculatePrevTangentialForce(double normalForce, Particle p, double MU, double KT) {
    double x = xIntegrator.getPreviousPosition();
    double y = yIntegrator.getPreviousPosition();
    double[] v = {xIntegrator.getPreviousVelocity(), yIntegrator.getPreviousVelocity()};

    double oX = p.xIntegrator.getPreviousPosition();
    double oY = p.yIntegrator.getPreviousPosition();
    double[] oV = {p.xIntegrator.getPreviousVelocity(), yIntegrator.getPreviousVelocity()};

    /* TODO PrevParticlesContact exists for memory, not for this,
     * Uhm, actually, prevForces don't go further than 1 instant, so no memory is necessary
     * instead, we pass a new list and call it a day.
    if(!this.prevParticlesContact.containsKey(p.id)){
      this.prevParticlesContact.put(p.id, new LinkedList<>());
    }
     */

    return calculateTangentialForce(normalForce, x, y, radius, v, oX, oY, p.getRadius(), oV, MU, KT, deltaT, new LinkedList<>()); //prevParticlesContact.get(p.id));
  }


  private double[] calculatePrevTangentialForceTopWall(double normalForce, double wallY, double MU, double KT) {
    double x = xIntegrator.getPreviousPosition();
    double y = yIntegrator.getPreviousPosition();
    double[] v = {xIntegrator.getPreviousVelocity(), yIntegrator.getPreviousVelocity()};

    return calculateTangentialForceHorizontalWall(normalForce, x, y, radius, v, wallY, 0, MU, KT, deltaT, new LinkedList<>());
  }


  private double[] calculatePrevTangentialForceLeftBase(double normalForce, double wallY, double wallVy, double MU, double KT) {
    double x = xIntegrator.getPreviousPosition();
    double y = yIntegrator.getPreviousPosition();
    double[] v = {xIntegrator.getPreviousVelocity(), yIntegrator.getPreviousVelocity()};

    return calculateTangentialForceHorizontalWall(normalForce, x, y, radius, v, wallY, wallVy, MU, KT, deltaT, new LinkedList<>());
  }


  private double[] calculatePrevTangentialForceRightBase(double normalForce, double wallY, double wallVy, double MU, double KT) {
    double x = xIntegrator.getPreviousPosition();
    double y = yIntegrator.getPreviousPosition();
    double[] v = {xIntegrator.getPreviousVelocity(), yIntegrator.getPreviousVelocity()};

    return calculateTangentialForceHorizontalWall(normalForce, x, y, radius, v, wallY, wallVy, MU, KT, deltaT, new LinkedList<>());
  }


  private double[] calculatePrevTangentialForceLeftWall(double normalForce, double wallX, double MU, double KT) {
    double x = xIntegrator.getPreviousPosition();
    double y = yIntegrator.getPreviousPosition();
    double[] v = {xIntegrator.getPreviousVelocity(), yIntegrator.getPreviousVelocity()};

    return calculateTangentialForceVerticalWall(normalForce, x, y, radius, v, wallX, 0, MU, KT, deltaT, new LinkedList<>());
  }


  private double[] calculatePrevTangentialForceRightWall(double normalForce, double wallX, double MU, double KT) {
    double x = xIntegrator.getPreviousPosition();
    double y = yIntegrator.getPreviousPosition();
    double[] v = {xIntegrator.getPreviousVelocity(), yIntegrator.getPreviousVelocity()};

    return calculateTangentialForceVerticalWall(normalForce, x, y, radius, v, wallX, 0, MU, KT, deltaT, new LinkedList<>());
  }

  private double[] calculatePrevTangentialForceObstacle(double normalForce, double obsX, double obsY, double obsVY, double MU, double KT, List<Double> forceMemory) {
    double x = xIntegrator.getPreviousPosition();
    double y = yIntegrator.getPreviousPosition();
    double[] v = {xIntegrator.getPreviousVelocity(), yIntegrator.getPreviousVelocity()};

    double[] obsV = {0, obsVY};

    return calculateTangentialForceObstacle(normalForce, x, y, radius, v, obsX, obsY, obsV, MU, KT, deltaT, forceMemory);
  }

  private double[] calculatePrevTangentialForceRightVertex(double normalForce, double obsX, double obsY, double obsVY, double MU, double KT) {
    return calculatePrevTangentialForceObstacle(normalForce, obsX, obsY, obsVY, MU, KT, new LinkedList<>());
  }


  private double[] calculatePrevTangentialForceLeftVertex(double normalForce, double obsX, double obsY, double obsVY, double MU, double KT) {
    return calculatePrevTangentialForceObstacle(normalForce, obsX, obsY, obsVY, MU, KT, new LinkedList<>());
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

  public double getPositionX() {
    return this.xIntegrator.getPosition();
  }
  public double getPositionY() {
    return this.yIntegrator.getPosition();
  }

  public double getVelocityX() {
    return this.xIntegrator.getVelocity();
  }
  public double getVelocityY() {
    return this.yIntegrator.getVelocity();
  }


  private double calculateNormalForce(double x, double y) {
    //TODO DELETE ?
    return 0.0;
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

  private double[] calculateNormalForceHorizontalWall(double x, double y, double[] v, double wallY, double wallVy) {
    double[] normalVersor = calculateNormalVersor(x, y, x, wallY);
    double[] relativeVelocity = {v[0] - 0, v[1] - wallVy};
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

  private double[] calculateNormalForceObstacle(double x, double y, double[] v, double obstacleX, double obstacleY, double obsVelX, double obsVelY) {
    double[] obstacleV = {obsVelX, obsVelY};

    return calculateNormalForce(x, y, v, obstacleX, obstacleY, obstacleV);
  }

  private double[] calculatePrevNormalForce(Particle p) {
    double x = xIntegrator.getPreviousPosition();
    double y = yIntegrator.getPreviousPosition();
    double[] v = {xIntegrator.getPreviousVelocity(), yIntegrator.getPreviousVelocity()};

    double oX = p.xIntegrator.getPreviousPosition();
    double oY = p.yIntegrator.getPreviousPosition();
    double[] oV = {p.xIntegrator.getPreviousVelocity(), p.yIntegrator.getPreviousVelocity()};

    return calculateNormalForce(x, y, v, oX, oY, oV);
  }

  private double[] calculatePrevNormalForceVerticalWall(double wallX) {
    double x = xIntegrator.getPreviousPosition();
    double y = yIntegrator.getPreviousPosition();
    double[] v = {xIntegrator.getPreviousVelocity(), yIntegrator.getPreviousVelocity()};

    return calculateNormalForceVerticalWall(x, y, v, wallX);
  }

  private double[] calculatePrevNormalForceHorizontalWall(double wallY, double wallVy) {
    double x = xIntegrator.getPreviousPosition();
    double y = yIntegrator.getPreviousPosition();
    double[] v = {xIntegrator.getPreviousVelocity(), yIntegrator.getPreviousVelocity()};

    return calculateNormalForceHorizontalWall(x, y, v, wallY, wallVy);
  }

  private double[] calculatePrevNormalForceObstacle(double obstacleX, double obstacleY, double obsVelX, double obsVelY) {
    double x = xIntegrator.getPreviousPosition();
    double y = yIntegrator.getPreviousPosition();
    double[] v = {xIntegrator.getPreviousVelocity(), yIntegrator.getPreviousVelocity()};

    return calculateNormalForceObstacle(x, y, v, obstacleX, obstacleY, obsVelX, obsVelY);
  }

  private double[] calculateNextNormalForce(Particle p) {
    double x = xIntegrator.getNextPosition();
    double y = yIntegrator.getNextPosition();
    double[] v = {xIntegrator.getPredictedVelocity(), yIntegrator.getPredictedVelocity()};

    double oX = p.xIntegrator.getNextPosition();
    double oY = p.yIntegrator.getNextPosition();
    double[] oV = {p.xIntegrator.getPredictedVelocity(), p.yIntegrator.getPredictedVelocity()};

    return calculateNormalForce(x, y, v, oX, oY, oV);

  }

  private double[] calculateNextNormalForceVerticalWall(double wallX) {
    double x = xIntegrator.getNextPosition();
    double y = yIntegrator.getNextPosition();
    double[] v = {xIntegrator.getPredictedVelocity(), yIntegrator.getPredictedVelocity()};

    return calculateNormalForceVerticalWall(x, y, v, wallX);
  }

  private double[] calculateNextNormalForceHorizontalWall(double wallY, double wallVy) {
    double x = xIntegrator.getNextPosition();
    double y = yIntegrator.getNextPosition();
    double[] v = {xIntegrator.getPredictedVelocity(), yIntegrator.getPredictedVelocity()};

    return calculateNormalForceHorizontalWall(x, y, v, wallY, wallVy);
  }

  private double[] calculateNextNormalForceObstacle(double obstacleX, double obstacleY, double obsVelX, double obsVelY) {
    double x = xIntegrator.getNextPosition();
    double y = yIntegrator.getNextPosition();
    double[] v = {xIntegrator.getPredictedVelocity(), yIntegrator.getPredictedVelocity()};

    return calculateNormalForceObstacle(x, y, v, obstacleX, obstacleY, obsVelX, obsVelY);
  }

  private double proyectX(double normalForce, double tangencialForce, double normalVersorX, double normalVersorY) {
    return normalForce * normalVersorX - tangencialForce * normalVersorY;
  }

  private double proyectY(double normalForce, double tangencialForce, double normalVersorX, double normalVersorY) {
    return normalForce * normalVersorY + tangencialForce * normalVersorX;

  }

  private double calculatePrevXForce(double topWallY, double leftWallX, double rightWallX, double baseY, double leftVertexX, double rightVertexX, double baseVelY,
                                     double gravity, double MU, double KT) {
    double xForce = 0;
    for (Particle p : prevContacts){
      double[] resN = calculatePrevNormalForce(p);
      double[] resT = calculatePrevTangentialForce(resN[0], p, MU, KT);
      xForce += proyectX(resN[0], resT[0], resN[1], resN[2]);
    }

    if (prevTopWallContact) {
      double[] resN = calculatePrevNormalForceHorizontalWall(topWallY, 0);
      double[] resT = calculatePrevTangentialForceTopWall(resN[0], topWallY, MU, KT);
      xForce += proyectX(resN[0], resT[0], resN[1], resN[2]);
    }
    if (prevLeftWallContact) {
      double[] resN = calculatePrevNormalForceVerticalWall(leftWallX);
      double[] resT = calculatePrevTangentialForceLeftWall(resN[0], leftWallX, MU, KT);
      xForce += proyectX(resN[0], resT[0], resN[1], resN[2]);
    }
    if (prevRightWallContact) {
      double[] resN = calculatePrevNormalForceVerticalWall(rightWallX);
      double[] resT = calculatePrevTangentialForceRightWall(resN[0], rightWallX, MU, KT);
      xForce += proyectX(resN[0], resT[0], resN[1], resN[2]);
    }

    if (prevLeftBaseContact) {
      double[] resN = calculatePrevNormalForceHorizontalWall(baseY, baseVelY);
      double[] resT = calculatePrevTangentialForceLeftBase(resN[0], baseY, baseVelY, MU, KT);
      xForce += proyectX(resN[0], resT[0], resN[1], resN[2]);
    }
    if (prevRightBaseContact) {
      double[] resN = calculatePrevNormalForceHorizontalWall(baseY, baseVelY);
      double[] resT = calculatePrevTangentialForceRightBase(resN[0], baseY, baseVelY, MU, KT);
      xForce += proyectX(resN[0], resT[0], resN[1], resN[2]);
    }
    if (prevLeftVertexContact) {
      double[] resN = calculatePrevNormalForceObstacle(leftVertexX, baseY, 0, baseVelY);
      double[] resT = calculatePrevTangentialForceLeftVertex(resN[0], leftVertexX, baseY, baseVelY, MU, KT);
      xForce += proyectX(resN[0], resT[0], resN[1], resN[2]);
    }
    if (prevRightVertexContact) {
      double[] resN = calculatePrevNormalForceObstacle(rightVertexX, baseY, 0, baseVelY);
      double[] resT = calculatePrevTangentialForceRightVertex(resN[0], rightVertexX, baseY, baseVelY, MU, KT);
      xForce += proyectX(resN[0], resT[0], resN[1], resN[2]);
    }

    return xForce;
  }

  private double calculatePrevYForce(double topWallY, double leftWallX, double rightWallX, double baseY, double leftVertexX, double rightVertexX, double baseVelY,
                                     double gravity, double MU, double KT) {
    double yForce = mass * gravity;
    for (Particle p : prevContacts){
      double[] resN = calculatePrevNormalForce(p);
      double[] resT = calculatePrevTangentialForce(resN[0], p, MU, KT);
      yForce += proyectY(resN[0], resT[0], resN[1], resN[2]);
    }

    if (prevTopWallContact) {
      double[] resN = calculatePrevNormalForceHorizontalWall(topWallY, 0);
      double[] resT = calculatePrevTangentialForceTopWall(resN[0], topWallY, MU, KT);
      yForce += proyectY(resN[0], resT[0], resN[1], resN[2]);
    }
    if (prevLeftWallContact) {
      double[] resN = calculatePrevNormalForceVerticalWall(leftWallX);
      double[] resT = calculatePrevTangentialForceLeftWall(resN[0], leftWallX, MU, KT);
      yForce += proyectY(resN[0], resT[0], resN[1], resN[2]);
    }
    if (prevRightWallContact) {
      double[] resN = calculatePrevNormalForceVerticalWall(rightWallX);
      double[] resT = calculatePrevTangentialForceRightWall(resN[0], rightWallX, MU, KT);
      yForce += proyectY(resN[0], resT[0], resN[1], resN[2]);
    }

    if (prevLeftBaseContact) {
      double[] resN = calculatePrevNormalForceHorizontalWall(baseY, baseVelY);
      double[] resT = calculatePrevTangentialForceLeftBase(resN[0], baseY, baseVelY, MU, KT);
      yForce += proyectY(resN[0], resT[0], resN[1], resN[2]);
    }
    if (prevRightBaseContact) {
      double[] resN = calculatePrevNormalForceHorizontalWall(baseY, baseVelY);
      double[] resT = calculatePrevTangentialForceRightBase(resN[0], baseY, baseVelY, MU, KT);
      yForce += proyectY(resN[0], resT[0], resN[1], resN[2]);
    }
    if (prevLeftVertexContact) {
      double[] resN = calculatePrevNormalForceObstacle(leftVertexX, baseY, 0, baseVelY);
      double[] resT = calculatePrevTangentialForceLeftVertex(resN[0], leftVertexX, baseY, baseVelY, MU, KT);
      yForce += proyectY(resN[0], resT[0], resN[1], resN[2]);
    }
    if (prevRightVertexContact) {
      double[] resN = calculatePrevNormalForceObstacle(rightVertexX, baseY, 0, baseVelY);
      double[] resT = calculatePrevTangentialForceRightVertex(resN[0], rightVertexX, baseY, baseVelY, MU, KT);
      yForce += proyectY(resN[0], resT[0], resN[1], resN[2]);
    }

    return yForce;
  }

  private double calculateNextXForce(double topWallY, double leftWallX, double rightWallX, double baseY, double leftVertexX, double rightVertexX, double baseVelY,
                                     double gravity, double MU, double KT)  {
    double xForce = 0;
    for (Particle p : nextContacts){
      double[] resN = calculateNextNormalForce(p);
      double[] resT = calculateNextTangentialForce(resN[0], p, MU, KT);
      xForce += proyectX(resN[0], resT[0], resN[1], resN[2]);
    }

    if (nextTopWallContact) {
      double[] resN = calculateNextNormalForceHorizontalWall(topWallY, 0);
      double[] resT = calculateNextTangentialForceTopWall(resN[0], topWallY, MU, KT);
      xForce += proyectX(resN[0], resT[0], resN[1], resN[2]);
    }
    if (nextLeftWallContact) {
      double[] resN = calculateNextNormalForceVerticalWall(leftWallX);
      double[] resT = calculateNextTangentialForceLeftWall(resN[0], leftWallX, MU, KT);
      xForce += proyectX(resN[0], resT[0], resN[1], resN[2]);
    }
    if (nextRightWallContact) {
      double[] resN = calculateNextNormalForceVerticalWall(rightWallX);
      double[] resT = calculateNextTangentialForceRightWall(resN[0], rightWallX, MU, KT);
      xForce += proyectX(resN[0], resT[0], resN[1], resN[2]);
    }

    if (nextLeftBaseContact) {
      double[] resN = calculateNextNormalForceHorizontalWall(baseY, baseVelY);
      double[] resT = calculateNextTangentialForceLeftBase(resN[0], baseY, baseVelY, MU, KT);
      xForce += proyectX(resN[0], resT[0], resN[1], resN[2]);
    }
    if (nextRightBaseContact) {
      double[] resN = calculateNextNormalForceHorizontalWall(baseY, baseVelY);
      double[] resT = calculateNextTangentialForceRightBase(resN[0], baseY, baseVelY, MU, KT);
      xForce += proyectX(resN[0], resT[0], resN[1], resN[2]);
    }
    if (nextLeftVertexContact) {
      double[] resN = calculateNextNormalForceObstacle(leftVertexX, baseY, 0, baseVelY);
      double[] resT = calculateNextTangentialForceLeftVertex(resN[0], leftVertexX, baseY, baseVelY, MU, KT);
      xForce += proyectX(resN[0], resT[0], resN[1], resN[2]);
    }
    if (nextRightVertexContact) {
      double[] resN = calculateNextNormalForceObstacle(rightVertexX, baseY, 0, baseVelY);
      double[] resT = calculateNextTangentialForceRightVertex(resN[0], rightVertexX, baseY, baseVelY, MU, KT);
      xForce += proyectX(resN[0], resT[0], resN[1], resN[2]);
    }

    return xForce;
  }

  private double calculateNextYForce(double topWallY, double leftWallX, double rightWallX, double baseY, double leftVertexX, double rightVertexX, double baseVelY,
                                     double gravity, double MU, double KT) {
    double yForce = mass * gravity;
    for (Particle p : nextContacts){
      double[] resN = calculateNextNormalForce(p);
      double[] resT = calculateNextTangentialForce(resN[0], p, MU, KT);
      yForce += proyectY(resN[0], resT[0], resN[1], resN[2]);
    }

    if (nextTopWallContact) {
      double[] resN = calculateNextNormalForceHorizontalWall(topWallY, 0);
      double[] resT = calculateNextTangentialForceTopWall(resN[0], topWallY, MU, KT);
      yForce += proyectY(resN[0], resT[0], resN[1], resN[2]);
    }
    if (nextLeftWallContact) {
      double[] resN = calculateNextNormalForceVerticalWall(leftWallX);
      double[] resT = calculateNextTangentialForceLeftWall(resN[0], leftWallX, MU, KT);
      yForce += proyectY(resN[0], resT[0], resN[1], resN[2]);
    }
    if (nextRightWallContact) {
      double[] resN = calculateNextNormalForceVerticalWall(rightWallX);
      double[] resT = calculateNextTangentialForceRightWall(resN[0], rightWallX, MU, KT);
      yForce += proyectY(resN[0], resT[0], resN[1], resN[2]);
    }

    if (nextLeftBaseContact) {
      double[] resN = calculateNextNormalForceHorizontalWall(baseY, baseVelY);
      double[] resT = calculateNextTangentialForceLeftBase(resN[0], baseY, baseVelY, MU, KT);
      yForce += proyectY(resN[0], resT[0], resN[1], resN[2]);
    }
    if (nextRightBaseContact) {
      double[] resN = calculateNextNormalForceHorizontalWall(baseY, baseVelY);
      double[] resT = calculateNextTangentialForceRightBase(resN[0], baseY, baseVelY, MU, KT);
      yForce += proyectY(resN[0], resT[0], resN[1], resN[2]);
    }
    if (nextLeftVertexContact) {
      double[] resN = calculateNextNormalForceObstacle(leftVertexX, baseY, 0, baseVelY);
      double[] resT = calculateNextTangentialForceLeftVertex(resN[0], leftVertexX, baseY, baseVelY, MU, KT);
      yForce += proyectY(resN[0], resT[0], resN[1], resN[2]);
    }
    if (nextRightVertexContact) {
      double[] resN = calculateNextNormalForceObstacle(rightVertexX, baseY, 0, baseVelY);
      double[] resT = calculateNextTangentialForceRightVertex(resN[0], rightVertexX, baseY, baseVelY, MU, KT);
      yForce += proyectY(resN[0], resT[0], resN[1], resN[2]);
    }

    return yForce;
  }

  public boolean needsReinsertion(double lowerBound) {
    return yIntegrator.getPosition() < lowerBound - L / 10.0;
  }

  public void reinsert(double newX, double newY) {
    xIntegrator.reinsert(newX, 0, (x, vx) -> 0 );
    xIntegrator.reinsert(newY, 0, (y, vy) -> 0 );
    hasExited = false;
  }
}



































