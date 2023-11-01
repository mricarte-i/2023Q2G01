from dataclasses import dataclass

@dataclass
class StaticInfo:
    N        : int
    L        : float
    W        : float
    D        : float
    w        : float
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
    W         : float
    D         : float
    w         : float
    instants  : list[float]
    particles : list[ParticleInfo]
    

def parse_static_file(static_file : str) -> StaticInfo:
    with open(static_file) as file:
        N       = int(file.readline())
        W       = float(file.readline())
        L       = float(file.readline())
        D       = float(file.readline())
        w       = float(file.readline())
        masses  = []
        radii   = []
        for row in file:
            particle_info = row.split(" ")
            particle_info = list(filter(lambda x : x != '', particle_info))
            masses.append(float(particle_info[0]))
            radii.append(float(particle_info[1]))
        return StaticInfo(N, L, W, D, w, masses, radii)

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
    return SimulationInfo(static_info.N, static_info.L, static_info.W, static_info.D, static_info.w, dynamic_info.instants, particles)

def parse_simulation_files(static_file : str, dynamic_file : str) -> SimulationInfo:
    static_info         = parse_static_file(static_file)
    dynamic_info        = parse_dynamic_file(static_info, dynamic_file)
    return get_simulation_info(static_info, dynamic_info)

def parse_multiple_simulation_files(static_files : list[str], dynamic_files : list[str]) -> list[SimulationInfo]:
    static_file_count  = len(static_files)
    dynamic_file_count = len(dynamic_files)

    if static_file_count != dynamic_file_count:
        raise ValueError("number of static files and dynamic files don't match. static files are {}, dynamic files are {}".format(static_file_count, dynamic_file_count))
    
    sim_info_list = []
    for i in range(static_file_count):
        static_file  = static_files[i]
        dynamic_file = dynamic_files[i]

        sim_info = parse_simulation_files(static_file, dynamic_file)

        sim_info_list.append(sim_info)

    return sim_info_list

def parse_multiple_simulation_files_from_various_simulations(static_files_list : list[list[str]], dynamic_files_list : list[list[str]]) -> list[list[SimulationInfo]]:
    static_files_list_count  = len(static_files_list)
    dynamic_files_list_count = len(dynamic_files_list) 

    if static_files_list_count != dynamic_files_list_count:
        raise ValueError("number of static files from various simulations and dynamic files from various simulations don't match. static files of various simulations are {}, dynamic files of various simulations are {}".format(static_files_list_count, dynamic_files_list_count))
    
    sim_info_lists = []
    for i in range(static_files_list_count):
        static_files  = static_files_list[i]
        dynamic_files = dynamic_files_list[i]
        
        sim_info_list = parse_multiple_simulation_files(static_files, dynamic_files)
        sim_info_lists.append(sim_info_list)

    return sim_info_lists