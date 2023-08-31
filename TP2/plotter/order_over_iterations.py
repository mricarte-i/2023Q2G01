from utils.argument_parser  import parse_order_over_iterations_arguments
from utils.ref_files_parser import read_order_over_iterations_files
from utils.filename_parser  import get_params_from_filename
from utils.plot_generator   import plot_polarization_over_iterations_from_files

static_files,     dynamic_files,     polarization_files,     colors_file, out_image_file = parse_order_over_iterations_arguments()
static_file_list, dynamic_file_list, polarization_file_list, colors      = read_order_over_iterations_files(static_files, dynamic_files, polarization_files, colors_file)
noises = get_params_from_filename(static_file_list, dynamic_file_list, polarization_file_list)
plot_polarization_over_iterations_from_files(static_file_list, dynamic_file_list, polarization_file_list, out_image_file, colors, noises)