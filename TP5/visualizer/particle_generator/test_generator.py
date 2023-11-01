from argument_parser import parse_arguments
from random_generator import uniform_func_supplier
from particle_snapshot import generate_particle_snapshots
from sim_info_writers import write_static_file, write_dynamic_file

import numpy as np

L = 70
W = 20
N = 200
A = 0.15
time_bounds = (0, 10)

D = 3
w = 5
seed = 2
deltaT = 10**-3

radii_bounds = (0.85, 1.15)

static_filename, dynamic_filename = parse_arguments()

random_state = np.random.default_rng(seed=seed)
radii_supplier_f = uniform_func_supplier(radii_bounds[0], radii_bounds[1], random_state)
particle_snapshots = generate_particle_snapshots(N, (0, W), (A, L), radii_supplier_f, random_state)

write_static_file(static_filename, particle_snapshots, W, L, D, w)
write_dynamic_file(dynamic_filename, particle_snapshots, deltaT, time_bounds)