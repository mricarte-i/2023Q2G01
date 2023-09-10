from argument_parser  import parse_arguments
from visual_generator import generate_visualization

w = 0.09
h = 0.09

L, ovito_file = parse_arguments()

generate_visualization(w, h, L, ovito_file)