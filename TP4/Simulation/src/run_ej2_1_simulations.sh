#!/bin/bash

# Specify the path to the JAR file
JAR_FILE="timeDrivenSimulations-1.0-jar-with-dependencies.jar"

# Specify the values of N
N_VALUES=(5 10 15 20)

# Loop through the N_VALUES array
for N in "${N_VALUES[@]}"
do
  echo "Running with N=$N"
  java -jar "$JAR_FILE" random --static-file="static-N${N}.txt" --dynamic-file="dynamic-N${N}.txt" -n=2 -k=1 -N="$N"
  echo "---------------------------------"
done
