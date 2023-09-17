import numpy as np
import seaborn as sns
import matplotlib.pyplot as plt
from typing      import Callable, Any
from dataclasses import dataclass

def __make_column_mat__(X : np.ndarray) -> np.ndarray:
    return X.reshape((X.size, 1))

def __evaluate_X_in_K_fs__(X : np.ndarray, K_fs : list[Callable[[Any], Any]]) -> np.ndarray:
    K_fs_len = len(K_fs)
    f_X      = np.zeros((X.size, K_fs_len))

    for i in range(K_fs_len):
        vec_f = np.vectorize(pyfunc=K_fs[i])
        
        f_X[:, i].flat = vec_f(X)

    return f_X

def min_sqrs(X : np.ndarray, K_fs : list[Callable[[Any], Any]], Y : np.ndarray) -> np.ndarray:
    X    = __make_column_mat__(X)
    Y    = __make_column_mat__(Y)

    f_X   = __evaluate_X_in_K_fs__(X, K_fs)
    f_X_t = f_X.transpose()

    X_sqr   = f_X_t.dot(f_X) 
    p_inv_X = np.linalg.inv(X_sqr)

    K = p_inv_X.dot(f_X.T).dot(Y)

    return K
    
def square_error(Y : np.ndarray, F : np.ndarray) -> float:
    E     = Y - F
    E     = E * E
    error = E.sum()
    return error 

def mean_square_error(Y : np.ndarray, F : np.ndarray) -> float:
    return square_error(Y, F) / Y.size

@dataclass
class RegressionData:
    best_k_idx : int
    Ks         : np.ndarray
    K_errs     : np.ndarray


def perform_regression(X : np.ndarray, Ks : np.ndarray, f : Callable[[Any, Any], Any], Y : np.ndarray) -> tuple[np.ndarray, RegressionData]:
    best_k_idx = None
    K_errs     = []
    min_err    = None
    min_F      = None
    
    for i in range(Ks.size):
        f_k     = lambda x : f(x, Ks[i])
        f_k_vec = np.vectorize(f_k)
        F       = f_k_vec(X)

        err     = square_error(Y, F)
        K_errs.append(err)

        if min_err is None or err < min_err:
            best_k_idx = i
            min_err    = err
            min_F      = F
    
    return min_F, RegressionData(best_k_idx, Ks, K_errs)

def plot_regression_data(reg_data : RegressionData, file_name : str, x_label : str = "Pendiente") -> None:
    plt.figure()
    sns.set_style("darkgrid")

    data_range = min(reg_data.best_k_idx, reg_data.Ks.size - reg_data.best_k_idx)
    plt.plot(reg_data.Ks[reg_data.best_k_idx-data_range:reg_data.best_k_idx+data_range+1], reg_data.K_errs[reg_data.best_k_idx-data_range:reg_data.best_k_idx+data_range+1])
    plt.ylabel("Error cuadrático")
    plt.xlabel(x_label)
    plt.savefig(file_name, bbox_inches='tight', dpi=1200)
    