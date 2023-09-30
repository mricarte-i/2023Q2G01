from ovito.pipeline    import Pipeline, PythonScriptSource
from ovito.io          import export_file
from ovito.data        import Particles
from simulation_parser import SimulationInfo
import os

def create_particles_from_sim_info(frame, data, simulation_info : SimulationInfo):
    data.particles = Particles(count = simulation_info.N)
    coordinates = data.particles.create_property('Position')
    radius = data.particles.create_property("Radius")
    for i in range(simulation_info.N):
        coordinates[i,0] = simulation_info.particles[i].position[frame][0]
        coordinates[i,1] = simulation_info.particles[i].position[frame][1]
        coordinates[i,2] = 0
        radius[i] = simulation_info.particles[i].radius

def generate_frames(simulation_info : SimulationInfo, xyz_file : str) -> None:
    create_particles = lambda frame, data : create_particles_from_sim_info(frame, data, simulation_info)
    pipeline = Pipeline(source=PythonScriptSource(function=create_particles))
    xyz_file_split = xyz_file.rsplit("/", 1)
    if len(xyz_file_split) > 1: 
        os.makedirs(xyz_file_split[0], exist_ok=True)
    export_file(pipeline, xyz_file, format="xyz",
            columns=["Particle Identifier", "Position.X", "Position.Y", "Position.Z", "Radius"],
            multiple_frames=True, start_frame=0, end_frame=len(simulation_info.instants)-1)