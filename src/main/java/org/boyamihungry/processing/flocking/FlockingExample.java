package org.boyamihungry.processing.flocking;

import org.boyamihungry.processing.DrawingUtilities;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;

import javax.validation.constraints.NotNull;

/**
 * Created by patwheaton on 5/26/16.
 */
public class FlockingExample extends PApplet {


    public static final int AVOID = 15;
    public static final float AVOID_FORCE = 2f;
    public static final int COHESE = 15;
    public static final float COHESE_FORCE = 2f;
    public static final int FOLLOW = 50;
    public static final float FOLLOW_FORCE = 2F;
    //public static final PVector originVector = new PVector(0,0);

    public static final int WIDTH = 1440;
    public static final int HEIGHT = 1080;
    public static final int NUM_PARTICLES = 40;

    public static final int BACKGROUND = 50;

    public static final int BORDER = 320;
    public static final float BORDER_FORCE = -1f;
    public static final float WHAT_IS_ZERO = 0.001f;

    private @NotNull Flock flock;

    boolean stepFrame = false;

    //List particles;

    public void settings() {
        size(WIDTH,HEIGHT);
    }



    public void setup() {

        this.frameRate(90f);

        //PApplet app = (PApplet)this;

        //particles = new ArrayList<Particle>();

        Particle.ParticleAvoidCalculator avoid = (app, affectingP, affectedP) -> {
            // get distance between
            float dist = affectingP.getPosition().dist(affectedP.getPosition());
            if ( dist < AVOID ) {
                PVector diff = PVector.sub(affectingP.getVelocity(), affectedP.getVelocity());
                return diff.div(dist).mult(AVOID_FORCE);
            } else {
                return DrawingUtilities.getOriginVector();
            }

        };

        Particle.ParticleFollowCalculator follow = (app, affectingP, affectedP) -> {
            // get distance between
            float dist = affectingP.getPosition().dist(affectedP.getPosition());
            if ( dist < FOLLOW ) {
                return affectingP.getVelocity().copy().mult(FOLLOW_FORCE);
            } else {
                return DrawingUtilities.getOriginVector();
            }

        };


        Particle.ParticleCoheseCalculator cohese = (app, affectingP, affectedP) -> {
            // get distance between
            float dist = affectingP.getPosition().dist(affectedP.getPosition());
            if ( dist < COHESE ) {
                return affectingP.getPosition().copy().mult(COHESE_FORCE);
            } else {
                return DrawingUtilities.getOriginVector();
            }

        };

        Particle.ParticleBorderVelocityReaction borderVel = (app, p) -> {
            float otherWayVelX = 0f;
            float otherWayVelY = 0f;

            if ( p.getPosition().x < BORDER || p.getPosition().x + BORDER > app.width ) {
                float borderDist = p.getPosition().x < BORDER
                        ? p.getPosition().x
                        : width - p.getPosition().x;
                otherWayVelX = p.getVelocity().x / borderDist;
            }

            if ( p.getPosition().y < BORDER || p.getPosition().y + BORDER > height ) {
                float borderDist = (p.getPosition().y < BORDER ? p.getPosition().y : height - p.getPosition().y );
                otherWayVelY = p.getVelocity().y / borderDist;
            }

            if ( (int)(1000 * otherWayVelX) != 0 || (int)(1000 * otherWayVelY) != 0 ) {
                return new PVector(otherWayVelX, otherWayVelY).mult(BORDER_FORCE);
            } else {
                return DrawingUtilities.getOriginVector();
            }
        };

        Particle.ParticleBorderAccelReaction borderAcc = (app, p) -> {
            float otherWayAccX = 0f;
            float otherWayAccY = 0f;

            if ( p.getPosition().x < BORDER || p.getPosition().x + BORDER > width ) {
                float borderDist = (p.getPosition().x < BORDER ? p.getPosition().x : p.getPosition().x - width);
                otherWayAccX = (p.getAcceleration().x * borderDist) / (BORDER - Math.abs(borderDist));
            }

            if ( p.getPosition().y < BORDER || p.getPosition().y + BORDER > height ) {
                float borderDist = (p.getPosition().y < BORDER ? p.getPosition().y : p.getPosition().y - height);
                otherWayAccY = (p.getAcceleration().y * borderDist) / (BORDER - Math.abs(borderDist));
            }

            //if ( (int)(otherWayAccX) != 0 || (int)otherWayAccY != 0 ) {
            //    return new PVector(otherWayAccX * BORDER_FORCE, otherWayAccY * BORDER_FORCE);
            //} else {
                return DrawingUtilities.getOriginVector();
            //}
        };

        SimpleFlock flock = new SimpleFlock();

        for (int i = 0; i < NUM_PARTICLES; i++) {
            flock.addMember(
                    new Particle(
                            i,
                            new PVector(random(WIDTH), random(HEIGHT)),
                            DrawingUtilities.getOriginVector(),
                            PVector.random2D().mult(5),
                            (app,p) -> {   // drawing function

                                pushMatrix();
                                pushStyle();
                                translate(p.getPosition().x, p.getPosition().y);
                                text(p.getId(),0,0);
                                ellipse(0,0,5,5);
                                stroke(0,128,0);
                                DrawingUtilities.arrowLine(app,0,0,p.getVelocity().x * 10, p.getVelocity().y * 10, 0, .333f, true);
                                popStyle();
                                popMatrix();

                            },
                            avoid,
                            follow,
                            cohese,
                            borderVel,
                            borderAcc
                    )
            );
        }

        this.flock = flock;

        try {
            Thread.sleep(1000);
        } catch (InterruptedException iE) {
            // do nothing
        }
    }


    public void draw() {

        if ( stepFrame ) {
            stepFrame = false;
            background(BACKGROUND);
            flock.updateUsingAll(this);
            flock.draw(this);

        }

    }

    @Override
    public void keyTyped(KeyEvent event) {
        super.keyTyped(event);

        if ( event.getKey() == ' ') {
            stepFrame = true;
        }
    }

    static public void main(String[] passedArgs) {


        String[] appletArgs = new String[]{"org.boyamihungry.processing.flocking.FlockingExample"};
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }



}
