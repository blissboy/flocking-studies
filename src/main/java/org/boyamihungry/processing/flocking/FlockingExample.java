package org.boyamihungry.processing.flocking;

import controlP5.ControlP5;
import controlP5.ControlP5Constants;
import org.boyamihungry.processing.PVectorSummingCollector;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

import static org.boyamihungry.processing.DrawingUtilities.arrowLine;
import static org.boyamihungry.processing.flocking.Particle.FOLLOW_DISTANCE;

/**
 * Created by patwheaton on 5/26/16.
 */
public class FlockingExample extends PApplet {

    // variables tied to controls
    private int AVOID = 3;
    private float AVOID_FORCE = .05f;

    private int COHESE = 50;
    private float COHESE_FORCE = 2f;

    private int FOLLOW = 50;
    private float FOLLOW_FORCE = .32f;

    private int particle_size = 5;

    private boolean pauseFlock = false;
    private int featuredParticleId = -1;
    // end variables tied to controls


    private int WIDTH = 1920;
    private int HEIGHT = 1080;
    private int NUM_PARTICLES = 200;


    public static final int BACKGROUND = 200;

    private int BORDER = 320;
    private float BORDER_FORCE = -1f;

    private @NotNull Flock flock;

    private ControlP5 cp5;

    private int flockFrameWidth = 1280;
    private int flockFrameHeight = 800;
    private int featureFrameHeight = 540;
    private int featureFrameWidth = WIDTH - flockFrameWidth;
    private int featureFrameXCenter = flockFrameWidth + (featureFrameWidth / 2);
    private int featureFrameYCenter = featureFrameHeight / 2;
    private PVector featureFrameCenter = new PVector(featureFrameXCenter, featureFrameYCenter);
    boolean stepFrame = false;

    static final String FEATURED_WINDOW_DRAWER = "FEATURED_WINDOW_DRAWER";
    private int currentFeaturedWindowRange = COHESE;


    Map<Integer, PVector> featuredCoheseEffectsMap = new HashMap<>();


    /**
     * Take two points A and B, translate their position (such as to a new window), 
     * to create two points C and D, then zoom in on the two points. Given the position of 
     * points A, B, and C, and the ratio of the zoom window to the original window, calculate
     * the location of point D. 
     *
     * @param ptA
     * @param ptB
     * @param ptC
     * @param xZoomRatio
     * @param yZoomRatio
     * @return
     */
    private PVector zoomInOnTwoPoints(PVector ptA, PVector ptB, PVector ptC, float xZoomRatio, float yZoomRatio) {
//        PVector dum =        new PVector(
//
        return new PVector(
                ptC.x + ((ptB.x - ptA.x) * (xZoomRatio)),
                ptC.y + ((ptB.y - ptA.y) * (yZoomRatio))
//                -1 * (((ptA.x - ptB.x) * xZoomRatio) - ptC.x),
//                -1 * (((ptA.y - ptB.y) * yZoomRatio) - ptC.y)
        );
    }

    public void settings() {
        size(WIDTH,HEIGHT);
    }



    public void setup() {

        this.frameRate(90f);
        setupControlPanel(this);

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
                                arrowLine(app,0,0,p.getVelocity().x * 10, p.getVelocity().y * 10, 0, .333f, true);
                                popStyle();
                                popMatrix();
                            })
                            .withAvoidFlockCalculator( (p,f) -> {
                                PVector theSum = flock.getNeighborsWithinDistance(p.getPosition(), AVOID)
                                        .stream()
                                        .filter(neighborP -> (neighborP.getId() != p.getId()))
                                        .map( member ->
                                                PVector.sub(p.getPosition(), member.getPosition())
                                                        .div(p.getPosition().dist(member.getPosition().copy().mult(AVOID_FORCE))))
                                        .collect(new PVectorSummingCollector());

                                return theSum;

                            })
                            .withCoheseToFlockCalculator((p,f) -> {
                                return flock.getNeighborsWithinDistance(p.getPosition(), FOLLOW_DISTANCE)
                                        .stream()
                                        .filter(closeP -> (closeP.getId() != p.getId()))
                                        .map( neighbor -> {
                                            if ( neighbor.getId() == featuredParticleId ) {
                                                featuredCoheseEffectsMap.put(p.getId(),neighbor.getPosition().copy().sub(p.getPosition()));
                                            }
                                            return neighbor.getPosition().copy().sub(p.getPosition() );
                                        })
                                        .collect(new PVectorSummingCollector());
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

    private void setupControlPanel(PApplet app) {
        cp5 = new ControlP5(this);
        final int SLIDER_WIDTH = 200;
        final int SLIDER_HEIGHT = 20;
        final int SLIDER_LEFT = 40;
        final int vSpacing = 15;
        final int hSpacing = 40;
        int count = 0;

        cp5.getFont().setSize(24);

        cp5.addSlider("AVOID")
                .setPosition(SLIDER_LEFT, ((vSpacing + SLIDER_HEIGHT) * ++count) + flockFrameHeight)
                .setSize(SLIDER_WIDTH, SLIDER_HEIGHT)
                .setRange(1, 100)
                .setValue(AVOID)
                .setColorCaptionLabel(ControlP5Constants.RED);
        cp5.addSlider("COHESE")
                .setPosition(SLIDER_LEFT, ((vSpacing + SLIDER_HEIGHT) * ++count) + flockFrameHeight)
                .setSize(SLIDER_WIDTH, SLIDER_HEIGHT)
                .setRange(1, 100)
                .setValue(COHESE);
        cp5.addSlider("FOLLOW")
                .setPosition(SLIDER_LEFT, ((vSpacing + SLIDER_HEIGHT) * ++count) + flockFrameHeight)
                .setSize(SLIDER_WIDTH, SLIDER_HEIGHT)
                .setRange(1, 100)
                .setValue(FOLLOW);
        cp5.addSlider("AVOID_FORCE")
                .setPosition(SLIDER_LEFT, ((vSpacing + SLIDER_HEIGHT) * ++count) + flockFrameHeight)
                .setSize(SLIDER_WIDTH, SLIDER_HEIGHT)
                .setRange(1, 100)
                .setValue(AVOID_FORCE);
        cp5.addSlider("COHESE_FORCE")
                .setPosition(SLIDER_LEFT, ((vSpacing + SLIDER_HEIGHT) * ++count) + flockFrameHeight)
                .setSize(SLIDER_WIDTH, SLIDER_HEIGHT)
                .setRange(1, 100)
                .setValue(COHESE_FORCE);
        cp5.addSlider("FOLLOW_FORCE")
                .setPosition(SLIDER_LEFT, ((vSpacing + SLIDER_HEIGHT) * ++count) + flockFrameHeight)
                .setSize(SLIDER_WIDTH, SLIDER_HEIGHT)
                .setRange(1, 100)
                .setValue(FOLLOW_FORCE);
        cp5.addSlider("particle_size")
                .setPosition(SLIDER_LEFT, ((vSpacing + SLIDER_HEIGHT) * ++count) + flockFrameHeight)
                .setSize(SLIDER_WIDTH, SLIDER_HEIGHT)
                .setRange(1, 50)
                .setValue(particle_size);

        count = 0;
        cp5.addToggle("pauseFlock")
                .setPosition(SLIDER_LEFT + SLIDER_WIDTH * 2 + hSpacing,
                        ((vSpacing + SLIDER_HEIGHT) * ++count) + flockFrameHeight)
                .setSize(100, SLIDER_HEIGHT)
                .setValue(false);

        count++;
        cp5.addTextfield(featuredParticleId, "Featured Particle ID")
                .setPosition(SLIDER_LEFT + SLIDER_WIDTH * 2 + hSpacing, ((vSpacing + SLIDER_HEIGHT) * ++count) + flockFrameHeight)
                .setSize(SLIDER_WIDTH, SLIDER_HEIGHT);





    }


    public void draw() {

        background(BACKGROUND);

        // draw frame around everything
        pushStyle();
        stroke(0);
        strokeWeight(4);
        line(flockFrameWidth,0,flockFrameWidth,height);
        line(0,flockFrameHeight,flockFrameWidth,flockFrameHeight);
        line(flockFrameWidth, featureFrameHeight,width, featureFrameHeight);
        popStyle();



        flock.draw(this);

        // draw "feature" frame
        int textY = 30;
        int textX = flockFrameWidth + 30;
        text(mouseX + ", " + mouseY, textX, textY);
        textY += 20;
        text("featured: " + featuredParticleId, textX, textY);
        textY += 20;

        if ( flock.getMembers().containsKey(featuredParticleId)) {
            Particle featuredParticle = flock.getMembers().get(featuredParticleId);

            // todo: figure out scaling based on what we are displaying (cohese, follow, avoid). For now, just using cohese
            int currentZoomRegion = COHESE * 2;
            int currentFeaturedWindowRange = COHESE;

            float xZoomRatio = (float)featureFrameWidth / (float)currentZoomRegion;
            float yZoomRatio = (float)featureFrameHeight / (float)currentZoomRegion;

            pushStyle();
            // draw featured in feature frame
            fill(255, 255, 255);
            ellipse(featureFrameXCenter,featureFrameYCenter, particle_size * 3, particle_size * 3);
            stroke(0,255,0);
            fill(0,255,0);
            pushMatrix();
            translate(featureFrameXCenter, featureFrameYCenter);
            arrowLine(this,
                    0,
                    0,
                    featuredParticle.getVelocity().x * 10 * xZoomRatio,
                    featuredParticle.getVelocity().y * 10 * yZoomRatio,
                    0,
                    0.333f,
                    true);

            // draw circle of interest
            stroke(255,0,255);
            noFill();
            ellipse(0, 0, currentZoomRegion * xZoomRatio, currentZoomRegion * yZoomRatio);
            popMatrix();

            // draw the featured particle's neighbors, and show their impact on the featured
            flock.getNeighborsWithinDistance(featuredParticle.getPosition(), currentFeaturedWindowRange)
                    .stream()
                    .filter(neighbor -> neighbor.getId() != featuredParticle.getId())
                    .forEach(neighbor -> {
                        PVector translatedNeighbor = zoomInOnTwoPoints(
                                featuredParticle.getPosition(),
                                neighbor.getPosition(),
                                featureFrameCenter,
                                xZoomRatio,
                                yZoomRatio);
                        //System.out.println( "translated neighbor id:" + neighbor.getId() + " " + translatedNeighbor.x + "," + translatedNeighbor.y);
                        pushMatrix();
                        translate(translatedNeighbor.x, translatedNeighbor.y);
                        ellipse(0,0, particle_size, particle_size);
                        arrowLine(this,0,0,neighbor.getVelocity().x * 10 * xZoomRatio, neighbor.getVelocity().y * 10 * yZoomRatio, 0, 0.333f, true);
                        text(neighbor.getId(), 0,0);
                        stroke (255,0,0);
                        if ( featuredCoheseEffectsMap.containsKey(neighbor.getId())) {
                            arrowLine(this, 0,0, featuredCoheseEffectsMap.get(neighbor.getId()).x * 10, featuredCoheseEffectsMap.get(neighbor.getId()).y * 10, 0, 0.333f, true);
                        }
                        popMatrix();
                    });
            popStyle();
        }

        if ( !pauseFlock || stepFrame ) {
            flock.updateUsingAll(this);
            stepFrame = false;
        }

    }


    @Override
    public void keyTyped(KeyEvent event) {
        super.keyTyped(event);

        switch ( event.getKey()) {
            case 'p' :
                pauseFlock = !pauseFlock;
                break;
            case ' ' :
                pauseFlock = true;
                stepFrame = true;
                break;
            default:
                // no op
        }
    }

    @Override
    public void mouseMoved() {
        super.mouseMoved();

        flock.getMembers().values().stream()
                .filter( p -> {
                    return Math.abs(p.getPosition().x - mouseX) < 3 && Math.abs(p.getPosition().y - mouseY) < 3;
                })
                .forEach( (p) -> {
                    if ( p.getId() != featuredParticleId) {
                        int oldFeaturedId = featuredParticleId;
                        featuredParticleId = p.getId();
                        println("featured Particle = " + featuredParticleId);
                        flock.getMembers().get(featuredParticleId).addDrawer(FEATURED_WINDOW_DRAWER,
                                oldFeaturedId < 0 ?
                                        (app,featured) -> {   // drawing function
                                            pushMatrix();
                                            pushStyle();
                                            translate(featured.getPosition().x, featured.getPosition().y);
                                            noFill();
                                            stroke(255,255,0);
                                            ellipse(0,0,currentFeaturedWindowRange,currentFeaturedWindowRange);
                                            popStyle();
                                            popMatrix();
                                        } :
                                        flock.getMembers().get(oldFeaturedId).removeDrawer(FEATURED_WINDOW_DRAWER)

                        );
                    }

                });
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
