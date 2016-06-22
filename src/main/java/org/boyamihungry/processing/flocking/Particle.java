package org.boyamihungry.processing.flocking;

import org.boyamihungry.processing.DrawingUtilities;
import processing.core.PApplet;
import processing.core.PVector;

import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.Set;


/**
 * Created by patwheaton on 5/25/16.
 */
public class Particle {

    public static final int FOLLOW_DISTANCE = 50;
    public static final float FOLLOW_FORCE = 2f;

    public static final int COHESE_DISTANCE = 20;
    public static final float COHESE_FORCE = 2f;

    public static final float MAX_STEER_FORCE = .11f;

    @NotNull
    private int id;
    @NotNull
    private PVector position;
    @NotNull
    private PVector acceleration = DrawingUtilities.getOriginVector().copy();
    @NotNull
    private PVector velocity;
    @NotNull
    private ParticleDraw drawer;

    @NotNull
    private ParticleFollowFlockCalculator followFlockCalculator;

    @NotNull
    private ParticleCoheseFlockCalculator coheseFlockCalculator;

    @NotNull
    private ParticleAvoidFlockCalculator avoidFlockCalculator;

    @NotNull
    private Wiggler wiggler;

    @NotNull
    private ParticleBorderTeleporter teleporter;



    //todo: how to use this?
    @NotNull
    private ParticleBorderVelocityReaction borderVelReaction =
            (app, p) -> {return DrawingUtilities.getOriginVector();};

    @NotNull
    private ParticleBorderAccelReaction borderAccReaction =
            (app, p) -> {return DrawingUtilities.getOriginVector();};


    public Particle(int id,
                    PVector position,
                    PVector velocity,
                    ParticleDraw drawer,
                    ParticleAvoidFlockCalculator avoidFlock,
                    ParticleCoheseFlockCalculator coheseFlock,
                    ParticleFollowFlockCalculator followFlock,
                    Wiggler wiggler,
                    ParticleBorderTeleporter teleporter) {

        this.velocity = velocity;
        this.drawer = drawer;
        this.position = position;
        this.coheseFlockCalculator = coheseFlock;
        this.followFlockCalculator = followFlock;
        this.avoidFlockCalculator = avoidFlock;
        this.wiggler = wiggler != null ? wiggler : this.wiggler;
        this.teleporter = teleporter;

    }

    public void step (Optional<Flock> flock) {

        flock.ifPresent(
                (f) -> {
                    this.acceleration = followFlockCalculator.followFlock(this, f);
                    this.acceleration = avoidFlockCalculator.avoidFlock(this, f);
                    this.acceleration = getCoheseToFlockCalculator().coheseToFlock(this, f);
                }
        );

        this.velocity.add(acceleration.limit(MAX_STEER_FORCE));
        this.wiggler.wiggle(this);
        this.velocity.limit(5);
        this.position.add(velocity);
        this.acceleration.setMag(0f);
        this.teleporter.teleportIfNeeded(this);
    }

    public Particle accelerate(PVector p) {
        acceleration.add(p);
        return this;
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
        public PVector followFlock(//PApplet app,
                                   Particle affectedP,
                                   Flock flock);
    }
    @FunctionalInterface
    public interface ParticleAvoidCalculator {
        public PVector avoid(PApplet app,
                             Particle affectedP,
                             Particle affectingP);
    }


    //TODO: for all the *flock calculators, they should return vector, and likely be renamed to admit that.

    @FunctionalInterface
    public interface ParticleInteractionCalculator {
        public PVector getInteractionResult(Particle affected, Particle affector);
    }



    @FunctionalInterface
    public interface ParticleAvoidFlockCalculator {
        public PVector avoidFlock(//PApplet app,
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
        public PVector coheseToFlock(//PApplet app,
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

    @FunctionalInterface
    public interface ParticleFollowNeighborsFilter {
        public Set getNeighborsForFollow(Particle p, Flock f);
    }

    @FunctionalInterface
    public interface Wiggler {
        public PVector wiggle(Particle p);
    }

    @FunctionalInterface
    public interface ParticleBorderTeleporter {
        public PVector teleportIfNeeded(Particle p);
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

    public ParticleFollowFlockCalculator getFollowFlockCalculator() {
        return followFlockCalculator;
    }

    public ParticleCoheseFlockCalculator getCoheseToFlockCalculator() {
        return coheseFlockCalculator;
    }

    public ParticleAvoidFlockCalculator getAvoidWithinFlockCalculator() {
        return avoidFlockCalculator;
    }



//    public void borders(PApplet app) {
////        float mag = acceleration.mag();
////        acceleration.add(this.borderAccReaction.borderReact(app, this));
////        acceleration.setMag(mag);
//
////        float mag = velocity.mag();
////
////        // debug
////        PVector borderVelReaction =
////                this.borderVelReaction.borderReact(app, this);
////
////        if ( ! borderVelReaction.equals(DrawingUtilities.getOriginVector())) {
////            app.pushStyle();
////            app.pushMatrix();
////            app.translate(this.getPosition().x, this.getPosition().y);
////            app.stroke(255, 0, 0);
////            DrawingUtilities.arrowLine(
////                    app,
////                    0,
////                    0,
////                    borderVelReaction.x * 5,
////                    borderVelReaction.y * 5,
////                    0,
////                    0.333f,
////                    true);
////            //app.ellipse();
////            app.popMatrix();
////            app.popStyle();
////            // end debug
////        }
////        velocity.add(this.borderVelReaction.borderReact(app, this));
////        //velocity.setMag(mag);
//
//        /* this way wraps around */
//        if ( getPosition().x < 0 ) {
//            this.position.x += app.width;
//        } else if (getPosition().x > app.width ) {
//            this.position.x -= app.width;
//        }
//        if ( getPosition().y < 0 ) {
//            this.position.y += app.height;
//        } else if (getPosition().y > app.height ) {
//            this.position.y -= app.height;
//        }
//
//        /* this way just bounces them ******************************
//
//        if ( getPosition().x < 0 || getPosition().x > app.width ) {
//            velocity.set(getVelocity().x * -1, getVelocity().y);
//        }
//
//        if ( getPosition().y < 0 || getPosition().y > app.height) {
//            velocity.set(getVelocity().x, getVelocity().y * -1);
//        }
//
//        **************************************/
//    }


    public static class Builder {

        // required
        @NotNull
        private int id;
        @NotNull
        private PVector position;
        @NotNull
        private PVector velocity;
        @NotNull
        private ParticleDraw drawer;

        @NotNull
        private ParticleFollowNeighborsFilter followNeighborsCalculator =
                (p, f) -> {
                    return f.getNeighborsWithinDistance(p.getPosition().copy(), FOLLOW_DISTANCE);
                };

        @NotNull
        private ParticleCoheseFlockCalculator builder_coheseFlockCalculator;
        @NotNull
        private ParticleAvoidFlockCalculator avoidFlockCalculator;
        @NotNull
        private ParticleFollowFlockCalculator followFlockCalculator;
        @NotNull
        private Wiggler wiggler;
        @NotNull
        private ParticleBorderTeleporter teleporter;


        public Builder(int id, PVector position, PVector velocity, ParticleDraw drawer ) {
            this.id = id;
            this.position = position;
            this.velocity = velocity;
            this.drawer = drawer;
        }

        // todo: add Builder constructor with Particle as arg

        public Builder withAvoidFlockCalculator(ParticleAvoidFlockCalculator avoidFlock) {
            this.avoidFlockCalculator = avoidFlock;
            return this;
        }

        public Builder withFollowFlockCalculator(ParticleFollowFlockCalculator followFlock) {
            this.followFlockCalculator = followFlock;
            return this;
        }

        public Builder withCoheseToFlockCalculator(ParticleCoheseFlockCalculator coheseToFlock) {
            this.builder_coheseFlockCalculator = coheseToFlock;
            return this;
        }

        public Builder withWiggle(Wiggler wiggler) {
            this.wiggler = wiggler;
            return this;
        }

        public Builder withTeleport(ParticleBorderTeleporter teleporter) {
            this.teleporter = teleporter;
            return this;
        }

        public Particle build() {
            return new Particle(id, position, velocity,drawer,avoidFlockCalculator,builder_coheseFlockCalculator,followFlockCalculator,wiggler,teleporter);
        }

    }


}
