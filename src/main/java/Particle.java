import processing.core.PApplet;
import processing.core.PVector;

import java.util.List;

public class Particle {
    static PApplet p;
    private PVector position;
    private PVector velocity = new PVector();
    private List<Particle> targets;

    private float size = 200;
    private float newSize = size;
    private int index;

    private int tiles;
    int tilesCount = 0;
    private int[] color = new int[4];

    public Particle(float posX, float posY) {
        position = new PVector(posX, posY);
        velocity.set(p.random(-0.8f,0.8f), 0);
    }

    public void setTarget(List<Particle> targets) {
        this.targets = targets;
    }

    public Particle setSize(float size) {
        this.tiles = (int) ((Math.PI*Math.pow((size), 2)) / Math.pow(Rectangle.SIZE,2)); // how many rectangles are in the blob
        this.size = size;
        this.newSize = this.size;
        return this;
    }

    public Particle setColor(int... color) {
        this.color[0] = color[0];
        this.color[1] = color[1];
        this.color[2] = color[2];
        this.color[3] = 50;
         return this;
    }

    public Particle setIndex(int index) {
        this.index = index;
        return this;
    }

    public int getIndex() {
        return index;
    }

    public Particle setPosition(PVector position) {
        this.position = position;
        return this;
    }

    public PVector getPosition() {
        return position;
    }

    public float getSize() {
        return newSize;
    }

    public int[] getColor() {
        return color;
    }

    public void show(){
//        p.fill(9, 143, 134, 20);
//        p.stroke(0);
//        p.ellipse(position.x, position.y, size*2, size*2);
    }

    public void update(){
        position.add(velocity);

        if(tilesCount < tiles * 0.99){
            newSize += 0.5;
        } else if(tilesCount> tiles * 1.01){
            newSize -= 0.5;
        }
    }

    public void attracted(){

        /*
            Attraction function is defined by sigmoid type function
            y = ( -g / (0.5 + exp( k*( x-(t/2) ) ) ) ) + g
            where
                g is a step value (denotes max and min)
                k is a slope value
                t is a 0 point (Equilibrium point) roughly x = 2*t

         */
        velocity = new PVector();

        for(Particle target:targets) {
            PVector targetVector = new PVector();
            targetVector.set(target.position).sub(position).normalize();

            float x = target.position.dist(position) / 20;//target.position.dist(position) / 20;
            float t = 25;//size/4;
            float g = 2f;//2f;
            float k = 1;//1;
            float y = (float) (-g / (0.5 + Math.exp((k * (x - (t / 2))))) + g);

            velocity.add(targetVector.mult(y));
        }

    }

    @Override
    public String toString() {
        return "Particle{" +
                "index=" + index +
                ", size=" + size +
                '}';
    }
}
