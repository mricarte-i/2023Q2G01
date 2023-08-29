from argument_parser   import parse_arguments
from simulation_parser import parse_simulation_files
from plot_generator    import plot_polarization_over_iterations
from frame_generator   import generate_frames
from visual_generator  import generate_visualization
import matplotlib
matplotlib.use('Agg')

# Parse filenames from terminal arguments
static_file, dynamic_file, polarization_file, plot_file, xyz_file, ovito_file, animation_file = parse_arguments()

# Parse static and dynamic files' content into SimulationInfo
simulation_info = parse_simulation_files(static_file, dynamic_file, polarization_file)

# Plot order coefficient over iteration
plot_polarization_over_iterations(simulation_info, plot_file)

# Transform simulation info to xyz files with ovito module
generate_frames(simulation_info, xyz_file)

# Load xyz file, apply triangle meshes and save simulation
generate_visualization(simulation_info, xyz_file, ovito_file, animation_file)