import matplotlib.pyplot as plt
import numpy as np
import math


class Particle:
    def __init__(self, x, y):
        self.x = x
        self.y = y


def read_data(dynamic_file_path, from_time, plot_to_time): # TODO: params from_time, to_time
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
    sum_zi_squared = 0
    zi_squareds = np.array([])

    for idx in range(0, len(particles[time]), 1): 
        zi_squared = calculate_zi_squared(particles[time][idx], particles[start_time][idx])
        zi_squareds = np.append(zi_squareds, zi_squared)
        sum_zi_squared = sum_zi_squared + zi_squared

    z_squared = sum_zi_squared / len(particles[time])
    std_deviation = np.std(zi_squareds)

    return z_squared, std_deviation


def generate_plot_and_return_slope(times, z_squareds, std_deviations, interpolation_to_time):
    # Interpolate the data within the specified time range
    interpolation_indices = np.where(times <= interpolation_to_time)
    interpolation_times = times[interpolation_indices]
    interpolation_z_squareds = z_squareds[interpolation_indices]

    # # Plot the data within the specified time range
    # plot_indices = np.where(times <= plot_to_time)
    # plot_times = times[plot_indices]
    # plot_z_squareds = z_squareds[plot_indices]

    # Perform linear regression on data
    coefficients = np.polyfit(interpolation_times, interpolation_z_squareds, 1)
    slope = coefficients[0]
    intercept = coefficients[1]
    z_approximations = slope * interpolation_times + intercept

    # Plot the filtered data and the linear approximation
    plt.scatter(times, z_squareds, label='z', s=5)  # TODO: ver si está bien el label
    plt.plot(interpolation_times, z_approximations, color='red', label='z: aproximación')  # TODO: ver si está bien el label
    plt.errorbar(times, z_squareds, yerr=std_deviations, fmt="o")
    # plt.bar(times, z_squareds, yerr=std_deviations, capsize=5, align='center', alpha=0.7)

    # Add labels and legend
    plt.xlabel('t') # TODO: ver si está bien el label
    plt.ylabel('z') # TODO: ver si está bien el label
    plt.legend()

    # Show the plot
    plt.show()

    return slope


def main(): # TODO: args
    # PARAMS:
    dynamic_file_path = 'dynamic.txt'
    from_time = 80
    to_time = 800
    interpolation_to_time = 550

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