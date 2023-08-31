from dataclasses import dataclass
import math

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
    polarization  : list[float]

    def density(self) -> float:
        return self.N / math.pow(self.L, 2)
    

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
    
def angle_from_x_y(x : float, y : float) -> float:
    mod_x = abs(x)
    mod_y = abs(y)
    normal_angle = math.atan(mod_y/mod_x)
    if y >= 0 and x < 0:
        normal_angle = math.pi - normal_angle
    elif y < 0 and x < 0:
        normal_angle = math.pi + normal_angle
    elif y < 0 and x >= 0:
        normal_angle = 2*math.pi - normal_angle
    return normal_angle

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
                angles[t_index].append(angle_from_x_y(float(particle_info[3]), float(particle_info[4])))
            t_index += 1
        return DynamicInfo(t_index, positions, angles)
    
def parse_polarization_file(polarization_file : str) -> list[float]:
    polarization_values = None
    with open(polarization_file) as file:
        while(True):
            t = file.readline()
            if not t:
                break
            if polarization_values is None:
                polarization_values = []
            polarization_values.append(float(file.readline()))
    return polarization_values
    
def get_simulation_info(static_info : StaticInfo, dynamic_info : DynamicInfo, polarization_values : list[float]) -> SimulationInfo:
    particles = []
    for i in range(static_info.N):
        particles.append(ParticleInfo(
            static_info.radii[i],
            [positions_for_t[i] for positions_for_t in dynamic_info.positions],
            [angles_for_t[i] for angles_for_t in dynamic_info.angles]
        ))
    return SimulationInfo(static_info.N, static_info.L, dynamic_info.instant_count, particles, polarization_values)

def parse_simulation_files(static_file : str, dynamic_file : str, polarization_file : str) -> SimulationInfo:
    static_info         = parse_static_file(static_file)
    dynamic_info        = parse_dynamic_file(static_info, dynamic_file)
    polarization_values = parse_polarization_file(polarization_file)
    return get_simulation_info(static_info, dynamic_info, polarization_values)