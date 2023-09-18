from impulse_parser     import get_impulses_from_file, impulse_sum_with_equal_timestamps, generate_impulses_sequences
from pressure_generator import get_pressures, get_pressures_between
from container_utils    import get_perimeter, get_area

import matplotlib.pyplot as plt
import pandas  as pd
import seaborn as sns

def pressure_over_inverse_area(impulse_files : list[str], Ls : list[float], delta_t : float, stationary_ts : list[float]) -> list[pd.DataFrame]:
    pressure_colname, area_colname = "Pressure", "Area"
    dfs = []

    for i in range(len(impulse_files)):
        L = Ls[i]
        stationary_t = stationary_ts[i]
        impulse_file = impulse_files[i]
        
        left_container_impulses,     right_container_impulses     = get_impulses_from_file(impulse_file)
        left_container_impulses_sqc, right_container_impulses_sqc = generate_impulses_sequences(left_container_impulses, right_container_impulses, delta_t)

        impulses = impulse_sum_with_equal_timestamps(left_container_impulses_sqc, right_container_impulses_sqc)
        
        perimeter = get_perimeter(L)
        area      = get_area(L)

        pressures          = get_pressures(impulses, delta_t, perimeter)
        last_pressure_time = pressures.instants[-1]

        stationary_pressures = get_pressures_between(pressures, stationary_t, last_pressure_time)

        l_df = pd.DataFrame(stationary_pressures.pressures, columns=[pressure_colname])
        l_df.insert(1, area_colname, (1 / area))

        print(L, len(stationary_pressures.pressures))

        dfs.append(l_df)
    
    return dfs

def plot_pressure_over_inverse_area(dfs : list[pd.DataFrame], Ls : list[float], filename : str = "pressure_over_inverse_area.png" ) -> pd.DataFrame:
    pressure_colname, area_colname = "Pressure", "Area"
    sns.set_style("darkgrid")
    i = 0
    for df in dfs:
        mean = df.mean().values[0]
        std  = df.std().values[0]
        label  = "<P>$_{est}$ = " + "{:.2f}".format(mean) + " ± " + "{:.1g}".format(std) + "\n"
        label += "L = {}\n".format(Ls[i])
        sns.lineplot(data=df, x=area_colname, y=pressure_colname, errorbar="sd", marker="o", err_style="bars", linestyle="", err_kws={'capsize': 10}, label=label)
        i += 1
    print(df.groupby([area_colname]).mean(), df.groupby([area_colname]).std())
    plt.ylabel("Presión promedio estacionaria ($\\frac{N}{m}$)")
    plt.xlabel("Inversa del área del sistema ($\\frac{1}{m^{2}}$)")
    plt.legend(bbox_to_anchor=(1.04, 1), loc="upper left")
    plt.savefig(filename, bbox_inches='tight', dpi=1200)

