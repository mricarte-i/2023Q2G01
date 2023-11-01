from particle_snapshot import ParticleSnapshot

def write_static_file(filename: str, particle_snapshots: list[ParticleSnapshot], W: float, L: float, D: float, w: float) -> None:
    N = len(particle_snapshots)
    with open(filename, "w") as file:
        file.write("{} \n".format(N))
        file.write("{} \n".format(W))
        file.write("{} \n".format(L))
        file.write("{} \n".format(D))
        file.write("{} \n".format(w))
        for particle_snapshot in particle_snapshots:
            file.write("{} \n".format(particle_snapshot.static_info()))

def write_dynamic_file(filename: str, particle_snapshots: list[ParticleSnapshot], deltaT: float, time_bounds: tuple[float, float]) -> None:
    with open(filename, "w") as file:
        time = time_bounds[0]
        end_time = time_bounds[1]

        while time <= end_time:
            file.write("{} \n".format(time))
            for particle_snapshot in particle_snapshots:
                file.write("{} \n".format(particle_snapshot.dynamic_info()))

            time += deltaT