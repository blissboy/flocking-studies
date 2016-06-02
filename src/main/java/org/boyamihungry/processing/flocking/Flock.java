package org.boyamihungry.processing.flocking;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.Set;

/**
 * Created by patwheaton on 5/30/16.
 */
public interface Flock {

    public void addMember(Particle p);
    public Set<Particle> getMembers();
    public Set<Particle> getNeighborsWithinDistance(PVector location, int distance);

    default public void updateUsingAll(PApplet app){
            followAll(app);
            avoidAll(app);
            coheseAll(app);
        };
    default public void coheseAll(PApplet app){
            for(Particle p:getMembers()){
                p.getCoheseToFlockCalculator().coheseFlock(app,p,this);
            }
        };
    default public void followAll(PApplet app){
        for(Particle p:getMembers()){
            p.getFollowFlockCalculator().followFlock(app,p,this);
        }
    };
    default public void avoidAll(PApplet app){
            for(Particle p:getMembers()){
                p.getAvoidWithinFlockCalculator().avoidFlock(app,p,this);
            }
        };
    default public void draw(PApplet app) {
        for (Particle p : getMembers()) {
            p.getDrawer().draw(app,p);
        }
    }

}
