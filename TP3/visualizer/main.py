from argument_parser    import parse_arguments
from simulation_parser  import parse_simulation_files
from sequence_generator import convert_to_sequence
from frame_generator    import generate_frames
from visual_generator   import generate_visualization

w = 0.09
h = 0.09

L, dt, static_file, dynamic_file, xyz_file, ovito_file = parse_arguments()

simulation_info = parse_simulation_files(static_file, dynamic_file)

simulation_info = convert_to_sequence(simulation_info, dt)

generate_frames(simulation_info, xyz_file)

generate_visualization(w, h, L, xyz_file, ovito_file)