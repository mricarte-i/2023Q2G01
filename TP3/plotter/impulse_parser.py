from dataclasses import dataclass

@dataclass
class Impulses:
    instants : list[float]
    impulses : list[float]

def get_impulses_from_file(impulse_file : str) -> tuple[Impulses, Impulses]:
    container_mapping = {
        1 : 0,
        2 : 1
    }

    instants = {
        0 : [],
        1 : []
    }

    impulses = {
        0 : [],
        1 : []
    }

    with open(impulse_file) as file:
        while True:
            t = file.readline()
            if not t:
                break

            t = float(t)

            impulse_line      = file.readline()
            impulse_line_info = impulse_line.split(" ")

            impulse           = float(impulse_line_info[0])
            container         = container_mapping[int(impulse_line_info[1])]

            instants[container].append(t)
            impulses[container].append(impulse)

    cont_0_impulse_data = Impulses(instants[0], impulses[0])
    cont_1_impulse_data = Impulses(instants[1], impulses[1])

    return (cont_0_impulse_data, cont_1_impulse_data)

def generate_impulses_sequences(impulse_data_1 : Impulses, impulse_data_2 : Impulses, delta_t : float, start_t : float = 0) -> tuple[Impulses, Impulses]:
    impulse_data_1_last_t = impulse_data_1.instants[-1]
    impulse_data_2_last_t = impulse_data_2.instants[-1]
    
    last_t = min(impulse_data_1_last_t, impulse_data_2_last_t)

    impulse_data_1_sqc = impulse_sequence_generator(impulse_data_1, delta_t, start_t, last_t)
    impulse_data_2_sqc = impulse_sequence_generator(impulse_data_2, delta_t, start_t, last_t)

    return impulse_data_1_sqc, impulse_data_2_sqc


def impulse_sequence_generator(impulse_data : Impulses, delta_t : float, start_t : float = 0, end_t : float = None) -> Impulses:
    if end_t is None:
        end_t = impulse_data.instants[-1]

    instant_count = len(impulse_data.instants)

    sqc_instants = [0.0]
    sqc_impulses = [0.0]

    curr_idx = 0
    while curr_idx < instant_count and impulse_data.instants[curr_idx] < start_t:
        curr_idx += 1
    
    curr_t   = start_t
    
    while curr_t < end_t:
        next_t = curr_t + delta_t

        accum_impulse = 0.0
        while curr_idx < instant_count and impulse_data.instants[curr_idx] <= next_t:
            accum_impulse += impulse_data.impulses[curr_idx]
            curr_idx      += 1

        if next_t <= end_t:
            sqc_instants.append(next_t)
            sqc_impulses.append(accum_impulse)

        curr_t = next_t

    return Impulses(sqc_instants, sqc_impulses)

def impulse_sum_with_equal_timestamps(impulse_data_1 : Impulses, impulse_data_2 : Impulses) -> Impulses:
    instants = impulse_data_1.instants.copy()
    impulses = []
    for i in range(len(instants)):
        curr_impulse = impulse_data_1.impulses[i] + impulse_data_2.impulses[i]
        impulses.append(curr_impulse)

    return Impulses(instants, impulses)