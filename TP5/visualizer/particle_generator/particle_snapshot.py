from random_generator import uniform_func_supplier
from dataclasses import dataclass
from typing import Callable
import numpy as np
import math

@dataclass
class ParticleSnapshot:
    radius: float
    x: float
    y: float
    vx: float = 0
    vy: float = 0
    mass: float = 1

    def distance_to(self, other: 'ParticleSnapshot') -> float:
        return math.sqrt((self.x - other.x)**2 + (self.y - other.y)**2)

    def static_info(self) -> str:
        return "{} {}".format(self.mass, self.radius)

    def dynamic_info(self) -> str:
        return "{} {} {} {}".format(self.x, self.y, self.vx, self.vy)
    
def overlapping_particle_snapshots(new_particle: ParticleSnapshot, particles: list[ParticleSnapshot]) -> bool:
    for particle in particles:
        dist_particles = new_particle.distance_to(particle)
        radii_sum = new_particle.radius + particle.radius

        if dist_particles < radii_sum:
            return True
        
    return False

def generate_particle_snapshots(N: int, x_bounds: tuple[float, float], y_bounds: tuple[float, float], radii_supplier_f: Callable[[], float], random_state : np.random.Generator) -> list[ParticleSnapshot]:
    particle_snapshots = []

    for _ in range(N):
        radius = radii_supplier_f()
        overlapping = True

        while overlapping:
            x_supplier_f = uniform_func_supplier(x_bounds[0]+radius, x_bounds[1]-radius, random_state)
            y_supplier_f = uniform_func_supplier(y_bounds[0]+radius, y_bounds[1]-radius, random_state)
            x = x_supplier_f()
            y = y_supplier_f()

            particle_snapshot = ParticleSnapshot(radius, x, y)

            if not overlapping_particle_snapshots(particle_snapshot, particle_snapshots):
                overlapping = False
        
        particle_snapshots.append(particle_snapshot)

    return particle_snapshots