from   simulation_parser import SimulationInfo
import matplotlib.pyplot as plt
import numpy as np
import os

def plot_polarization_over_iterations(simulation_info : SimulationInfo, plot_file : str):
    plot_file_split = plot_file.rsplit('/', 1)
    if len(plot_file_split) > 1:
        os.makedirs(plot_file_split[0], exist_ok=True)
    instants            = np.array(list(range(simulation_info.instant_count)))
    polarization_values = np.array(simulation_info.polarization)
    plt.plot(instants, polarization_values)
    plt.xlabel('Iteración')
    plt.ylabel('Coeficiente de orden')
    plt.savefig(plot_file, bbox_inches='tight', dpi=300)