#!/bin/bash

# Specify the path to the JAR file
JAR_FILE="timeDrivenSimulations-1.0-jar-with-dependencies.jar"

# Specify the values of N
N_VALUES=(5 10 15 20 25 30)

# Loop through the N_VALUES array
for N in "${N_VALUES[@]}"
do
  echo "Running with N=$N"
  if [ $N -gt 20 ]
  then
    java -jar "$JAR_FILE" random --static-file="static-N${N}-ordered.txt" --dynamic-file="dynamic-N${N}-ordered.txt" -n=5 -k=1 -N="$N" --equidistant --seed=42 --ordered
  else
    java -jar "$JAR_FILE" random --static-file="static-N${N}-ordered.txt" --dynamic-file="dynamic-N${N}-ordered.txt" -n=5 -k=1 -N="$N" --seed=42 --ordered
  fi
  echo "---------------------------------"
done
