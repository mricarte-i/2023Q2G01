import matplotlib.pyplot as plt
import numpy as np
import math
from min_sqrs import min_sqrs, perform_regression, plot_regression_data


class Particle:
    def __init__(self, x, y):
        self.x = x
        self.y = y


def read_data(dynamic_file_path, from_time, plot_to_time):
    times = np.array([])
    particles = {}
    save = False
    
    with open(dynamic_file_path) as file:
        while(True):
            l = file.readline()
            if not l:
                break
            l_splitted = l.split(' ')
            if len(l_splitted) == 1:
                if float(l_splitted[0]) >= from_time:
                    save = True
                if float(l_splitted[0]) >= plot_to_time:
                    return times, particles
                if save:
                    print(f'New timestamp: {l_splitted[0]}')
                    times = np.append(times, float(l_splitted[0])) 
            else:
                if save:
                    if times[-1] in particles:
                        particles[times[-1]] = np.append(particles[times[-1]], Particle(float(l_splitted[0]), float(l_splitted[1])))
                    else:
                        particles[times[-1]] = [Particle(float(l_splitted[0]), float(l_splitted[1]))]

    return times, particles


def calculate_zi_squared(particle, particleAtOrigin):
    return math.pow(particle.x - particleAtOrigin.x, 2) + math.pow(particle.y - particleAtOrigin.y, 2)


def calculate_z_squared(particles, time, start_time):
    zi_squareds = np.array([])

    for idx in range(0, len(particles[time]), 1): 
        zi_squared = calculate_zi_squared(particles[time][idx], particles[start_time][idx])
        zi_squareds = np.append(zi_squareds, zi_squared)
    
    std_deviation = np.std(zi_squareds)
    z_squared = np.mean(zi_squareds)

    return z_squared, std_deviation


def generate_plot_and_return_slope(times, z_squareds, std_deviations, interpolation_to_time):
    # Interpolate the data within the specified time range
    interpolation_indices = np.where(times <= interpolation_to_time)
    interpolation_times = times[interpolation_indices]
    interpolation_z_squareds = z_squareds[interpolation_indices]

    # Perform linear regression on data
    # coefficients = np.polyfit(interpolation_times, interpolation_z_squareds, 1)
    coefficients = min_sqrs(interpolation_times - interpolation_times[0], [lambda x:x], interpolation_z_squareds)
    slope = coefficients[0][0]
    # intercept = coefficients[1]
    # z_approximations = slope * interpolation_times + intercept
    z_approximations = slope * (interpolation_times - interpolation_times[0])

    # Plot the filtered data and the linear approximation
    plt.scatter(times, z_squareds, label='Coeficiente de difusión', s=1, color='teal')
    plt.plot(interpolation_times, z_approximations, color='red', label='Coeficiente de difusión - regresión lineal')
    plt.fill_between(times, z_squareds-std_deviations, z_squareds+std_deviations, alpha=0.2, color='teal')
    # plot_sqr_err_regression(interpolation_times - interpolation_times[0], interpolation_z_squareds)

    # Add labels and legend
    plt.xlabel('Tiempo (s)')
    plt.ylabel('Coeficiente de difusión (m²)')
    plt.legend(loc='upper left')

    # Show the plot
    plt.show()


    return slope


def plot_sqr_err_regression(X : np.ndarray, Y : np.ndarray) -> float:
    plt.figure()
    Ks = np.arange(0, 5, 0.001)
    _, reg_data = perform_regression(X, Ks, lambda x, c : c*x, Y)
    plot_regression_data(reg_data, "z_regression_sqr_err.png")
    return reg_data.Ks[reg_data.best_k_idx]


def main():
    # PARAMS:
    dynamic_file_path = 'dynamic.txt'
    from_time = 80
    to_time = 450
    interpolation_to_time = 200

    print('Reading data...')
    times, particles = read_data(dynamic_file_path, from_time, to_time)

    print('Calculating z_squareds...')
    z_squareds = np.array([])
    std_deviations = np.array([])

    for time in times:
        z_squared, std_deviation = calculate_z_squared(particles, time, times[0])
        z_squareds = np.append(z_squareds, z_squared) 
        std_deviations = np.append(std_deviations, std_deviation) 

    print('Generating plot and returning slope...')
    slope = generate_plot_and_return_slope(times, z_squareds, std_deviations, interpolation_to_time)

    print(f'Diffusion coeficient: {slope/4}')


if __name__ == "__main__":
    main()

from min_sqrs import perform_regression, plot_regression_data

def plot_sqr_err_regression(X : np.ndarray, Y : np.ndarray) -> float:
    plt.figure()
    Ks = np.arange(3, 4, 0.001)
    _, reg_data = perform_regression(X, Ks, [lambda x, c : c*x], Y)
    plot_regression_data(reg_data, "z_regression_sqr_err.png")
    return reg_data.Ks[reg_data.best_k_idx]