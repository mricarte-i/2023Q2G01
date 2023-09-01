import re
import pandas as pd

def get_params_from_filename(static_file_list : list[str], dynamic_file_list : list[str], polarization_file_list : list[str]) -> list[float]:
    eta_list    = []
    eta_pattern = r"_(l-\d+_eta-((\d+\.\d+)|\d+)pi)_"
    for static_file in static_file_list:
        match = re.search(eta_pattern, static_file)
        if match : eta_list.append(float(match.group(2)))
    return eta_list