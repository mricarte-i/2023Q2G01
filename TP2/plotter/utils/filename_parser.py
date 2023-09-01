import re
import pandas as pd

def get_params_from_filename(static_file_list : list[str], dynamic_file_list : list[str], polarization_file_list : list[str]) -> list[float]:
    eta_list    = []
    eta_pattern = r"_(l-\d+_eta-((\d+\.\d+)|\d+)pi)_"
    for static_file in static_file_list:
        match = re.search(eta_pattern, static_file)
        if match : eta_list.append(float(match.group(2)))
    return eta_list

def get_files_df(file_list : list[str]) -> pd.DataFrame:
    pattern  = r"^(static|out|polout)_l-(\d+)_eta-((\d+\.\d+)|\d+)pi_iter-(\d+)\.txt$"
    file_dict = {
        "type"     : [],
        "L"        : [],
        "eta"      : [],
        "iter"     : [],
        "filename" : []
    }
    for file in file_list:
        match = re.search(pattern, file)
        if match:
            file_dict["type"].append(match.group(1))
            file_dict["L"].append(match.group(2))
            file_dict["eta"].append(match.group(3))
            file_dict["iter"].append(match.group(4))
            file_dict["filename"].append(file)
    return pd.DataFrame(data=file_dict)