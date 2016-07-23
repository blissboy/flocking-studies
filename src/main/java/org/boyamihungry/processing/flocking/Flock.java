package org.boyamihungry.processing.flocking;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by patwheaton on 5/30/16.
 */
public interface Flock {

    public void addMember(Particle p);
    public Map<Integer, Particle> getMembers();
    public Set<Particle> getNeighborsWithinDistance(PVector location, int distance);

    default void updateUsingAll(PApplet app){
        followAll(app);
        avoidAll(app);
        coheseAll(app);
        stepAll(app);
        bordersAll(app);
    };
    default public void coheseAll(PApplet app){
        getMembers().values().stream().forEach(
                (p) -> {
                    p.getCoheseToFlockCalculator().coheseToFlock(p,this);
                }
        );
    };
    default public void followAll(PApplet app){
        getMembers().values().stream().forEach(
                (p) -> {
                    p.getFollowFlockCalculator().followFlock(p,this);
                }
        );
    };
    default public void avoidAll(PApplet app){
        getMembers().values().stream().forEach(
                (p) -> {
                    p.getAvoidWithinFlockCalculator().avoidFlock(p,this);
                }
        );
    };
    default public void stepAll(PApplet app) {
        getMembers().values().stream().forEach(
                (p) -> {
                    p.step(Optional.of(this));
                }
        );
    }
    default public void bordersAll(PApplet app) {
//        for(Particle p:getMembers()){
//            p.get(app);
//        }
    }
    default public void draw(PApplet app) {
        getMembers().values().stream().forEach(
                (particle) -> {
                    particle.getDrawers().values().parallelStream().forEach((drawer) -> drawer.draw(app,particle));
                }
        );
    }

}
