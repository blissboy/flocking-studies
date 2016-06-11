package org.boyamihungry.processing;

import processing.core.PApplet;
import processing.core.PVector;

/**
 * Created by patwheaton on 6/6/16.
 */
public class DrawingUtilities {

    private static final PVector originVector = new PVector(0f,0f,0f);
    private static final PVector yNormalVector = new PVector(0f,1f,0f);
    private static final PVector xNormalVector = new PVector(1f,0f,0f);


    /*
        * Draws a lines with arrows of the given angles at the ends.
        * x0 - starting x-coordinate of line
        * y0 - starting y-coordinate of line
        * x1 - ending x-coordinate of line
        * y1 - ending y-coordinate of line
        * startAngle - angle of arrow at start of line (in radians)
        * endAngle - angle of arrow at end of line (in radians)
        * solid - true for a solid arrow; false for an "open" arrow
        */
    public static void arrowLine(
            PApplet app,
            float x0,
            float y0,
            float x1,
            float y1,
            float startAngle,
            float endAngle,
            boolean solid) {

        app.line(x0, y0, x1, y1);
        if (startAngle != 0)
        {
            arrowhead(app, x0, y0, app.atan2(y1 - y0, x1 - x0), startAngle, solid);
        }
        if (endAngle != 0)
        {
            arrowhead(app, x1, y1, app.atan2(y0 - y1, x0 - x1), endAngle, solid);
        }
    }

    /*
     * Draws an arrow head at given location
     * x0 - arrow vertex x-coordinate
     * y0 - arrow vertex y-coordinate
     * lineAngle - angle of line leading to vertex (radians)
     * arrowAngle - angle between arrow and line (radians)
     * solid - true for a solid arrow, false for an "open" arrow
     */
    public static void arrowhead(
            PApplet app,
            float x0,
            float y0,
            float lineAngle,
            float arrowAngle,
            boolean solid) {

        float phi;
        float x2;
        float y2;
        float x3;
        float y3;
        final float SIZE = 8;

        x2 = x0 + SIZE * app.cos(lineAngle + arrowAngle);
        y2 = y0 + SIZE * app.sin(lineAngle + arrowAngle);
        x3 = x0 + SIZE * app.cos(lineAngle - arrowAngle);
        y3 = y0 + SIZE * app.sin(lineAngle - arrowAngle);
        if (solid)
        {
            app.triangle(x0, y0, x2, y2, x3, y3);
        }
        else
        {
            app.line(x0, y0, x2, y2);
            app.line(x0, y0, x3, y3);
        }
    }

    public static PVector getOriginVector() {
        //return new PVector(0f,0f,0f);
        return DrawingUtilities.originVector.copy();
    }


//    public QuadraticEquation getQEQFromThreePoints(PVector p1, PVector p2, PVector p3) {
//        return getQEQFromThreePoints(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
//    }
//    public QuadraticEquation getQEQFromThreePoints(float p1x, float p1y, float p2x, float p2y, float p3x, float p3y) {
//
//
//        float a,b,c;
//
//
//
//        return (x) -> {
//
//        };
//
//
//
//
//    }
//
//
//    @FunctionalInterface
//    public interface QuadraticEquation {
//        int getY(int x);
//    }
}
