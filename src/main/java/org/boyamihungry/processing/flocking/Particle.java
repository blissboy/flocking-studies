package org.boyamihungry.processing.flocking;

import org.boyamihungry.processing.DrawingUtilities;
import processing.core.PApplet;
import processing.core.PVector;

import javax.validation.constraints.NotNull;

/**
 * Created by patwheaton on 5/25/16.
 */
public class Particle {

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
            (app, vec1, vec2) -> {return DrawingUtilities.getOriginVector();};

    @NotNull
    private ParticleCoheseCalculator coheseCalculator =
            (app, vec1, vec2) -> {return DrawingUtilities.getOriginVector();};
    @NotNull
    private ParticleAvoidCalculator avoidCalculator =
            (app, vec1, vec2) -> {return DrawingUtilities.getOriginVector();};

    @NotNull
    private ParticleFollowFlockCalculator followFlockCalculator =
            (app, p, flock) -> {
                PVector followSum = DrawingUtilities.getOriginVector();
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
                PVector coheseSum = DrawingUtilities.getOriginVector();
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
                PVector avoidSum = DrawingUtilities.getOriginVector();
                for ( Particle other: flock.getMembers()) {
                    if ( other.getId() != p.getId() ) {
                        avoidSum.add(p.getAvoidCalculator().avoid(app,p,other));
                    }
                }
                return avoidSum;
            };


    //todo: how to use this?
    @NotNull
    private ParticleBorderVelocityReaction borderVelReaction =
            (app, p) -> {return DrawingUtilities.getOriginVector();};

    @NotNull
    private ParticleBorderAccelReaction borderAccReaction =
            (app, p) -> {return DrawingUtilities.getOriginVector();};


    public Particle(int id,
                    PVector position,
                    PVector acceleration,
                    PVector velocity,
                    ParticleDraw drawer,
                    ParticleAvoidCalculator avoid,
                    ParticleFollowCalculator follow,
                    ParticleCoheseCalculator cohese,
                    ParticleBorderVelocityReaction borderVelReaction,
                    ParticleBorderAccelReaction borderAccReaction) {



        this.id = id;
        this.acceleration = acceleration;
        this.velocity = velocity;
        this.drawer = drawer;
        this.position = position;
        this.borderVelReaction = borderVelReaction;
        this.borderAccReaction = borderAccReaction;

        this.coheseCalculator = cohese != null ? cohese : coheseCalculator;
        this.followCalculator = follow != null ? follow : followCalculator;
        this.avoidCalculator = avoid != null ? avoid : avoidCalculator;
        
    }

    public void step () {
        this.position.add(velocity);
        this.velocity.add(acceleration);
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

    @FunctionalInterface
    public interface ParticleBorderVelocityReaction {
        public PVector borderReact(PApplet app,
                                   Particle affectedP);
    }

    @FunctionalInterface
    public interface ParticleBorderAccelReaction {
        public PVector borderReact(PApplet app,
                                   Particle affectedP);
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

    public void borders(PApplet app) {
//        float mag = acceleration.mag();
//        acceleration.add(this.borderAccReaction.borderReact(app, this));
//        acceleration.setMag(mag);

        float mag = velocity.mag();

        // debug
        PVector borderVelReaction =
                this.borderVelReaction.borderReact(app, this);

        if ( ! borderVelReaction.equals(DrawingUtilities.getOriginVector())) {
            app.pushStyle();
            app.pushMatrix();
            app.translate(this.getPosition().x, this.getPosition().y);
            app.stroke(255, 0, 0);
            DrawingUtilities.arrowLine(
                    app,
                    0,
                    0,
                    borderVelReaction.x * 5,
                    borderVelReaction.y * 5,
                    0,
                    0.333f,
                    true);
            //app.ellipse();
            app.popMatrix();
            app.popStyle();
            // end debug
        }
        velocity.add(this.borderVelReaction.borderReact(app, this));
        //velocity.setMag(mag);
    }

}
