from ovito.pipeline    import Pipeline, PythonScriptSource
from ovito.io          import export_file
from ovito.data        import Particles
from simulation_parser import SimulationInfo
import os
import math

A = 0.15

def base_y_position(frame, w: float, simulation_info: SimulationInfo) -> float:
    return A * math.sin(w * simulation_info.instants[frame])

def create_particles_from_sim_info(frame, data, w : float, D : float, simulation_info : SimulationInfo, base_width : float = 1):
    data.particles = Particles(count = simulation_info.N + 6)
    coordinates = data.particles.create_property('Position')
    radius = data.particles.create_property("Radius")
    particle_types = data.particles.create_property("Particle Type")

    base_half_width  = base_width / 2
    coordinates[0,0] = 0 - base_half_width
    coordinates[1,0] = (simulation_info.W - D) / 2 - base_half_width
    coordinates[2,0] = (simulation_info.W - D) / 2 + D + base_half_width
    coordinates[3,0] = simulation_info.W + base_half_width
    
    for curr_idx in range(4):
        coordinates[curr_idx,1] = base_y_position(frame, w, simulation_info) - base_half_width
        coordinates[curr_idx,2] = 0
        radius[curr_idx] = base_half_width
        particle_types[curr_idx] = curr_idx + 1

    coordinates[4,0] = 0 - base_half_width
    coordinates[4,1] = simulation_info.L + base_half_width
    coordinates[4,2] = 0
    radius[4] = base_half_width
    particle_types[4] = 5


    coordinates[5,0] = simulation_info.W + base_half_width
    coordinates[5,1] = simulation_info.L + base_half_width
    coordinates[5,2] = 0
    radius[5] = base_half_width
    particle_types[5] = 6

    for i in range(simulation_info.N):
        curr_idx = i+6
        coordinates[curr_idx,0] = simulation_info.particles[i].position[frame][0]
        coordinates[curr_idx,1] = simulation_info.particles[i].position[frame][1]
        coordinates[curr_idx,2] = 0
        radius[curr_idx] = simulation_info.particles[i].radius
        particle_types[curr_idx] = 0

def generate_frames(simulation_info : SimulationInfo, xyz_file : str, w : float, D : float) -> None:
    create_particles = lambda frame, data : create_particles_from_sim_info(frame, data, w, D, simulation_info)
    pipeline = Pipeline(source=PythonScriptSource(function=create_particles))
    xyz_file_split = xyz_file.rsplit("/", 1)
    if len(xyz_file_split) > 1: 
        os.makedirs(xyz_file_split[0], exist_ok=True)
    export_file(pipeline, xyz_file, format="xyz",
            columns=["Particle Identifier", "Position.X", "Position.Y", "Position.Z", "Radius", "Particle Type"],
            multiple_frames=True, start_frame=0, end_frame=len(simulation_info.instants)-1)