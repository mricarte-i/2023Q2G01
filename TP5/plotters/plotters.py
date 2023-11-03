import matplotlib.pyplot as plt
from matplotlib.ticker import MaxNLocator
import datetime
import numpy as np
import scipy.stats as scst

def run_plots():
    # timestamps = parse_timestamps()
    plot_exiting_particles_vs_t(timestamps)
    # t_est = x
    plot_q_vs_t(timestamps, t_est)
    # ...

def get_first_timestap_after_idx(time: float, timestamps: list[float]) -> int:
    i = 0
    for timestamp in timestamps:
        if timestamp >= time:
            break
        i = i + 1
    return i

def get_caudal(timestamps: list[float], t_est: float):
    accumulated_values = list(range(1, len(timestamps) + 1))

    i = 0
    for timestamp in timestamps:
        if timestamp >= t_est:
            t_est_index = i
            break
        i = i + 1

    timestamps_from_stationary = timestamps[t_est_index:]
    accumulated_values_from_stationary = accumulated_values[t_est_index:]

    x = np.array(timestamps_from_stationary)
    y = np.array(accumulated_values_from_stationary)
    result = scst.linregress(x, y) #scipy.stats.lingress(x, y)
    return result.slope, result.stderr
"""
    slope, V = np.polyfit(x, y, 1, cov=True)

    stddev = []
    #V es la matriz de covarianza
    #la desv estandar para slope[j] es sqrt(V[j][j])
    for j in range(len(slope)):
        stddev.append(np.sqrt(V[j][j]))

    return slope, stddev """




def get_timestamps(fileName):
    timestamps = []
    with open(fileName) as file:
        for line in file:
            timestamps.append(float(line))
    return timestamps


def plot_exiting_particles_vs_t_for_multiple_ws(timestamps: list[list[float]], t_ests: list[float], ws: list[int], colors: list[str], save_to: str, perform_regression=False):
    # TODO: unhardcode
    #timestamps_w_5 = [0.1, 0.2, 0.3, 0.5, 0.6, 0.7, 0.71, 0.745, 0.77, 0.779, 0.8, 0.81, 0.84]
    #t_est_w_5 = 0.7
    #timestamps_w_10 = [0.14, 0.23, 0.36, 0.57, 0.61, 0.72, 0.73, 0.735, 0.75, 0.77, 0.8, 0.84, 0.89]
    #t_est_w_10 = 0.7
    # timestamps_w_5 = get_timestamps('exitingParticles_w5.txt')
    # t_est_w_5 = 22 # TODO: set correct value
    # ...

    plt.xlabel('Tiempo (s)')
    plt.ylabel('Número de partículas descargadas')

    #plot_exiting_particles_vs_t(plt, timestamps_w_5, t_est_w_5, 'red', 'w = 5')

    # TODO: quitar si vamos a hacer todas las curvas en un mismo gráfico.
    # Si se hace un gráfico por curva, entonces poner un plt.show() entre cada llamada
    # a plot_exiting_particles_vs_t(...)
    # plt.show()

    #plot_exiting_particles_vs_t(plt, timestamps_w_10, t_est_w_10, 'blue', 'w = 10')

    w_caudales = dict()

    for i in range(len(ws)):
        if perform_regression:
            Q, errs = plot_exiting_particles_vs_t(plt, timestamps[i], t_ests[i], colors[i], 'w = ' + str(ws[i]), perform_regression=True)
            w_caudales[ws[i]] = (Q, errs)
        else:
            plot_exiting_particles_vs_t(plt, timestamps[i], t_ests[i], colors[i], 'w = ' + str(ws[i]), perform_regression=False)

    plt.legend(bbox_to_anchor=(1.04, 1), loc="upper left")
    plt.savefig(save_to, bbox_inches='tight', dpi=1200, facecolor='white')
    return w_caudales
    #plt.show()

from min_sqrs import min_sqrs, perform_regression

def plot_regression(timestamps: list[float], accumulated_values: list[float], t_est: float, color: str) -> tuple[int, np.ndarray]:
    t_est_idx = get_first_timestap_after_idx(t_est, timestamps)

    X = np.array(timestamps[t_est_idx:])-timestamps[t_est_idx]
    Y = np.array(accumulated_values[t_est_idx:])-accumulated_values[t_est_idx]
    F = [lambda x : x]

    k = min_sqrs(X, F, Y)
    k = k.item()

    Y = Y + accumulated_values[t_est_idx]
    k = round(k, 3)

    k_int = (0, k*2)
    K_s = np.arange(k_int[0], k_int[1] + 0.001, step=0.001)
    min_f, reg_data, min_E = perform_regression(X, K_s, lambda x, c : c*x + Y[0], Y)

    print(k, reg_data.Ks[reg_data.best_k_idx])

    O = np.apply_along_axis(lambda x : k*x + Y[0], 0, X)

    plt.plot(X+timestamps[t_est_idx], O, linestyle="--", color=color, label="Q = {}".format(k))

    return k, min_E

# timestamps = [...], t_est = x
def plot_exiting_particles_vs_t(plt, timestamps, t_est, color, label, perform_regression=False):
    accumulated_values = list(range(1, len(timestamps) + 1))

    plt.plot(timestamps, accumulated_values, color=color, label=label)

    if perform_regression:

        Q, errs = plot_regression(timestamps, accumulated_values, t_est, color)

        return Q, errs
    
    return None
    """
    LO CAMBIE POR REGRESION

    i = 0
    for timestamp in timestamps:
        if timestamp >= t_est:
            t_est_index = i
            break
        i = i + 1

    timestamps_from_stationary = timestamps[t_est_index:]
    accumulated_values_from_stationary = accumulated_values[t_est_index:]

    x = np.array(timestamps_from_stationary)
    y = np.array(accumulated_values_from_stationary)
    slope = np.polyfit(x, y, 1)
    slope_poly = np.poly1d(slope)
    plt.plot(timestamps_from_stationary, slope_poly(timestamps_from_stationary), color=color, alpha=0.5, linestyle='dashed', label='Aprox. lineal para ' + label)
    """


# timestamps = [...], t_est = x
def plot_q_vs_t(timestamps, t_est):
    return


# q = [...], w = [5, 10, 15, 20, 30, 50]
def plot_q_vs_w(q: list[float], q_errors: list[float], w: list[int], save_to: str, join_ponts: bool = False):
    # TODO: unhardcode
    #w = [5, 10, 15, 20, 30, 50]
    #q = [20, 18, 16, 17, 19, 22]
    #q_errors = [1.1, 0.8, 0.4, 0.5, 0.7, 1.5]

    # TODO: quitar si vamos a hacer todos los puntos de un solo color
    # colors = ['red', 'green', 'blue', 'yellow', 'purple', 'grey']
    # for i in range(len(w)):
    #     plt.errorbar(w[i], q[i], yerr=q_errors[i], fmt='o', color=colors[i], label=f'w = {w[i]}')
    # plt.legend()

    # TODO: quitar si vamos a hacer todos los puntos de distintos colores
    if join_ponts:
        plt.errorbar(w, q, yerr=q_errors, fmt='o', linestyle="-")
    else:
        plt.errorbar(w, q, yerr=q_errors, fmt='o')
    #plt.plot(w, q, marker="o", linestyle="")

    plt.xlabel('Vibración del silo')
    plt.ylabel('Caudal')
    #plt.title('Caudal en función de omega')

    plt.savefig(save_to, bbox_inches='tight', dpi=1200, facecolor='white')
    #plt.show()


# q = [...], d = [4, 5, 6]
def plot_q_vs_d(q: list[float], q_errors: list[float], D: list[int], w: int, save_to: str):
    # TODO: unhardcode
    #D = [4, 5, 6]
    #q = [20, 18, 16]
    #q_errors = [1.1, 0.8, 1.4]

    # TODO: quitar si vamos a hacer todos los puntos de un solo color
    # colors = ['red', 'green', 'blue', 'yellow', 'purple', 'grey']
    # for i in range(len(D)):
    #     plt.errorbar(D[i], q[i], yerr=q_errors[i], fmt='o', color=colors[i], label=f'D = {D[i]}')
    # plt.legend()

    # TODO: quitar si vamos a hacer todos los puntos de distintos colores
    plt.errorbar(D, q, yerr=q_errors, fmt='o')

    plt.xlabel('D')
    plt.ylabel('Q')
    plt.title('Caudal en función de la longitud de la apertura para w = ' + str(w))

    plt.savefig(save_to, bbox_inches='tight', dpi=1200, facecolor='white')
    #plt.show()

# TODO: ajustar parametros
#plot_exiting_particles_vs_t_for_multiple_ws()
#plot_q_vs_w('a', 'a')
#plot_q_vs_d('a', 'a')