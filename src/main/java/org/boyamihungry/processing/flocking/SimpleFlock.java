package org.boyamihungry.processing.flocking;

import processing.core.PVector;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by patwheaton on 5/31/16.
 */
public class SimpleFlock implements Flock {


    Map<Integer,Particle> particles = new HashMap<>();


    @Override
    public void addMember(@NotNull Particle p) {
        particles.put(p.getId(),p);
    }

    @Override
    public Map<Integer, Particle> getMembers() {
        return particles;
    }

    @Override
    public Set<Particle> getNeighborsWithinDistance(@NotNull PVector location,
                                                    @NotNull int distance) {

        return getMembers().values().stream()
                        .filter(p -> location.dist(p.getPosition()) < distance)
                        .collect(Collectors.toSet());
    }


}
