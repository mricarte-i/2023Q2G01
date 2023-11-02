from ovito.data        import DataCollection, Particles, ParticleType
from ovito.pipeline    import Pipeline, StaticSource
from ovito.io          import import_file
from ovito.vis         import ParticlesVis, Viewport
from ovito.modifiers   import AssignColorModifier, ExpressionSelectionModifier, CreateBondsModifier
from simulation_parser import SimulationInfo
import numpy as np
import ovito
import os

def add_particles(xyz_file : str) -> None:
    particles_pipeline = import_file(xyz_file, columns=["Particle Identifier", "Position.X", "Position.Y", "Position.Z", "Radius", "Particle Type"])
    
    particles_pipeline.modifiers.append(ExpressionSelectionModifier(expression="ParticleIdentifier > 6"))
    particles_pipeline.modifiers.append(AssignColorModifier(operate_on="particles", color=(0.9882352941176471, 0.7803921568627451, 0.16470588235294117), keep_selection=False))
    particles_pipeline.modifiers.append(ExpressionSelectionModifier(expression="ParticleIdentifier <= 6"))
    particles_pipeline.modifiers.append(AssignColorModifier(operate_on="particles", color=(0.06274509803921569, 0.10980392156862745, 0.6313725490196078), keep_selection=False))
    
    bonds_modif = CreateBondsModifier(mode=CreateBondsModifier.Mode.Pairwise)
    bonds_modif.vis.width = 1
    bonds_modif.vis.flat_shading = True
    bonds_modif.set_pairwise_cutoff(1, 2, float("inf"))
    bonds_modif.set_pairwise_cutoff(3, 4, float("inf"))
    bonds_modif.set_pairwise_cutoff(1, 5, float("inf"))
    bonds_modif.set_pairwise_cutoff(4, 6, float("inf"))

    particles_pipeline.modifiers.append(bonds_modif)

    particle_data = particles_pipeline.compute()

    particle_data.particles_.vis.shape = ParticlesVis.Shape.Sphere
    particle_data.cell_.vis.enabled = False

    particle_types = particles_pipeline.source.data.particles_.particle_types_

    for i in range(1, 7):
        particle_types.type_by_id_(i).shape = ParticlesVis.Shape.Square

    particles_pipeline.add_to_scene()

def add_poly(L : float, W : float, flask_width : float = 1):
    data           = DataCollection()
    data.particles = Particles(count = 2)

    data.particles_.vis.enabled = True
    data.particles_.vis.shape = ParticlesVis.Shape.Square

    coordinates = data.particles.create_property('Position')

    half_flask_width = flask_width / 2

    coordinates[0,0] = 0 - half_flask_width
    coordinates[0,1] = L + half_flask_width
    coordinates[0,2] = 0

    coordinates[1,0] = W + half_flask_width
    coordinates[1,1] = L + half_flask_width
    coordinates[1,2] = 0

    radii = data.particles.create_property("Radius")
    radii[:] = half_flask_width

    color = data.particles.create_property("Color")
    color[:] = (0.06274509803921569, 0.10980392156862745, 0.6313725490196078)

    pairs = [(0, 1)] # Pairs of particle indices to connect by bonds
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

def render_animation(animation_file : str, h_res : int = 1600, v_res : int = 1200) -> None:
    vp = Viewport()
    vp.type = Viewport.Type.Top
    vp.zoom_all(size=(h_res,v_res))

    animation_file_split = animation_file.rsplit('/', 1)
    if len(animation_file_split) > 1:
        os.makedirs(animation_file_split[0], exist_ok=True)
    vp.render_anim(animation_file, size=(h_res,v_res), fps=25)

def generate_visualization(sim_info : SimulationInfo, xyz_file : str, ovito_file : str, animation_file : str) -> None:

    add_particles(xyz_file)

    add_poly(sim_info.L, sim_info.W)

    save_animation_state_file(ovito_file)

    if animation_file is not None:
        render_animation(animation_file)