# Para N = 5, 10, 15, 20, 25, 30, calcular la velocidad promedio del sistema en función del 
# tiempo y luego el promedio temporal en el estacionario como función de N.

from simulation_parser import parse_static_file, parse_dynamic_file
import math
import matplotlib.pyplot as plt
import statistics

N_LIST = [5, 10, 15, 20, 25, 30] 
STATIONARY_TIMESTAMPS = [25, 25, 25, 25, 25, 25] # TODO: adjust values

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
        dynamic_file_path = './plotters/dynamic-N' + str(N) + '.txt'

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
    for N in N_LIST:
        plt.plot(average_velocities[N].keys(), average_velocities[N].values(), label='N = ' + str(N))

    plt.xlabel('t')
    plt.ylabel('avg_v')

    plt.title('avg_v vs. t')
    plt.legend(loc='upper left')

    plt.show()


def calculate_average_stationary_velocities_and_std_dev():
    for N in N_LIST:
        filtered_average_velocities = {timestamp: velocity for timestamp, velocity in average_velocities[N].items() if timestamp > STATIONARY_TIMESTAMPS[N]}

        average_stationary_velocities_and_std_dev[N] = (statistics.mean(filtered_average_velocities[N].values()), statistics.stdev(average_velocities[N].values()))
        # velocity_sum = 0

        # for v in average_velocities[N].values():
        #     velocity_sum += v

        # average_stationary_velocities_and_std_dev[N] = (velocity_sum / len(average_velocities[N]), statistics.stdev(average_velocities[N].values()))


def plot_avg_stationary_velocities_and_std_dev():
        plt.scatter(average_stationary_velocities_and_std_dev.keys(), [item[0] for item in average_stationary_velocities_and_std_dev.values()])
        plt.errorbar(average_stationary_velocities_and_std_dev.keys(), [item[0] for item in average_stationary_velocities_and_std_dev.values()], yerr=[item[1] for item in average_stationary_velocities_and_std_dev.values()], fmt="o")

        plt.xlabel('n')
        plt.ylabel('avg_stationary_v')

        plt.title('avg_stationary_v vs. n')

        plt.show()


parse_dynamic_file()
calculate_average_instant_velocity()
plot_avg_velocity_vs_time()
calculate_average_stationary_velocities_and_std_dev()
plot_avg_stationary_velocities_and_std_dev()