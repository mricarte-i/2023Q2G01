from simulation_parser import SimulationInfo, ParticleInfo

def __init_particles__(simulation_info : SimulationInfo) -> list[ParticleInfo]:
    particles = []
    for i in range(simulation_info.N):
        p = simulation_info.particles[i]
        particles.append(ParticleInfo(p.mass, p.radius, [], []))
    return particles

def __get_particle_dynamic_vals_at__(event : int, particle : ParticleInfo) -> tuple[tuple[float,float], tuple[float, float]]:
    position = particle.position[event]
    velocity = particle.velocity[event]
    return (position, velocity)

def __get_position_at__(t : float, 
                        x0 : float, y0 : float, 
                        vx : float, vy : float) -> tuple[float, float]:
    x = x0 + vx * t
    y = y0 + vy * t
    return (x, y)

def __add_instant_to_particle__(particle : ParticleInfo, x : float, y : float, vx : float, vy : float) -> None:
    particle.position.append((x, y))
    particle.velocity.append((vx, vy))

def convert_to_sequence(simulation_info : SimulationInfo, dt : float, no_interpolation : bool = False) -> SimulationInfo:
    instant_count = len(simulation_info.instants)
    
    N         = simulation_info.N
    instants  = []
    particles = __init_particles__(simulation_info)

    if instant_count <= 0:
        return SimulationInfo(N, instants, particles)
    
    current_time  = simulation_info.instants[0]
    end_time      = simulation_info.instants[-1]
    current_event = 0
    next_event    = 1   if instant_count > 1 \
                        else None
    
    while current_time < end_time:
        instants.append(current_time)
        while next_event is not None \
            and current_time >= simulation_info.instants[next_event]:
            current_event  = next_event
            next_event    += 1
            if next_event > instant_count:
                next_event = None
        
        current_time_diff = current_time - simulation_info.instants[current_event]
        for i in range(N):
            event_particle = simulation_info.particles[i]
            
            if no_interpolation:
                position, velocity = __get_particle_dynamic_vals_at__(next_event, event_particle)
                x , y  = position
                vx, vy = velocity
            else:
                position, velocity = __get_particle_dynamic_vals_at__(current_event, event_particle)
                x0, y0 = position
                vx, vy = velocity
                x , y  = __get_position_at__(current_time_diff, x0, y0, vx, vy)  

            __add_instant_to_particle__(particles[i], x, y, vx, vy)
        
        current_time += dt

    return SimulationInfo(N, instants, particles)