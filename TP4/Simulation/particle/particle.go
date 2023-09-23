package particle

import "math"

// in GO fields that start with lowercase are private
type Particle struct {
	r      float64
	radius float64
	mass   float64
	v      float64
	id     int
}

func NewParticle(x, r, m, v float64, id int) *Particle {
	return &Particle{x, r, m, v, id}
}

func (p *Particle) GetDistance(o Particle) float64 {
	// we are working in 1D y'all :)
	return math.Abs(p.r-o.GetPosition()) - p.radius - o.GetRadius()
}

// also sims are Damped Harmonic Oscillators!
// F = m*a = -k*r -gamma*v, k: spring constant, gamma: viscous damping coefficient
// r and v are params since we might have to deal with future or past info
func (p *Particle) GetAcceleration(r, k, g, v float64) float64 {
	return -(k*r + g*v) / p.mass
}

// GETTER SETTERS
func (p *Particle) GetPosition() float64 {
	return p.r
}

func (p *Particle) GetVelocity() float64 {
	return p.v
}

func (p *Particle) GetRadius() float64 {
	return p.radius
}

func (p *Particle) GetMass() float64 {
	return p.mass
}

func (p *Particle) GetId() int {
	return p.id
}

func (p *Particle) SetPosition(newR float64) {
	p.r = newR
}

func (p *Particle) SetVelocity(newV float64) {
	p.v = newV
}

func (p *Particle) SetRadius(newRad float64) {
	p.radius = newRad
}

func (p *Particle) SetMass(newM float64) {
	p.mass = newM
}

func (p *Particle) SetId(newId int) {
	p.id = newId
}
