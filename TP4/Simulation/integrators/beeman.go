package integrators

import (
	"fmt"
	"math"

	particle "simulation/particle"
)

// BeemanIntegrator "extends" ElasticParams
// so b (BeemanIntegrator) could be able to use b.GetElasticConstant(), just like if it was an ElasticParams
type BeemanIntegrator struct {
	ElasticParams
	prevA float64
	a     float64
	r, v  float64
}

func Test() {
	fmt.Println("imagine this actually worked lmao")
}

func NewBeemanIntegrator(p *particle.Particle, k, g, dt float64) (e *BeemanIntegrator) {
	ep := *NewElasticParams(p, k, g, dt)
	r := p.GetPosition()
	v := p.GetVelocity()

	a := p.GetAcceleration(p.GetPosition(), k, g, p.GetVelocity())
	prevA := p.GetAcceleration(p.GetPosition()-dt*p.GetVelocity(), k, g, p.GetVelocity()-dt*a)
	return &BeemanIntegrator{ep, prevA, a, r, v}
}

func (b *BeemanIntegrator) GetParticle() *particle.Particle {
	return b.p
}

func (b *BeemanIntegrator) Update() {
	newR := b.getNewPosition()
	newV := b.getNewVelocity(newR)
	b.r = newR
	b.v = newV
	b.prevA = b.a
	b.a = b.p.GetAcceleration(b.r, b.k, b.g, b.v)
	b.p.SetPosition(newR)
	b.p.SetVelocity(newV)
}

// GO makes 1/6 result in 0, you need to convert to float with each number...
const (
	div2o3 = float64(2) / float64(3)
	div1o6 = float64(1) / float64(6)
	div1o3 = float64(1) / float64(3)
	div5o6 = float64(5) / float64(6)
)

// in GO functions that start with lowercase are private
func (b *BeemanIntegrator) getNewPosition() float64 {
	return b.r + b.v*b.dt + div2o3*b.a*math.Pow(b.dt, 2) - div1o6*b.prevA*math.Pow(b.dt, 2)
}

func (b *BeemanIntegrator) getNewVelocity(newR float64) float64 {
	futureA := b.p.GetAcceleration(newR, b.k, b.g, b.v)
	return b.v + div1o3*futureA*b.dt + div5o6*b.a*b.dt - div1o6*b.prevA*b.dt
}
