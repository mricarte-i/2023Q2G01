from ovito.data      import DataCollection, Particles
from ovito.pipeline  import Pipeline, StaticSource
from ovito.io        import import_file
from ovito.vis       import ParticlesVis, Viewport
from ovito.modifiers import AssignColorModifier
import ovito
import os
import numpy as np

def add_cell(w : float, h : float, x: float = 0, y: float = 0, cell_width : float = 0.0001) -> None:
    cell_data = DataCollection()
    cell_dims = [[w,0,0,x],
                 [0,h,0,y],
                 [0,0,0,0]]
    
    cell = cell_data.create_cell(cell_dims, pbc=(False, False, False))
    cell.vis.line_width = cell_width
    cell.vis.enabled = False
    cell_data.objects.append(cell)
    cell_pipeline = Pipeline(source=StaticSource(data=cell_data))
    cell_pipeline.add_to_scene()

def add_particles(xyz_file : str) -> None:
    particles_pipeline = import_file(xyz_file, columns=["Particle Identifier", "Position.X", "Position.Y", "Position.Z", "Radius"])
    particles_pipeline.modifiers.append(AssignColorModifier(operate_on="particles", color=(0.9882352941176471, 0.7803921568627451, 0.16470588235294117)))
    particle_data = particles_pipeline.compute()

    particle_data.particles_.vis.shape = ParticlesVis.Shape.Circle
    particle_data.cell_.vis.enabled = False

    particles_pipeline.add_to_scene()

def add_poly(w : float, h : float, L : float, flask_width : float = 0.0005):
    data           = DataCollection()
    data.particles = Particles(count = 8)

    data.particles_.vis.enabled = True
    data.particles_.vis.shape = ParticlesVis.Shape.Square

    coordinates = data.particles.create_property('Position')

    half_flask_width = flask_width / 2

    coordinates[0,0] = 0                        - half_flask_width
    coordinates[0,1] = 0                        - half_flask_width
    coordinates[0,2] = 0

    coordinates[1,0] = w                        + half_flask_width
    coordinates[1,1] = 0                        - half_flask_width
    coordinates[1,2] = 0

    coordinates[2,0] = 0                        - half_flask_width
    coordinates[2,1] = h                        + half_flask_width
    coordinates[2,2] = 0

    coordinates[3,0] = w                        + half_flask_width
    coordinates[3,1] = h                        + half_flask_width
    coordinates[3,2] = 0

    coordinates[4,0] = w                        + half_flask_width
    coordinates[4,1] = (h- L) / 2               - half_flask_width
    coordinates[4,2] = 0

    coordinates[5,0] = w                        + half_flask_width
    coordinates[5,1] = (h - L) / 2 + L          + half_flask_width
    coordinates[5,2] = 0

    coordinates[6,0] = 2*w                      + half_flask_width
    coordinates[6,1] = (h - L) / 2              - half_flask_width
    coordinates[6,2] = 0

    coordinates[7,0] = 2*w                      + half_flask_width
    coordinates[7,1] = (h - L) / 2 + L          + half_flask_width
    coordinates[7,2] = 0

    radii = data.particles.create_property("Radius")
    radii[:] = half_flask_width

    color = data.particles.create_property("Color")
    color[:] = (0.06274509803921569, 0.10980392156862745, 0.6313725490196078)

    pairs = [(0, 1), (0, 2), (2, 3), (1, 4), (3, 5), (4, 6), (5, 7), (6, 7)] # Pairs of particle indices to connect by bonds
    bonds = data.particles_.create_bonds(count=len(pairs), vis_params={'width': flask_width, 'flat_shading' : True})
    bonds.colors_ = (0.06274509803921569, 0.10980392156862745, 0.6313725490196078)
    bonds.create_property('Topology', data=pairs)
    pipeline = Pipeline(source=StaticSource(data=data))
    pipeline.add_to_scene()

def save_animation_state_file(ovito_file : str) -> None:
    ovito_file_split = ovito_file.rsplit('/', 1)
    if len(ovito_file_split) > 1:
        os.makedirs(ovito_file_split[0], exist_ok=True)
    ovito.scene.save(ovito_file)

def render_animation(animation_file : str, h_res : int = 1920, v_res : int = 1080) -> None:
    vp = Viewport()
    vp.type = Viewport.Type.Top
    vp.zoom_all()

    animation_file_split = animation_file.rsplit('/', 1)
    if len(animation_file_split) > 1:
        os.makedirs(animation_file_split[0], exist_ok=True)
    vp.render_anim(animation_file, size=(h_res,v_res))

def generate_visualization(w : float, h : float, L : float, xyz_file : str, ovito_file : str) -> None:
    #add_cell(w, h, x=0, y=0)
    #add_cell(w, L, x=w, y=(h - L)/2)

    add_poly(w, h, L)

    add_particles(xyz_file)

    save_animation_state_file(ovito_file)