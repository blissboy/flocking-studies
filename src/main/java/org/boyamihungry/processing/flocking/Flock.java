package org.boyamihungry.processing.flocking;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.Optional;
import java.util.Set;

/**
 * Created by patwheaton on 5/30/16.
 */
public interface Flock {

    public void addMember(Particle p);
    public Set<Particle> getMembers();
    public Set<Particle> getNeighborsWithinDistance(PVector location, int distance);

    default void updateUsingAll(PApplet app){
            followAll(app);
            avoidAll(app);
            coheseAll(app);
            stepAll(app);
            bordersAll(app);
        };
    default public void coheseAll(PApplet app){
            for(Particle p:getMembers()){
                p.getCoheseToFlockCalculator().coheseToFlock(p,this);
            }
        };
    default public void followAll(PApplet app){
        for(Particle p:getMembers()){
            p.getFollowFlockCalculator().followFlock(p,this);
        }
    };
    default public void avoidAll(PApplet app){
            for(Particle p:getMembers()){
                p.getAvoidWithinFlockCalculator().avoidFlock(p,this);
            }
        };
    default public void stepAll(PApplet app) {
        for(Particle p:getMembers()){
            p.step(Optional.of(this));
        }
    }
    default public void bordersAll(PApplet app) {
//        for(Particle p:getMembers()){
//            p.get(app);
//        }
    }
    default public void draw(PApplet app) {
        for (Particle p : getMembers()) {
            p.getDrawer().draw(app,p);
        }
    }

}
