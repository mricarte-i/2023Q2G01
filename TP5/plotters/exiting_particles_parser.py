
def exiting_particle_times_from_file(filename: str) -> list[float]:
    exiting_particle_times = []
    with open(filename) as file:
        while True:
            t = file.readline()
            if not t:
                break
            t = float(t)
            exiting_particle_times.append(t)
    return exiting_particle_times

def exiting_particle_times_from_multiple_files(files: list[str]) -> list[list[float]]:
    timestamps_list = []
    for file in files:
        timestamps = exiting_particle_times_from_file(file)
        timestamps_list.append(timestamps)
    return timestamps_list