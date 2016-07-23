 /**
 * Scale 
 * by Denis Grutze. 
 * 
 * Paramenters for the scale() function are values specified 
 * as decimal percentages. For example, the method call scale(2.0) 
 * will increase the dimension of the shape by 200 percent. 
 * Objects always scale from the origin. 
 */

float a = 0.0;
float s = 0.0;

float px = 500;
float py = 300;
PVector p = new PVector(px, py);

float n1x = 430;
float n1y = 270;
PVector n1 = new PVector(n1x, n1y);

float w = 300;
float h = w;

float fx = 1200;
float fy = w/2;
PVector f = new PVector(fx, fy);


float r = 150;

float pSize = 5;

void setup() {
  size(1920, 1080);
  frameRate(30);
}

void draw() {
  background(190);

  pushStyle();
  pushMatrix();
  rectMode(CENTER);
  stroke(255, 255, 0); // yellow
  noFill();
  rect(p.x, p.y, r, r);  

  fill(120,120,200);
  noStroke();
  ellipse(p.x,p.y, pSize * 3, pSize * 3);
  
  ellipse(n1.x, n1.y, pSize, pSize);

  //translate(fx - px, fy - py);
  //scale(w/r,h/r); 
  
  stroke(123, 255, 0); // yellow
  noFill();
  rectMode(CENTER);
  //rect(p.x, p.y, r, r);  
  rect(translatedX(p.x), translatedY(p.y), (w), (h));
  
  fill(120,255,200);
  noStroke();
  
  //ellipse(p.x,p.y, pSize * 3, pSize * 3);
  ellipse(translatedX(p.x),translatedY(p.y), (pSize * 3), (pSize * 3));
  
  //ellipse(n1.x, n1.y, pSize, pSize);
  ellipse(translatedX(n1.x), translatedY(n1.y), pSize, pSize);

  //translate(fx - px, fy - py);
  //scale(w/r,h/r); 
  
  
  
  popMatrix();
  popStyle();
}

float translatedX(float x) {
  return ((w * (x - px)) / r) + fx;
}
float translatedY(float y) {
  return ((h * (y - py)) / r) + fy;
}