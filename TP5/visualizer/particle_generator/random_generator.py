from typing import Callable
import numpy as np

def uniform_func_supplier(lower_bound: float, upper_bound: float, random_state: np.random.Generator) -> Callable[[], float]:
    
    def uniform_supplier() -> float:
        return random_state.random() * (upper_bound - lower_bound) + lower_bound
    
    return uniform_supplier