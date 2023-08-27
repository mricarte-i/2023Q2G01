import ovito
from ovito.io import import_file
from simulation_parser import SimulationInfo
from ovito.data import SimulationCell, DataCollection
from ovito.pipeline import Pipeline, StaticSource
import numpy as np
from ovito.vis import Viewport


def create_2d_cell(L):
    cell = SimulationCell(pbc = (True, True, True))
    cell[...] = [[L,0,0,0],
                 [0,L,0,0],
                 [0,0,0,0]]
    return cell



def generate_visualization(simulation_info : SimulationInfo, lammps_data_file : str, ovito_file : str) -> None:
    data = DataCollection()

    L = simulation_info.L

    cell_dims = [[L,0,0,0],
                 [0,L,0,0],
                 [0,0,0,0]]
    
    cell = data.create_cell(cell_dims, pbc=(True, True, True))
    cell.vis.line_width = 0.1
    data.objects.append(cell)
    
    cell_pipeline = Pipeline(source=StaticSource(data=data))
    cell_pipeline.add_to_scene()

    particles_pipeline = import_file(lammps_data_file + ".xyz", columns=["Particle Identifier", "Position.X", "Position.Y", "Position.Z", "Radius", "Dipole Orientation.X", "Dipole Orientation.Y", "Dipole Orientation.Z", "Angle"])
    data = particles_pipeline.compute()
    data.particles_.vis.enabled = False
    data.particles_['Dipole Orientation'].vis.enabled = True
    data.particles_['Dipole Orientation'].vis.width = 0.1
    data.cell_.vis.enabled = False
    particles_pipeline.add_to_scene()

    ovito.scene.save(ovito_file)