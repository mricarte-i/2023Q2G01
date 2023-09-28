from dataclasses import dataclass

@dataclass
class StaticInfo:
    N        : int
    L        : float
    masses   : list[float]
    radii    : list[float]

@dataclass
class DynamicInfo:
    instants   : list[float]
    positions  : list[list[tuple[float, float]]]
    velocities : list[list[tuple[float, float]]]

@dataclass
class ParticleInfo:
    mass     : float
    radius   : float
    position : list[tuple[float, float]]
    velocity : list[tuple[float, float]]

@dataclass
class SimulationInfo:
    N         : int
    L         : float
    instants  : list[float]
    particles : list[ParticleInfo]
    

def parse_static_file(static_file : str) -> StaticInfo:
    with open(static_file) as file:
        N       = int(file.readline())
        L       = float(file.readline())
        masses  = []
        radii   = []
        for row in file:
            particle_info = row.split(" ")
            particle_info = list(filter(lambda x : x != '', particle_info))
            masses.append(float(particle_info[0]))
            radii.append(float(particle_info[1]))
        return StaticInfo(N, L, masses, radii)

def parse_dynamic_file(static_info : StaticInfo, dynamic_file : str) -> DynamicInfo:
    with open(dynamic_file) as file:
        positions  = []
        velocities = []
        instants   = []
        t_index   = 0
        while(True):
            t = file.readline()
            if not t:
                break
            instants.append(float(t))
            positions.append([])
            velocities.append([])
            for _ in range(static_info.N):
                particle_info = file.readline()
                particle_info = particle_info.split(" ")
                particle_info = list(filter(lambda x : x != '', particle_info))
                positions[t_index].append((float(particle_info[0]), float(particle_info[1])))
                velocities[t_index].append((float(particle_info[2]), float(particle_info[3])))
            t_index += 1
        return DynamicInfo(instants, positions, velocities)
    
def get_simulation_info(static_info : StaticInfo, dynamic_info : DynamicInfo) -> SimulationInfo:
    particles = []
    for i in range(static_info.N):
        particles.append(ParticleInfo(
            static_info.masses[i],
            static_info.radii[i],
            [positions_for_t[i]  for positions_for_t in dynamic_info.positions],
            [velocities_for_t[i] for velocities_for_t in dynamic_info.velocities]
        ))
    return SimulationInfo(static_info.N, static_info.L, dynamic_info.instants, particles)

def parse_simulation_files(static_file : str, dynamic_file : str) -> SimulationInfo:
    static_info         = parse_static_file(static_file)
    dynamic_info        = parse_dynamic_file(static_info, dynamic_file)
    return get_simulation_info(static_info, dynamic_info)