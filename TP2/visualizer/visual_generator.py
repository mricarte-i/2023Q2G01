import ovito
from ovito.data import SimulationCell
from ovito.io import import_file
from simulation_parser import SimulationInfo
import numpy as np

def generate_visualization(simulation_info : SimulationInfo, xyz_file : str, ovito_file : str) -> None:
    pipeline = import_file(xyz_file, columns = ['Particle Identifier', 'Position.X', 'Position.Y', 'Position.Z', "Radius"])
    pipeline.add_to_scene()
    ovito.scene.save(ovito_file)