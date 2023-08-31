def read_string_list_from_file(file_name : str) -> list[str]:
    strings = []
    with open(file_name) as file:
        while(True):
            line = file.readline()
            if not line:
                break
            line = line.strip()
            if line != "" : strings.append(line)
    return strings

def read_simulation_refs_files(static_files : str, dynamic_files : str, polarization_files : str) -> tuple[list[str], list[str], list[str]]:
    static_file_list        = read_string_list_from_file(static_files)
    dynamic_file_list       = read_string_list_from_file(dynamic_files)
    polarization_file_list  = read_string_list_from_file(polarization_files)
    return (static_file_list, dynamic_file_list, polarization_file_list)

def read_order_over_iterations_files(static_files : str, dynamic_files : str, polarization_files : str, colors_file : str) -> tuple[list[str], list[str], list[str]]:
    static_file_list, \
        dynamic_file_list, \
        polarization_file_list = read_simulation_refs_files(static_files, dynamic_files, polarization_files)
    colors = read_string_list_from_file(colors_file) 
    return (static_file_list, dynamic_file_list, polarization_file_list, colors)