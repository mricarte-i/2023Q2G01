from argument_parser   import parse_arguments
from simulation_parser import parse_simulation_files
from visual_generator  import generate_visualization

w = 0.09
h = 0.09

L, static_file, dynamic_file, ovito_file = parse_arguments()

simulation_info = parse_simulation_files(static_file, dynamic_file)

generate_visualization(w, h, L, ovito_file)