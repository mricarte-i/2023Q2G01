PYTHON_CMD=$1
STATIC_FILE=$2
DYNAMIC_FILE=$3
POLARIZATION_FILE=$4
PLOT_FILE=$5
XYZ_FILE=$6
OVITO_FILE=$7
ANIMATION_FILE=$8
OVITO_GUI_MODE=1 xvfb-run $PYTHON_CMD main.py -s $STATIC_FILE -d $DYNAMIC_FILE -p $POLARIZATION_FILE --plot-file $PLOT_FILE --xyz $XYZ_FILE -o $OVITO_FILE -a $ANIMATION_FILE