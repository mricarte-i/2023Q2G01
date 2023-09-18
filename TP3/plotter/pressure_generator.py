from impulse_parser import Impulses
from dataclasses    import dataclass

@dataclass
class Pressures:
    instants  : list[float]
    pressures : list[float]

def get_pressures(impulses : Impulses, delta_t : float, perimeter : float) -> Pressures:
    instants  = impulses.instants
    pressures = []

    denominator = delta_t * perimeter

    for impulse in impulses.impulses:
        pressure = impulse / denominator
        pressures.append(pressure)

    return Pressures(instants, pressures)

def avg_pressure(pressures : Pressures, from_t : float, to_t : float) -> float:
    instants = pressures.instants

    instant_count = len(instants)

    curr_idx = 0
    while curr_idx < instant_count and pressures.instants[curr_idx] < from_t:
        curr_idx += 1

    curr_t = from_t

    pressure_accum   = 0
    pressure_samples = 0

    while curr_t <= to_t:
        pressure_accum   += pressures.pressures[curr_idx]
        pressure_samples += 1

        curr_idx += 1
        if curr_idx >= instant_count:
            break

        curr_t = pressures.instants[curr_idx]

    return pressure_accum / pressure_samples

def get_pressures_between(pressures : Pressures, from_t : float, to_t : float) -> Pressures:
    instants = pressures.instants

    instant_count = len(instants)

    curr_idx = 0
    while curr_idx < instant_count and pressures.instants[curr_idx] < from_t:
        curr_idx += 1

    curr_t = from_t

    between_instants  = []
    between_pressures = []

    while curr_t <= to_t:
        between_instants.append(pressures.instants[curr_idx])
        between_pressures.append(pressures.pressures[curr_idx])

        curr_idx += 1
        if curr_idx >= instant_count:
            break

        curr_t = pressures.instants[curr_idx]

    return Pressures(between_instants, between_pressures)