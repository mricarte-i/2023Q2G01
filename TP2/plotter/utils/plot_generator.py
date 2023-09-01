from   utils.simulation_parser import SimulationInfo, parse_simulation_files
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import seaborn as sns
import os

def plot_polarization_over_iterations_from_files(simulations_static_files : list[str], simulations_dynamic_files : list[str], simulations_polarization_files : list[str], plot_file : str, colors : list[str], noises : list[float], min_iter : float, max_iter : float):
    static_files_len        = len(simulations_static_files)
    dynamic_files_len       = len(simulations_dynamic_files)
    polarization_files_len  = len(simulations_polarization_files)
    if static_files_len != dynamic_files_len or static_files_len != polarization_files_len or static_files_len != dynamic_files_len : raise ValueError("static, dynamic and polarization files must be of equal length. Currently: \n\"static\": {}\n\"dynamic\": {}\n\"polarization\": {}".format(static_files_len, dynamic_files_len, polarization_files_len))
    simulations_info = []
    for i in range(static_files_len):
        simulations_info.append(parse_simulation_files(simulations_static_files[i], simulations_dynamic_files[i], simulations_polarization_files[i]))
    plot_polarization_over_iterations(simulations_info, plot_file, colors, noises, min_iter, max_iter)

def plot_polarization_over_iterations(simulations_info : list[SimulationInfo], plot_file : str, colors : list[str], noises : list[float], min_iter : float, max_iter : float):
    plot_file_split = plot_file.rsplit('/', 1)
    if len(plot_file_split) > 1:
        os.makedirs(plot_file_split[0], exist_ok=True)
    plt.figure()
    sns.set_style("darkgrid")
    plt.plot([], [], ' ', label="L = {}".format(int(simulations_info[0].L)))
    plt.plot([], [], ' ', label="N = {}".format(int(simulations_info[0].N)))
    plt.plot([], [], ' ', label=" ")
    plt.plot([], [], ' ', label="Ruido")
    i = 0
    for simulation_info in simulations_info:
        instants            = np.array(list(range(simulation_info.instant_count)))
        polarization_values = np.array(simulation_info.polarization)
        add_polarization_over_iterations_plot(instants, polarization_values, colors[i], "{:.2g}".format(noises[i]) + "π")
        i += 1     
    plt.ylim((0, 1.05))
    plt.xlim((min_iter, max_iter))
    plt.legend(bbox_to_anchor=(1.04, 1), loc="upper left")
    plt.savefig(plot_file, bbox_inches='tight', dpi=1200)

def add_polarization_over_iterations_plot(instants : np.ndarray, polarization_values : np.ndarray, color : str, label : str):
    df = pd.DataFrame(data={
        "Iteración"            : instants,
        "Coeficiente de orden" : polarization_values
    })
    sns.lineplot(data=df, x="Iteración", y="Coeficiente de orden", color=color, label=label)

def plot_order_over_noise(simulation_df : pd.DataFrame):
    plt.figure()
    sns.set_style("darkgrid")
    Ns = simulation_df["N"].unique()
    cm = plt.get_cmap('jet')
    i = 0
    print("Holis")
    plt.plot([], [], ' ', label="N")
    for N in Ns:
        order_over_noise_df = simulation_df[simulation_df["N"] == N][["eta", "va", "iter"]]
        ax = sns.pointplot(data=order_over_noise_df, x="eta", y="va", errorbar="se", capsize=.1, label=N, join=False, color=cm(1.*i/len(Ns)))
        #plt.setp(ax.collections, alpha=.3)
        #plt.setp(ax.lines, alpha=.5)
        i += 1
    plt.xlabel('Ruido')
    plt.ylabel('Coeficiente de orden')
    plt.ylim((0, 1.05))
    plt.legend(bbox_to_anchor=(1.04, 1), loc="upper left")
    plt.savefig("order_over_noise.png", bbox_inches='tight', dpi=1200)

def plot_order_over_density(simulation_df : pd.DataFrame):
    plt.figure()
    sns.set_style("darkgrid")
    etas = simulation_df["eta"].unique()
    cm = plt.get_cmap('jet')
    i = 0
    print("Holis")
    plt.plot([], [], ' ', label="N = {}".format(int(simulation_df["N"].values[0])))
    plt.plot([], [], ' ', label=" ")
    plt.plot([], [], ' ', label="Ruido")
    for eta in etas:
        order_over_noise_df = simulation_df[simulation_df["eta"] == eta][["density", "va", "iter"]]
        ax = sns.lineplot(data=order_over_noise_df, x="density", y="va", errorbar="se", label=eta, color=cm(1.*i/len(etas)), marker='o', err_style="bars", err_kws={'capsize':0.1})
        #plt.setp(ax.collections, alpha=.3)
        #plt.setp(ax.lines, alpha=.5)
        i += 1
    plt.xlabel('Densidad')
    plt.ylabel('Coeficiente de orden')
    plt.ylim((0, 1.05))
    plt.legend(bbox_to_anchor=(1.04, 1), loc="upper left")
    plt.savefig("order_over_density.png", bbox_inches='tight', dpi=1200)