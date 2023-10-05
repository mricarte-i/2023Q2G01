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
    java -jar "$JAR_FILE" random --static-file="../../plotters/ej2_2/static-N${N}.txt" --dynamic-file="../../plotters/ej2_2/dynamic-N${N}.txt" -n=2 -k=1 -N="$N" --equidistant
  else
    java -jar "$JAR_FILE" random --static-file="../../plotters/ej2_2/static-N${N}.txt" --dynamic-file="../../plotters/ej2_2/dynamic-N${N}.txt" -n=2 -k=1 -N="$N"
  fi
  echo "---------------------------------"
done
