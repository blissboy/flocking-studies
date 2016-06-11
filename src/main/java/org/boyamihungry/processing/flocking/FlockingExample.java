package org.boyamihungry.processing.flocking;

import controlP5.ControlP5;
import controlP5.ControlP5Constants;
import org.boyamihungry.processing.DrawingUtilities;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;

import javax.validation.constraints.NotNull;

/**
 * Created by patwheaton on 5/26/16.
 */
public class FlockingExample extends PApplet {


    private int AVOID = 15;
    private float AVOID_FORCE = 20f;
    private int COHESE = 50;
    private float COHESE_FORCE = 2f;
    private int FOLLOW = 50;
    private float FOLLOW_FORCE = .2f;

    private int WIDTH = 1440;
    private int HEIGHT = 1080;
    private int NUM_PARTICLES = 400;

    private int particle_size = 5;

    public static final int BACKGROUND = 50;

    private int BORDER = 320;
    private float BORDER_FORCE = -1f;

    private @NotNull Flock flock;

    private ControlP5 cp5;


    boolean stepFrame = false;

    //List particles;

    public void settings() {
        size(WIDTH,HEIGHT);
    }



    public void setup() {

        this.frameRate(90f);

//        private int AVOID = 15;
//        private float AVOID_FORCE = 20f;
//        private int COHESE = 50;
//        private float COHESE_FORCE = 2f;
//        private int FOLLOW = 50;
//        private float FOLLOW_FORCE = .2f;
//
//        private int WIDTH = 1440;
//        private int HEIGHT = 1080;
//        private int NUM_PARTICLES = 400;

        cp5 = new ControlP5(this);
        final int SLIDER_WIDTH = 200;
        final int SLIDER_HEIGHT = 20;
        final int SLIDER_LEFT = 40;
        final int vSpacing = 15;
        int count = 0;
        cp5.addSlider("AVOID")
                .setPosition(SLIDER_LEFT, (vSpacing + SLIDER_HEIGHT) * ++count)
                .setSize(SLIDER_WIDTH, SLIDER_HEIGHT)
                .setRange(1, 100)
                .setValue(AVOID)
                .setColorCaptionLabel(ControlP5Constants.RED).getValueLabel().getFont().setSize(30);
        cp5.addSlider("COHESE")
                .setPosition(SLIDER_LEFT, (vSpacing + SLIDER_HEIGHT) * ++count)
                .setSize(SLIDER_WIDTH, SLIDER_HEIGHT)
                .setRange(1, 100)
                .setValue(COHESE)
                .setColorCaptionLabel(ControlP5Constants.RED).getValueLabel().getFont().setSize(30);
        cp5.addSlider("FOLLOW")
                .setPosition(SLIDER_LEFT, (vSpacing + SLIDER_HEIGHT) * ++count)
                .setSize(SLIDER_WIDTH, SLIDER_HEIGHT)
                .setRange(1, 100)
                .setValue(FOLLOW)
                .setColorCaptionLabel(ControlP5Constants.RED).getValueLabel().getFont().setSize(30);
        cp5.addSlider("AVOID_FORCE")
                .setPosition(SLIDER_LEFT, (vSpacing + SLIDER_HEIGHT) * ++count)
                .setSize(SLIDER_WIDTH, SLIDER_HEIGHT)
                .setRange(1, 100)
                .setValue(AVOID_FORCE)
                .setColorCaptionLabel(ControlP5Constants.RED).getValueLabel().getFont().setSize(30);
        cp5.addSlider("COHESE_FORCE")
                .setPosition(SLIDER_LEFT, (vSpacing + SLIDER_HEIGHT) * ++count)
                .setSize(SLIDER_WIDTH, SLIDER_HEIGHT)
                .setRange(1, 100)
                .setValue(COHESE_FORCE)
                .setColorCaptionLabel(ControlP5Constants.RED).getValueLabel().getFont().setSize(30);
        cp5.addSlider("FOLLOW_FORCE")
                .setPosition(SLIDER_LEFT, (vSpacing + SLIDER_HEIGHT) * ++count)
                .setSize(SLIDER_WIDTH, SLIDER_HEIGHT)
                .setRange(1, 100)
                .setValue(FOLLOW_FORCE)
                .setColorCaptionLabel(ControlP5Constants.RED).getValueLabel().getFont().setSize(30);

        cp5.addSlider("particle_size")
                .setPosition(SLIDER_LEFT, (vSpacing + SLIDER_HEIGHT) * ++count)
                .setSize(SLIDER_WIDTH, SLIDER_HEIGHT)
                .setRange(1, 50)
                .setValue(particle_size)
                .setColorCaptionLabel(ControlP5.BLUE).getValueLabel().getFont().setSize(30);

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
                // we want to cohese to where they are going
                return affectingP.getVelocity().copy().mult(COHESE_FORCE);
            } else {
                return DrawingUtilities.getOriginVector();
            }

        };

        Particle.ParticleBorderVelocityReaction borderVel = (app, p) -> {
            //float otherWayVelX = 0f;
            //float otherWayVelY = 0f;

            // left border
            float otherWayVelX = ( p.getVelocity().x + BORDER_FORCE ) / (p.getPosition().x * p.getPosition().x);
            otherWayVelX += ( p.getVelocity().x - BORDER_FORCE ) / (Math.pow(((float)width) - p.getPosition().x,2));

            float otherWayVelY = ( p.getVelocity().y + BORDER_FORCE ) / (p.getPosition().y * p.getPosition().y);
            otherWayVelY += ( p.getVelocity().y - BORDER_FORCE ) / (Math.pow((((float)height) - p.getPosition().y),2));


//            if ( p.getPosition().x < BORDER || p.getPosition().x + BORDER > app.width ) {
//                float borderDist = p.getPosition().x < BORDER
//                        ? p.getPosition().x
//                        : width - p.getPosition().x;
//                otherWayVelX = p.getVelocity().x / borderDist;
//            }
//
//            if ( p.getPosition().y < BORDER || p.getPosition().y + BORDER > height ) {
//                float borderDist = (p.getPosition().y < BORDER ? p.getPosition().y : height - p.getPosition().y );
//                otherWayVelY = p.getVelocity().y / borderDist;
//            }

//            if ( (int)(1000 * otherWayVelX) != 0 || (int)(1000 * otherWayVelY) != 0 ) {
            return new PVector(otherWayVelX, otherWayVelY).mult(BORDER_FORCE);
//            } else {
//                return DrawingUtilities.getOriginVector();
//            }
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
                            PVector.random2D().mult(3),
                            (app,p) -> {   // drawing function

                                pushMatrix();
                                pushStyle();
                                translate(p.getPosition().x, p.getPosition().y);
                                text(p.getId(),0,0);
                                ellipse(0,0,particle_size,particle_size);
                                stroke(0,128,0);
                                DrawingUtilities.arrowLine(app,0,0,p.getVelocity().x * 10, p.getVelocity().y * 10, 0, .333f, true);
                                popStyle();
                                popMatrix();

                            },
                            avoid,
                            follow,
                            cohese,
                            borderVel,
                            borderAcc,
                            null
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

        //if ( stepFrame ) {
        //    stepFrame = false;
            background(BACKGROUND);
            flock.updateUsingAll(this);
            flock.draw(this);
        //}

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
