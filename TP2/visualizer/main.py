from argument_parser   import parse_arguments
from simulation_parser import parse_simulation_files
from frame_generator   import generate_frames
from visual_generator  import generate_visualization

# Parse filenames from terminal arguments
static_file, dynamic_file, xyz_file, ovito_file, animation_file = parse_arguments()

# Parse static and dynamic files' content into SimulationInfo
simulation_info = parse_simulation_files(static_file, dynamic_file)

# Transform simulation info to xyz files with ovito module
generate_frames(simulation_info, xyz_file)

# Load xyz file, apply triangle meshes and save simulation
generate_visualization(simulation_info, xyz_file, ovito_file, animation_file)