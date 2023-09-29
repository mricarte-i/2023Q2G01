from simulation_parser import SimulationInfo, parse_multiple_simulation_files, parse_multiple_simulation_files_from_various_simulations
import numpy  as np
import pandas as pd

def __single_calculate_err__(x_analytical : float, x_numerical : float) -> float:
    return (x_analytical - x_numerical)

def calculate_err_time_series_from_sim_info(analytical : SimulationInfo, numerical : SimulationInfo) -> tuple[np.ndarray, np.ndarray]:
    count_analytical_instants = len(analytical.instants)
    count_numerical_instants  = len(numerical.instants)

    if count_analytical_instants != count_numerical_instants:
        raise ValueError("instant count must be the same for analytical and numerical")
    
    instants = []
    errors   = []
    for i in range(count_analytical_instants):
        t_analytical = analytical.instants[i]
        
        x_analytical, _ = analytical.particles[0].position[i]
        x_numerical,  _ = numerical.particles[0].position[i]

        sqr_error = __single_calculate_err__(x_analytical, x_numerical)

        instants.append(t_analytical)
        errors.append(sqr_error)

    instants = np.array(instants)
    errors   = np.array(errors)

    return (instants, errors)

def calculate_multiple_err_time_series_from_sim_info(analyticals : list[SimulationInfo], numericals : list[SimulationInfo]) -> tuple[list[np.ndarray], list[np.ndarray]]:
    instant_series_list = []
    sqr_err_series_list = []
    
    for i in range(len(analyticals)):
        analytical = analyticals[i]
        numerical  = numericals[i]

        instant_series, err_series = calculate_err_time_series_from_sim_info(analytical, numerical)
        sqr_err_series = np.power(err_series, 2)

        instant_series_list.append(instant_series)
        sqr_err_series_list.append(sqr_err_series)
    
    return (instant_series_list, sqr_err_series_list)

def calculate_sqr_err_df_from_time_series(steps : list[float], sqr_err_time_series : list[np.ndarray]) -> pd.DataFrame:
    dfs = pd.DataFrame(columns=["step", "sqr_err"])

    for i in range(len(steps)):
        step = steps[i]
        
        df = pd.DataFrame(data=sqr_err_time_series[i], columns=["sqr_err"])
        df = df.assign(step=step)
        
        dfs = pd.concat([dfs, df], ignore_index=True)
    
    return dfs
        
def calculate_sqr_err_df_from_sim_info(steps : list[float], analyticals : list[SimulationInfo], numericals : list[SimulationInfo]) -> pd.DataFrame:
    _, sqr_err_series_list = calculate_multiple_err_time_series_from_sim_info(analyticals, numericals)

    sqr_err_df = calculate_sqr_err_df_from_time_series(steps, sqr_err_series_list)

    return sqr_err_df

def calculate_sqr_err_df_from_files(steps : list[float], analytical_static_files : list[str], analytical_dynamic_files : list[str], numerical_static_files_list : list[list[str]], numerical_dynamic_files_list : list[list[str]], numerical_labels : list[str]) -> pd.DataFrame:
    analytical_sim_info_list = parse_multiple_simulation_files(analytical_static_files, analytical_dynamic_files)
    numerical_sim_info_lists = parse_multiple_simulation_files_from_various_simulations(numerical_static_files_list, numerical_dynamic_files_list)

    numerical_sim_info_lists_count = len(numerical_sim_info_lists)
    
    sqr_err_dfs = pd.DataFrame(columns=["step", "sqr_err", "integrator"])
    for i in range(numerical_sim_info_lists_count):
        numerical_sim_info_list = numerical_sim_info_lists[i]

        sqr_err_df = calculate_sqr_err_df_from_sim_info(steps, analytical_sim_info_list, numerical_sim_info_list)
        sqr_err_df.assign(integrator=numerical_labels[i])

        sqr_err_dfs = pd.concat([sqr_err_dfs, sqr_err_df], ignore_index=True)

    return sqr_err_dfs