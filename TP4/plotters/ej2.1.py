# Para N = 5, 10, 15, 20, 25, 30, calcular la velocidad promedio del sistema en función del 
# tiempo y luego el promedio temporal en el estacionario como función de N.

from simulation_parser import parse_static_file, parse_dynamic_file
import math
import matplotlib.pyplot as plt
import statistics
import seaborn as sns

N_LIST = [5, 10, 15, 20, 25, 30] 
STATIONARY_TIMESTAMPS = {
    5: 0, 
    10: 0, 
    15: 0, 
    20: 0, 
    25: 0, 
    30: 0
}

velocities = {}
# {
#     n5: {
#         t0: [va, vb, ...],
#         t1: [va, vb, ...],
#         ...
#     }, 
#     n10: {
#         t0: [va, vb, ...],
#         t1: [va, vb, ...],
#         ...
#     }, 
#     ...
# }
average_velocities = {}
# {
#     n5: {
#         t0: v0,
#         t1: v1,
#         ...
#     }, 
#     n10: {
#         t0: v0,
#         t1: v1,
#         ...
#     }, 
#     ...
# }
average_stationary_velocities_and_std_dev = {}
# {
#     n5: (v, std_dev), 
#     n10: (v, std_dev), 
#     ...
# }


def parse_dynamic_file():

    for N in N_LIST:
        dynamic_file_path = './dynamic-N' + str(N) + '.txt'

        if N not in velocities:
            velocities[N] = {}

        with open(dynamic_file_path) as file:
            while(True):
                t = file.readline()
                if not t:
                    break
                timestamp = float(t)

                velocities[N][timestamp] = []

                for _ in range(N):
                    particle_info = file.readline()
                    particle_info = particle_info.split(" ")
                    particle_info = list(filter(lambda x : x != '', particle_info))

                    # velocities[N][timestamp].append((float(particle_info[2]), float(particle_info[3])))
                    velocities[N][timestamp].append(float(particle_info[2]))


def calculate_average_instant_velocity():
    for N in N_LIST:
        average_velocities[N] = {}

        for timestamp in velocities[N].keys():
            # velocities_in_timestamp = velocities[N][timestamp]

            # velocity_modules_sum = 0

            # for velocity in velocities_in_timestamp:
            #     # velocity_modules_sum += math.sqrt(velocity_tuple[0]**2 + velocity_tuple[1]**2)
            #     velocity_modules_sum += velocity

            # average_velocities[N][timestamp] = velocity_modules_sum / len(velocities_in_timestamp)
            average_velocities[N][timestamp] = statistics.mean(velocities[N][timestamp])


def plot_avg_velocity_vs_time():
    sns.set_style("darkgrid")
    
    for N in N_LIST:
        average_velocities[N] = {timestamp: velocity for timestamp, velocity in average_velocities[N].items() if timestamp <= 180}
        plt.plot((average_velocities[N]).keys(), average_velocities[N].values(), label='N = ' + str(N))

    plt.xlabel('Tiempo (s)')
    plt.ylabel('Velocidad promedio ($\\frac{cm}{s}$)')

    plt.legend(bbox_to_anchor=(1.04, 1), loc="upper left")
    plt.savefig("2_1_avg_v_time.png", bbox_inches='tight', dpi=1200)

    plt.show()


def calculate_average_stationary_velocities_and_std_dev():
    filtered_average_velocities = {}

    for N in N_LIST:
        filtered_average_velocities[N] = {timestamp: velocity for timestamp, velocity in average_velocities[N].items() if timestamp > STATIONARY_TIMESTAMPS[N]}

        average_stationary_velocities_and_std_dev[N] = (statistics.mean(filtered_average_velocities[N].values()), statistics.stdev(average_velocities[N].values()))


def plot_avg_stationary_velocities_and_std_dev():
        sns.set_style("darkgrid")
        plt.plot(average_stationary_velocities_and_std_dev.keys(), [item[0] for item in average_stationary_velocities_and_std_dev.values()], marker="o")
        #plt.errorbar(average_stationary_velocities_and_std_dev.keys(), [item[0] for item in average_stationary_velocities_and_std_dev.values()], yerr=[item[1] for item in average_stationary_velocities_and_std_dev.values()], fmt="o")

        plt.xlabel('Número de partículas')
        plt.ylabel('Velocidad promedio estacionaria ($\\frac{cm}{s}$)')
        plt.savefig("2_1_avg_v_est_n.png", bbox_inches='tight', dpi=1200)

        plt.show()


parse_dynamic_file()
calculate_average_instant_velocity()
plot_avg_velocity_vs_time()
calculate_average_stationary_velocities_and_std_dev()
plot_avg_stationary_velocities_and_std_dev()