package org.boyamihungry.processing.flocking;

import controlP5.ControlP5;
import controlP5.ControlP5Constants;
import org.boyamihungry.processing.DrawingUtilities;
import org.boyamihungry.processing.PVectorSummingCollector;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;

import javax.validation.constraints.NotNull;

import static org.boyamihungry.processing.DrawingUtilities.getOriginVector;
import static org.boyamihungry.processing.flocking.Particle.FOLLOW_DISTANCE;

/**
 * Created by patwheaton on 5/26/16.
 */
public class FlockingExample extends PApplet {


    private int AVOID = 5;
    private float AVOID_FORCE = 20f;
    private int COHESE = 50;
    private float COHESE_FORCE = 2f;
    private int FOLLOW = 50;
    private float FOLLOW_FORCE = .2f;

    private int WIDTH = 1920;
    private int HEIGHT = 1080;
    private int NUM_PARTICLES = 200;

    private int particle_size = 5;

    public static final int BACKGROUND = 200;

    private int BORDER = 320;
    private float BORDER_FORCE = -1f;

    private @NotNull Flock flock;

    private ControlP5 cp5;

    private int flockFrameWidth = 1280;
    private int flockFrameHeight = 800;
    private int particleNeighborHeight = 540;






    boolean stepFrame = false;

    //List particles;

    public void settings() {
        size(WIDTH,HEIGHT);
    }



    public void setup() {

        this.frameRate(90f);

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

        // avoid means to find position of each particle around me, and steer away from each one in proportion
        // to how close I am to them.
        Particle.ParticleAvoidCalculator avoid = (app, affectingP, affectedP) -> {
            // get distance between
            float dist = affectingP.getPosition().dist(affectedP.getPosition());
            if ( dist < AVOID ) {
                return PVector.sub(affectedP.getPosition(), affectingP.getPosition())
                        .normalize()
                        .div(dist)
                        .mult(AVOID_FORCE);
            } else {
                return getOriginVector();
            }
        };

        // follow means to look at velocity of particles near me, then follow that vel.
        Particle.ParticleFollowCalculator follow = (app, affectingP, affectedP) -> {
            return affectingP.getVelocity().copy().sub(affectedP.getVelocity());
        };


        // cohese means to look at locations of particles around me and steer towards them.
        Particle.ParticleCoheseCalculator cohese = (app, affectingP, affectedP) -> {
            return affectingP.getPosition().copy().sub(affectedP.getPosition());
        };

        Particle.ParticleBorderVelocityReaction borderVel =
                (app, p) -> {
                    // left border
                    float otherWayVelX = ( p.getVelocity().x + BORDER_FORCE ) / (p.getPosition().x * p.getPosition().x);
                    otherWayVelX += ( p.getVelocity().x - BORDER_FORCE ) / (Math.pow(((float)width) - p.getPosition().x,2));

                    float otherWayVelY = ( p.getVelocity().y + BORDER_FORCE ) / (p.getPosition().y * p.getPosition().y);
                    otherWayVelY += ( p.getVelocity().y - BORDER_FORCE ) / (Math.pow((((float)height) - p.getPosition().y),2));

                    return new PVector(otherWayVelX, otherWayVelY).mult(BORDER_FORCE);
                };

        SimpleFlock flock = new SimpleFlock();

        for (int i = 0; i < NUM_PARTICLES; i++) {
            flock.addMember(
                    new Particle.Builder(
                            i,
                            new PVector(random(flockFrameWidth), random(flockFrameHeight)),
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
                            })
                            .withAvoidFlockCalculator( (p,f) -> {
                                PVector theSum = flock.getNeighborsWithinDistance(p.getPosition(), AVOID)
                                        .stream()
                                        .filter(neighborP -> (neighborP.getId() != p.getId()))
                                        .map( member -> PVector.sub(p.getPosition(), member.getPosition()).div(p.getPosition().dist(member.getPosition().mult(AVOID_FORCE))))
                                        .collect(new PVectorSummingCollector());

                                return theSum;

                            })
                            .withCoheseToFlockCalculator((p,f) -> {
                                PVector theSum = flock.getNeighborsWithinDistance(p.getPosition(), FOLLOW_DISTANCE)
                                        .stream()
                                        .filter(closeP -> (closeP.getId() != p.getId()))
                                        .map( member -> member.getPosition().copy().sub(p.getPosition() ))
                                        .collect(new PVectorSummingCollector());

                                return theSum;
                            })
                            .withFollowFlockCalculator(
                                    ( p, f) -> { // follow flock
                                        PVector theSum = flock.getNeighborsWithinDistance(p.getPosition(), FOLLOW_DISTANCE)
                                                .stream()
                                                .filter(closeP -> (closeP.getId() != p.getId()))
                                                .map( member -> member.getVelocity().copy().sub(p.getVelocity()) )
                                                .collect(new PVectorSummingCollector());

                                        return theSum;
                                    })
                            .withWiggle((p) -> {return PVector.random2D().setMag(.1f);})
                            .withTeleport(
                                    (p) -> {
                                        PVector resultingPosition = p.getPosition();

                                        /* this way wraps around */
                                        if ( p.getPosition().x < 0 ) {
                                            resultingPosition.x += flockFrameWidth;
                                        } else if (p.getPosition().x > flockFrameWidth ) {
                                            resultingPosition.x -= flockFrameWidth;
                                        }
                                        if ( p.getPosition().y < 0 ) {
                                            resultingPosition.y += flockFrameHeight;
                                        } else if (p.getPosition().y > flockFrameHeight ) {
                                            resultingPosition.y -= flockFrameHeight;
                                        }

                                        /* this way just bounces them ******************************

                                        if ( getPosition().x < 0 || getPosition().x > app.width ) {
                                            velocity.set(getVelocity().x * -1, getVelocity().y);
                                        }

                                        if ( getPosition().y < 0 || getPosition().y > app.height) {
                                            velocity.set(getVelocity().x, getVelocity().y * -1);
                                        }

                                        **************************************/

                                        return resultingPosition;

                                    }
                            )
                            .build()

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

        // draw flocking frame
        pushStyle();
        stroke(0);
        strokeWeight(4);
        line(flockFrameWidth,0,flockFrameWidth,height);
        line(0,flockFrameHeight,flockFrameWidth,flockFrameHeight);
        line(flockFrameWidth,particleNeighborHeight,width,particleNeighborHeight);
        popStyle();

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
