#!/bin/bash

# Specify the path to the JAR file
JAR_FILE="../target/timeDrivenSimulations-1.0-jar-with-dependencies.jar"

# Create output path
mkdir ../../plotters/ej2_2

# Specify the values of N
N_VALUES=(10 20 30)

# Loop through the N_VALUES array
for N in "${N_VALUES[@]}"
do
  echo "Running with N=$N"
  if [ $N -gt 20 ]
  then
    java -jar "$JAR_FILE" random --static-file="../../plotters/ej2_2/static-N${N}-ordered.txt" --dynamic-file="../../plotters/ej2_2/dynamic-N${N}-ordered.txt" -n=5 -k=1 -N="$N" --equidistant --ordered --seed=42
  else
    java -jar "$JAR_FILE" random --static-file="../../plotters/ej2_2/static-N${N}-ordered.txt" --dynamic-file="../../plotters/ej2_2/dynamic-N${N}-ordered.txt" -n=5 -k=1 -N="$N" --ordered --seed=42
  fi
  echo "---------------------------------"
done
