import ovito
from ovito.io import import_file
from simulation_parser import SimulationInfo
from ovito.data import SimulationCell, DataCollection
from ovito.pipeline import Pipeline, StaticSource
import numpy as np
from ovito.vis import Viewport, VectorVis, OpenGLRenderer
import math
from ovito.modifiers import ColorCodingModifier

def num_to_rgb(val, max_val=1):
    i = (val * 255 / max_val)
    r = round(math.sin(0.024 * i + 0) * 127 + 128)
    g = round(math.sin(0.024 * i + 2) * 127 + 128)
    b = round(math.sin(0.024 * i + 4) * 127 + 128)
    return (r,g,b)

def a(v : VectorVis):
    v.color_mapping_interval

color_table = [
    (0.9882352941176471 , 0.7803921568627451 , 0.16470588235294117),
    (0.9803921568627451 , 0.1450980392156863 , 0.01568627450980392),
    (0.06274509803921569, 0.10980392156862745, 0.6313725490196078),
    (0.12549019607843137, 0.6823529411764706 , 0.1411764705882353),
    (0.9882352941176471 , 0.7803921568627451 , 0.16470588235294117)
]

def generate_visualization(simulation_info : SimulationInfo, xyz_file : str, ovito_file : str, animation_file : str) -> None:
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

    particles_pipeline = import_file(xyz_file + ".xyz", columns=["Particle Identifier", "Position.X", "Position.Y", "Position.Z", "Dipole Orientation.X", "Dipole Orientation.Y", "Dipole Orientation.Z", "Angle"])
    data = particles_pipeline.compute()
    data.particles_.vis.enabled = False
    data.particles_['Dipole Orientation'].vis.enabled = True
    data.particles_['Dipole Orientation'].vis.width = 0.1
    data.particles_['Dipole Orientation'].vis.color_mapping_gradient = ColorCodingModifier.Gradient(color_table)
    data.particles_['Dipole Orientation'].vis.color_mapping_property = 'Angle'
    data.particles_['Dipole Orientation'].vis.color_mapping_interval = (0, 2*math.pi)
    data.cell_.vis.enabled = False
    particles_pipeline.add_to_scene()
    
    vp = Viewport()
    vp.type = Viewport.Type.Top
    vp.zoom_all()

    ovito.scene.save(ovito_file)

    vp.render_anim(animation_file, size=(3840,2160))