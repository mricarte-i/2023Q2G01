from ovito.pipeline import Pipeline, PythonScriptSource
from ovito.io import export_file
from ovito.data import Particles, SimulationCell
import numpy as np
from simulation_parser import SimulationInfo

def create_3d_cell(L):
    cell = SimulationCell(pbc = (True, True, True))
    cell[...] = [[L,0,0,0],
                 [0,L,0,0],
                 [0,0,L,0]]
    return cell

def create_2d_cell(L):
    cell = SimulationCell(pbc = (True, True, True))
    cell[...] = [[L,0,0,0],
                 [0,L,0,0],
                 [0,0,0,0]]
    return cell

def create_particles_from_sim_info(frame, data, simulation_info : SimulationInfo):
    data.particles = Particles(count = simulation_info.N)
    cell = create_2d_cell(simulation_info.L)
    cell.vis.line_width = 0.1
    data.objects.append(cell)
    coordinates = data.particles.create_property('Position')
    radius = data.particles.create_property("Radius")
    for i in range(simulation_info.N):
        coordinates[i,0] = simulation_info.particles[i].position[frame][0]
        coordinates[i,1] = simulation_info.particles[i].position[frame][1]
        coordinates[i,2] = 0
        radius[i] = 1
    

def generate_frames(simulation_info : SimulationInfo, lammps_data_file : str) -> None:
    create_particles = lambda frame, data : create_particles_from_sim_info(frame, data, simulation_info)
    pipeline = Pipeline(source=PythonScriptSource(function=create_particles))
    export_file(pipeline, lammps_data_file + ".*.data", format="lammps/data",
            multiple_frames=True, start_frame=0, end_frame=simulation_info.instant_count-1)