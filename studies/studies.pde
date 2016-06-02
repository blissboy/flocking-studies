
  PVector origin = new PVector(0,0,0);
  PVector vec1 = new PVector(-70,400);
  PVector vec2 = new PVector(500,300);
  PVector vec3 = PVector.sub(vec2,vec1);
  //PVector vec5 = PVector.sub(vec1,vec2);
  PVector vec4;// = PVector.random2D();

void setup() {
  
  size(1200,1200);
  vec4 = PVector.cross(vec2, origin, null);
  
}

void draw() {
  
  pushStyle();
  translate(600,600);
  stroke(0,0,0);
  fill(0,0,0,255);
  line(0,0,vec1.x, vec1.y);
  line(0,0,vec2.x, vec2.y);
  stroke(0,0,255);
  line(0,0,vec3.x, vec3.y);
  stroke(0,255,0);
  //line(0,0,vec5.x, vec5.y);
  
  stroke(0,255,0);
  arrow(0,0,vec4.x, vec4.y);
  
  popStyle();
  
  //line(0,0,70, 400);
  //line(0,0,500, 300);
  
}
void arrow(int x1, int y1, int x2, int y2) {
  line(x1, y1, x2, y2);
  pushMatrix();
  translate(x2, y2);
  float a = atan2(x1-x2, y2-y1);
  rotate(a);
  line(0, 0, -10, -10);
  line(0, 0, 10, -10);
  popMatrix();
} 



void drawArrow2(float x1, float y1, float x0, float y0, float beginHeadSize, float endHeadSize, boolean filled) {

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