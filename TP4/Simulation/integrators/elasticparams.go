package integrators

import (
	particle "simulation/particle"
)

// in GO fields that start with lowercase are private
type ElasticParams struct {
	p        *particle.Particle
	k, g, dt float64
}

func NewElasticParams(p *particle.Particle, k, g, dt float64) *ElasticParams {
	return &ElasticParams{p, k, g, dt}
}

func (e *ElasticParams) GetElasticConstant() float64 {
	return e.k
}

func (e *ElasticParams) GetDampingCoefficient() float64 {
	return e.g
}

func (e *ElasticParams) SetElasticConstant(newK float64) {
	e.k = newK
}

func (e *ElasticParams) SetDampingCoefficient(newG float64) {
	e.g = newG
}
