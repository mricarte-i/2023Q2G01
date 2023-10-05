import seaborn as sns
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np

def plot_oscillator_step(sqr_err_df : pd.DataFrame, filename : str = "oscillator_step.png", xlim : tuple[float, float] = None, ylim : tuple[float, float] = None, hue : str = None) -> None:
    sns.set_style("darkgrid")
    if hue is None:
        sns.lineplot(data=sqr_err_df, x="step", y="sqr_err", marker="o", ci=None, color="blue", label="")
    else:
        sns.lineplot(data=sqr_err_df, x="step", y="sqr_err", marker="o", ci=None, hue=hue)
    plt.xlabel("Paso temporal (s)")
    plt.ylabel("Error cuadrático medio ($cm^{2}$)")

    plt.xscale("log")
    plt.yscale("log")

    if xlim is not None:
        plt.xlim(xlim)

    if ylim is not None:
        plt.ylim(ylim)

    if hue is not None:
        plt.legend(bbox_to_anchor=(1.04, 1), loc="upper left")

    plt.savefig(filename, bbox_inches='tight', dpi=1200)

def __plot_single_pdf__(values: np.ndarray, pdf : np.ndarray, label : str = None) -> None:
    if label is not None:
        sns.lineplot(x=values, y=pdf, marker="o", label=label)
    else:
        sns.lineplot(x=values, y=pdf, marker="o")

def plot_pdf(values : np.ndarray, pdf : np.ndarray, label : str = None) -> None:
    sns.set_style("darkgrid")
    __plot_single_pdf__(values, pdf, label)
    plt.ylabel("Densidad de Probabilidad")
    plt.xlabel("Velocidades (cm/s)")

def plot_multiple_pdf(values : list[np.ndarray], pdf : list[np.ndarray], labels : list[str], ylim : tuple[float, float] = None, filename : str = "pdf.png") -> None:
    sns.set_style("darkgrid")
    for i in range(len(pdf)):
        __plot_single_pdf__(values[i], pdf[i], label=labels[i])
    
    plt.legend(bbox_to_anchor=(1.04, 1), loc="upper left")
    if ylim is not None:
        plt.ylim(ylim)
    plt.ylabel("Densidad de Probabilidad")
    plt.xlabel("Velocidades (cm/s)")
    plt.savefig(filename, bbox_inches='tight', dpi=1200)