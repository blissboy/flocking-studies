package org.boyamihungry.processing.flocking;

import processing.core.PApplet;
import processing.core.PVector;

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
    public static final PVector originVector = new PVector(0,0);

    public static final int WIDTH = 1440;
    public static final int HEIGHT = 1080;
    public static final int NUM_PARTICLES = 40;

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
                return originVector;
            }

        };

        Particle.ParticleFollowCalculator follow = (app, affectingP, affectedP) -> {
            // get distance between
            float dist = affectingP.getPosition().dist(affectedP.getPosition());
            if ( dist < FOLLOW ) {
                return affectingP.getVelocity().copy().mult(FOLLOW_FORCE);
            } else {
                return originVector;
            }

        };


        Particle.ParticleCoheseCalculator cohese = (app, affectingP, affectedP) -> {
            // get distance between
            float dist = affectingP.getPosition().dist(affectedP.getPosition());
            if ( dist < COHESE ) {
                return affectingP.getPosition().copy().mult(COHESE_FORCE);
            } else {
                return originVector;
            }

        };

        SimpleFlock flock = new SimpleFlock();

        for (int i = 0; i < NUM_PARTICLES; i++) {
            flock.addMember(
                    new Particle(
                            i,
                            new PVector(random(WIDTH), random(HEIGHT)),
                            originVector,
                            PVector.random2D().mult(5),
                            (app,p) -> {
                                ellipse(p.getPosition().x, p.getPosition().y,5,5);
                            },
                            avoid,
                            follow,
                            cohese
                    )
            );
        }



        try {
            Thread.sleep(1000);
        } catch (InterruptedException iE) {
            // do nothing
        }
    }


    public void draw() {




    }



    static public void main(String[] passedArgs) {

        String[] appletArgs = new String[]{"org.boyamihungry.processing.flocking.FlockingExample"};
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }


    void drawArrow(float x1, float y1, float x0, float y0, float beginHeadSize, float endHeadSize, boolean filled) {

      PVector d = new PVector(x1 - x0, y1 - y0);
      d.normalize();

      float coeff = 1.5f;

      strokeCap(SQUARE);

      line(x0+d.x*beginHeadSize*coeff/(filled?1.0f:1.75f),
            y0+d.y*beginHeadSize*coeff/(filled?1.0f:1.75f),
            x1-d.x*endHeadSize*coeff/(filled?1.0f:1.75f),
            y1-d.y*endHeadSize*coeff/(filled?1.0f:1.75f));

      float angle = atan2(d.y, d.x);

      if (filled) {
        // begin head
        pushMatrix();
        translate(x0, y0);
        rotate(angle+PI);
        triangle(-beginHeadSize*coeff, -beginHeadSize,
                 -beginHeadSize*coeff, beginHeadSize,
                 0, 0);
        popMatrix();
        // end head
        pushMatrix();
        translate(x1, y1);
        rotate(angle);
        triangle(-endHeadSize*coeff, -endHeadSize,
                 -endHeadSize*coeff, endHeadSize,
                 0, 0);
        popMatrix();
      }
      else {
        // begin head
        pushMatrix();
        translate(x0, y0);
        rotate(angle+PI);
        strokeCap(ROUND);
        line(-beginHeadSize*coeff, -beginHeadSize, 0, 0);
        line(-beginHeadSize*coeff, beginHeadSize, 0, 0);
        popMatrix();
        // end head
        pushMatrix();
        translate(x1, y1);
        rotate(angle);
        strokeCap(ROUND);
        line(-endHeadSize*coeff, -endHeadSize, 0, 0);
        line(-endHeadSize*coeff, endHeadSize, 0, 0);
        popMatrix();
      }
    }

}
