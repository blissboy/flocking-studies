package org.boyamihungry.processing.flocking;

import processing.core.PVector;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by patwheaton on 5/31/16.
 */
public class SimpleFlock implements Flock {


    Set<Particle> particles = new HashSet<>();


    @Override
    public void addMember(@NotNull Particle p) {
        particles.add(p);
    }

    @Override
    public Set<Particle> getMembers() {
        return particles;
    }

    @Override
    public Set<Particle> getNeighborsWithinDistance(@NotNull PVector location,
                                                    @NotNull int distance) {

        Set<Particle> neighbors =
                getMembers().stream()
                        .filter(p -> location.dist(p.getPosition()) < distance)
                        .collect(Collectors.toSet());

//        Set<Particle> neighbors = new HashSet<>();
//
//        for (Particle p: getMembers()) {
//            if ( location.dist(p.getPosition()) < distance ) {
//                neighbors.add(p);
//            }
//        }

        return neighbors;
    }
}
