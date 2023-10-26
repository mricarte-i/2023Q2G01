from argument_parser    import parse_arguments
from simulation_parser  import parse_simulation_files
from sequence_generator import convert_to_sequence
from frame_generator    import generate_frames
from visual_generator   import generate_visualization

dt, w, D, static_file, dynamic_file, xyz_file, ovito_file, animation_file = parse_arguments()

simulation_info = parse_simulation_files(static_file, dynamic_file)

if dt is not None:
    simulation_info = convert_to_sequence(simulation_info, dt)

generate_frames(simulation_info, xyz_file, w, D)

generate_visualization(simulation_info, xyz_file, ovito_file, animation_file)