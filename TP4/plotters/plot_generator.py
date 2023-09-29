import seaborn as sns
import matplotlib.pyplot as plt
import pandas as pd

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