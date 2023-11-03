import math

def get_beverloo_function(height: float, width: float, depth: float, number_of_particles: int, gravity: float, exp: str, cr: float):
    exp_type = {
        "2D" : 1.5,
        "3D" : 2.5
    }
    volume = height * width * depth
    n_p = number_of_particles / volume
    f = lambda D : n_p * math.sqrt(gravity) * ((D - cr)**exp_type[exp])
    return f