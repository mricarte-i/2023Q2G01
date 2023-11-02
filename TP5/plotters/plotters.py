import matplotlib.pyplot as plt
import datetime
import numpy as np

def run_plots():
    # timestamps = parse_timestamps()
    plot_exiting_particles_vs_t(timestamps)
    # t_est = x
    plot_q_vs_t(timestamps, t_est)
    # ...


def get_timestamps(fileName):
    timestamps = []
    with open(fileName) as file:
        for line in file:
            timestamps.append(float(line))
    return timestamps


def plot_exiting_particles_vs_t_for_multiple_ws():
    # TODO: unhardcode
    timestamps_w_5 = [0.1, 0.2, 0.3, 0.5, 0.6, 0.7, 0.71, 0.745, 0.77, 0.779, 0.8, 0.81, 0.84]
    t_est_w_5 = 0.7
    timestamps_w_10 = [0.14, 0.23, 0.36, 0.57, 0.61, 0.72, 0.73, 0.735, 0.75, 0.77, 0.8, 0.84, 0.89]
    t_est_w_10 = 0.7
    # timestamps_w_5 = get_timestamps('exitingParticles_w5.txt')
    # t_est_w_5 = 22 # TODO: set correct value
    # ...

    plt.xlabel('t')
    plt.ylabel('n')
    plt.title('Descarga en función del tiempo')

    plot_exiting_particles_vs_t(plt, timestamps_w_5, t_est_w_5, 'red')
    
    # TODO: quitar si vamos a hacer todas las curvas en un mismo gráfico.
    # Si se hace un gráfico por curva, entonces poner un plt.show() entre cada llamada
    # a plot_exiting_particles_vs_t(...)
    # plt.show()
   
    plot_exiting_particles_vs_t(plt, timestamps_w_10, t_est_w_10, 'blue')
    plt.show()


# timestamps = [...], t_est = x
def plot_exiting_particles_vs_t(plt, timestamps, t_est, color):
    accumulated_values = list(range(1, len(timestamps) + 1))

    plt.plot(timestamps, accumulated_values, color=color)

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
    plt.plot(timestamps_from_stationary, slope_poly(timestamps_from_stationary), color='grey')


# timestamps = [...], t_est = x
def plot_q_vs_t(timestamps, t_est):
    return


# q = [...], w = [5, 10, 15, 20, 30, 50]
def plot_q_vs_w(q, w):
    # TODO: unhardcode
    w = [5, 10, 15, 20, 30, 50]
    q = [20, 18, 16, 17, 19, 22]
    q_errors = [1.1, 0.8, 0.4, 0.5, 0.7, 1.5]
    
    # TODO: quitar si vamos a hacer todos los puntos de un solo color
    # colors = ['red', 'green', 'blue', 'yellow', 'purple', 'grey']
    # for i in range(len(w)):
    #     plt.errorbar(w[i], q[i], yerr=q_errors[i], fmt='o', color=colors[i], label=f'w = {w[i]}')
    # plt.legend()
    
    # TODO: quitar si vamos a hacer todos los puntos de distintos colores
    plt.errorbar(w, q, yerr=q_errors, fmt='o')

    plt.xlabel('w')
    plt.ylabel('Q')
    plt.title('Caudal en función de omega')

    plt.show()


# q = [...], d = [4, 5, 6]
def plot_q_vs_d(q, d):
    # TODO: unhardcode
    D = [4, 5, 6]
    q = [20, 18, 16]
    q_errors = [1.1, 0.8, 1.4]
    
    # TODO: quitar si vamos a hacer todos los puntos de un solo color
    # colors = ['red', 'green', 'blue', 'yellow', 'purple', 'grey']
    # for i in range(len(D)):
    #     plt.errorbar(D[i], q[i], yerr=q_errors[i], fmt='o', color=colors[i], label=f'D = {D[i]}')
    # plt.legend()
    
    # TODO: quitar si vamos a hacer todos los puntos de distintos colores
    plt.errorbar(D, q, yerr=q_errors, fmt='o')

    plt.xlabel('D')
    plt.ylabel('Q')
    plt.title('Caudal en función de la longitud de la apertura para w = X')

    plt.show()

# TODO: ajustar parametros
plot_exiting_particles_vs_t_for_multiple_ws()
plot_q_vs_w('a', 'a')
plot_q_vs_d('a', 'a')