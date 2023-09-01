import pandas as pd
import re
from utils.simulation_parser import parse_simulation_files
import math

def get_files_df(file_list : list[str], directory : str) -> pd.DataFrame:
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
            file_dict["L"].append(int(match.group(2)))
            file_dict["eta"].append(float(match.group(3)))
            file_dict["iter"].append(int(match.group(5)))
            file_dict["filename"].append(str(directory) + str(file))
    return pd.DataFrame(data=file_dict)

def get_simulation_files_df(filenames : list[str], directory : str):
    file_df         = get_files_df(filenames, directory)
    grouped_file_df = file_df.groupby(["L", "eta", "iter"])
    merged_file_df = pd.DataFrame(columns=["L", "eta", "iter", "static_file", "dynamic_file", "polarization_file"])
    for group, frame in grouped_file_df:
        new_row = {
            "L"                 : [group[0]],
            "eta"               : [group[1]],
            "iter"              : [group[2]],
            "static_file"       : [frame[frame["type"] == "static"]["filename"].iloc[0]],
            "dynamic_file"      : [frame[frame["type"] == "out"]["filename"].iloc[0]],
            "polarization_file" : [frame[frame["type"] == "polout"]["filename"].iloc[0]]
        }
        merged_file_df = pd.concat([merged_file_df, pd.DataFrame(data=new_row)])
    return merged_file_df


def get_simulation_df(filenames : list[str], directory : str, va_instant : int) -> pd.DataFrame:
    merged_file_df = get_simulation_files_df(filenames, directory)
    simulation_df  = pd.DataFrame(columns=["L", "N", "eta", "iter", "va"])
    for _, iter_row in merged_file_df.iterrows():
        simulation_info = parse_simulation_files(iter_row["static_file"], iter_row["dynamic_file"], iter_row["polarization_file"])
        new_row = {
            "L"       : [simulation_info.L],
            "N"       : [simulation_info.N],
            "density" : [simulation_info.N / math.pow(simulation_info.L, 2)],
            "eta"     : [iter_row["eta"]],
            "iter"    : [iter_row["iter"]],
            "va"      : [simulation_info.polarization[va_instant]]
        }
        simulation_df = pd.concat([simulation_df, pd.DataFrame(data=new_row)])
    return simulation_df
    
