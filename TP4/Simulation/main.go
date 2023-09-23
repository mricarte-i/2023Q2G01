package main

import (
	"fmt"
	"math"
	"os"

	integrators "simulation/integrators"
	particle "simulation/particle"
)

func main() {
	fmt.Println("hello world")
	// sim parameters
	g := 100.0
	k := math.Pow(10, 4)
	tf := 5.0
	m := 100.0
	A := 1.0
	// initial conditions
	initV := -(A * g) / (2 * m)
	dt := math.Pow(10, -4)

	p := particle.NewParticle(A, 0, m, initV, 0)
	beeman := integrators.NewBeemanIntegrator(p, k, g, dt)
	time := 0.0
	step := 0

	// create output buffer, but could be done without a buffer and just use WriteString...
	output := make([]string, int(tf/dt))
	fmt.Println(tf / dt)

	// TODO: maybe add the abilty to run multiple integrators at once?
	//	 might require to clone the initial particle to make sure they don't step over eachother...
	for time < tf {
		// saves a string with state of the particle as ```[time] [position]````
		// TODO: only print every dt2 ~ 10^-1 or based on params
		output[step] = fmt.Sprintf("%.4f %.4f\n", time, p.GetPosition())
		beeman.Update()
		step += 1
		time = dt * float64(step)
	}

	// TODO: get output path from args
	f, err := os.Create("./test.txt")
	if err != nil {
		panic(err.Error())
	}
	defer f.Close()

	for i := 0; i < len(output); i++ {
		f.WriteString(output[i])
	}

	fmt.Println("Done!")
}
