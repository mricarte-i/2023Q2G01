from simulation_parser import SimulationInfo
from dataclasses import dataclass

@dataclass
class SimpleParticle:
  id: int
  x: float
  left: int
  right: int

def get_neighbors(siminfo: SimulationInfo):
  neighbors_pairs = []
  particles = []
  init = 0
  for i in range(siminfo.N):
    curr_pos = siminfo.particles[i].position[init][0]
    particles.append(SimpleParticle(i, curr_pos, -1, -1))
  particles.sort(key=lambda p:p.x, reverse=False)

  firstP = None
  prevP = None
  currP = None
  for i in range(siminfo.N):
    prevP = currP
    currP = particles[i]

    if firstP == None:
      firstP = currP
    if prevP != None:
      prevP.right = currP.id
      currP.left = prevP.id

  if firstP != None and firstP != currP:
    firstP.left = currP.id
    currP.right = firstP.id

  return particles

def get_dist(pos1, pos2, boundary):
  dist = abs(pos2 - pos1)
  wrapDist = boundary - dist
  return min(dist, wrapDist)

def get_densities(i: int, simInfo: SimulationInfo, neighborData: list[SimpleParticle]):
  densities_i = []
  instants = simInfo.instants
  j = neighborData[i].left
  k = neighborData[i].right

  for t in range(len(instants)):
    pos_i = simInfo.particles[i].position[t][0]
    pos_j = simInfo.particles[j].position[t][0]
    pos_k = simInfo.particles[k].position[t][0]

    d_ij = get_dist(pos_i, pos_j, simInfo.L)
    d_ik = get_dist(pos_i, pos_k, simInfo.L)

    densities_i.append(1/(d_ij + d_ik))

  return densities_i

def get_all_densities(simInfo: SimulationInfo, neighborData: list[SimpleParticle]):
  densities = []
  instants = simInfo.instants
  for i in range(simInfo.N):
    densities_i = get_densities(i, simInfo, neighborData)
    #ro_{i}(t)
    densities.append(densities_i)

  return densities

def get_all_velocities(simInfo: SimulationInfo):
  velocities = []
  instants = simInfo.instants
  for i in range(simInfo.N):
    velocities_i = []
    for t in range(len(instants)):
      vel_i = simInfo.particles[i].velocity[t][0]
      velocities_i.append(vel_i)
    #v_{i}(t)
    velocities.append(velocities_i)

  return velocities