from utils.argument_parser    import parse_order_over_noise_arguments
from utils.file_system_parser import get_files_in_directory
from utils.df_utils           import get_simulation_df
from utils.plot_generator     import plot_order_over_density

directory, va_instant = parse_order_over_noise_arguments()
files, directory      = get_files_in_directory(directory)
simulation_df         = get_simulation_df(files, directory, va_instant)
plot_order_over_density(simulation_df)