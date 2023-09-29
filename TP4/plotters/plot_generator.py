import seaborn as sns
import matplotlib.pyplot as plt
import pandas as pd

def plot_oscillator_step(sqr_err_df : pd.DataFrame, filename : str = "oscillator_step.png", xlim : tuple[float, float] = [0, 105], ylim : tuple[float, float] = [0, 6], hue : str = None) -> None:
    sns.set_style("darkgrid")
    if hue is None:
        sns.lineplot(data=sqr_err_df, x="step", y="sqr_err", marker="o", linestyle="", errorbar="sd", err_style="bars", err_kws={'capsize': 5}, color="blue", label="")
    else:
        sns.lineplot(data=sqr_err_df, x="step", y="sqr_err", marker="o", linestyle="", errorbar="sd", err_style="bars", err_kws={'capsize': 5}, color="blue", hue=hue)
    plt.xlabel("Paso temporal (s)")
    plt.ylabel("Error cuadrático medio ($cm^{2}$)")
    plt.xlim(xlim)
    plt.ylim(ylim)
    #plt.legend(bbox_to_anchor=(1.04, 1), loc="upper left")
    plt.savefig(filename, bbox_inches='tight', dpi=1200)