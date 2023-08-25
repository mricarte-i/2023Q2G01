from dataclasses import dataclass

@dataclass
class StaticInfo:
    N        : int
    L        : float
    radii    : list[float]

@dataclass
class DynamicInfo:
    instant_count : int
    positions     : list[list[tuple[float, float]]]
    angles        : list[list[float]]

@dataclass
class ParticleInfo:
    radius   : float
    position : list[tuple[float,float]]
    angle    : list[float]

@dataclass
class SimulationInfo:
    N             : int
    L             : float
    instant_count : int
    particles     : list[ParticleInfo]
    

def parse_static_file(static_file : str) -> StaticInfo:
    with open(static_file) as file:
        N       = int(file.readline())
        L       = float(file.readline())
        radii   = []
        for row in file:
            particle_info = row.split(" ")
            particle_info = list(filter(lambda x : x != '', particle_info))
            radii.append(float(particle_info[0]))
        return StaticInfo(N, L, radii)

def parse_dynamic_file(static_info : StaticInfo, dynamic_file : str) -> DynamicInfo:
    with open(dynamic_file) as file:
        positions = []
        angles    = []
        t_index   = 0
        while(True):
            t = file.readline()
            if not t:
                break
            positions.append([])
            angles.append([])
            for i in range(static_info.N):
                particle_info = file.readline()
                particle_info = particle_info.split(" ")
                particle_info = list(filter(lambda x : x != '', particle_info))
                positions[t_index].append((float(particle_info[0]), float(particle_info[1])))
                angles[t_index].append(float(particle_info[4]))
            t_index += 1
        return DynamicInfo(t_index, positions, angles)
    
def get_simulation_info(static_info : StaticInfo, dynamic_info : DynamicInfo) -> SimulationInfo:
    particles = []
    for i in range(static_info.N):
        particles.append(ParticleInfo(
            static_info.radii[i],
            [positions_for_t[i] for positions_for_t in dynamic_info.positions],
            [angles_for_t[i] for angles_for_t in dynamic_info.angles]
        ))
    return SimulationInfo(static_info.N, static_info.L, dynamic_info.instant_count, particles)

def parse_simulation_files(static_file : str, dynamic_file : str) -> SimulationInfo:
    static_info  = parse_static_file(static_file)
    dynamic_info = parse_dynamic_file(static_info, dynamic_file)
    return get_simulation_info(static_info, dynamic_info)