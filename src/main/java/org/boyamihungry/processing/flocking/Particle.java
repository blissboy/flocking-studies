package org.boyamihungry.processing.flocking;

import processing.core.PApplet;
import processing.core.PVector;

import javax.validation.constraints.NotNull;

/**
 * Created by patwheaton on 5/25/16.
 */
public class Particle {

    public static final PVector originVector = new PVector(0,0);


    @NotNull
    private int id;
    @NotNull
    private PVector position;
    @NotNull
    private PVector acceleration;
    @NotNull
    private PVector velocity;
    @NotNull
    private ParticleDraw drawer;

    @NotNull
    private ParticleFollowCalculator followCalculator =
            (app, vec1, vec2) -> {return originVector;};

    @NotNull
    private ParticleCoheseCalculator coheseCalculator =
            (app, vec1, vec2) -> {return originVector;};
    @NotNull
    private ParticleAvoidCalculator avoidCalculator =
            (app, vec1, vec2) -> {return originVector;};

    @NotNull
    private ParticleFollowFlockCalculator followFlockCalculator =
            (app, p, flock) -> {
                PVector followSum = originVector;
                for ( Particle other: flock.getMembers()) {
                    if ( other.getId() != p.getId() ) {
                        followSum.add(p.getFollowCalculator().follow(app,p,other));
                    }
                }
                return followSum;
            };

    @NotNull
    private ParticleCoheseFlockCalculator coheseFlockCalculator =
            (app, p, flock) -> {
                PVector coheseSum = originVector;
                for ( Particle other: flock.getMembers()) {
                    if ( other.getId() != p.getId() ) {
                        coheseSum.add(p.getCoheseCalculator().cohese(app,p,other));
                    }
                }
                return coheseSum;
            };

    @NotNull
    private ParticleAvoidFlockCalculator avoidFlockCalculator =
            (app, p, flock) -> {
                PVector avoidSum = originVector;
                for ( Particle other: flock.getMembers()) {
                    if ( other.getId() != p.getId() ) {
                        avoidSum.add(p.getAvoidCalculator().avoid(app,p,other));
                    }
                }
                return avoidSum;
            };

    public Particle(int id, PVector position,
                    PVector acceleration,
                    PVector velocity,
                    ParticleDraw drawer,
                    ParticleAvoidCalculator avoid,
                    ParticleFollowCalculator follow,
                    ParticleCoheseCalculator cohese) {
        this.acceleration = acceleration;
        this.velocity = velocity;
        this.drawer = drawer;
        this.position = position;
        
        this.coheseCalculator = cohese != null ? cohese : coheseCalculator;
        this.followCalculator = follow != null ? follow : followCalculator;
        this.avoidCalculator = avoid != null ? avoid : avoidCalculator;
        
    }




    @FunctionalInterface
    public interface ParticleDraw {
        public void draw(PApplet app, Particle p);
    }
    @FunctionalInterface
    public interface ParticleFollowCalculator {
        public PVector follow(PApplet app,
                              Particle affectedP,
                              Particle affectingP);
    }
    @FunctionalInterface
    public interface ParticleFollowFlockCalculator {
        public PVector followFlock(PApplet app,
                                   Particle affectedP,
                                   Flock flock);
    }
    @FunctionalInterface
    public interface ParticleAvoidCalculator {
        public PVector avoid(PApplet app,
                             Particle affectedP,
                             Particle affectingP);
    }
    @FunctionalInterface
    public interface ParticleAvoidFlockCalculator {
        public PVector avoidFlock(PApplet app,
                                  Particle affectedP,
                                  Flock flock);
    }
    @FunctionalInterface
    public interface ParticleCoheseCalculator {
        public PVector cohese(PApplet app,
                               Particle affectedP,
                               Particle affectingP);
    }
    @FunctionalInterface
    public interface ParticleCoheseFlockCalculator {
        public PVector coheseFlock(PApplet app,
                                   Particle affectedP,
                                   Flock flock);
    }

    public ParticleDraw getDrawer() {
        return drawer;
    }

    public int getId() {
        return id;
    }

    public PVector getPosition() {
        return position;
    }

    public PVector getAcceleration() {
        return acceleration;
    }

    public PVector getVelocity() {
        return velocity;
    }

    public ParticleFollowCalculator getFollowCalculator() {
        return followCalculator;
    }

    public ParticleCoheseCalculator getCoheseCalculator() {
        return coheseCalculator;
    }

    public ParticleAvoidCalculator getAvoidCalculator() {
        return avoidCalculator;
    }

    public ParticleFollowFlockCalculator getFollowFlockCalculator() {
        return followFlockCalculator;
    }

    public ParticleCoheseFlockCalculator getCoheseToFlockCalculator() {
        return coheseFlockCalculator;
    }

    public ParticleAvoidFlockCalculator getAvoidWithinFlockCalculator() {
        return avoidFlockCalculator;
    }
}
