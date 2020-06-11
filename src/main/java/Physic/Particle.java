package Physic;

import Graphics.Rectangle;
import Utils.Color;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.List;
import java.util.stream.Collectors;

public class Particle {
    public static PApplet p;
    private PVector position;
    private PVector velocity = new PVector();

    private float size = 200;
    private float newSize = size;
    private int index;

    private int tiles;
    private int tilesCount = 0;
    private Color color;

    public Particle(float posX, float posY) {
        position = new PVector(posX, posY);
        velocity.set(p.random(-0.8f,0.8f), 0);
    }

    public Particle setSize(float size) {
        this.tiles = (int) ((Math.PI*Math.pow((size), 2)) / Math.pow(Rectangle.SIZE,2)); // how many rectangles are in the blob
        this.size = size;
        this.newSize = this.size;
        return this;
    }

    public Particle setColor(int... color) {
        this.color = new Color(color);
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

    public Color getColor() {
        tilesCount++;
        return color;
    }

    public int getTilesCount() {
        return tilesCount;
    }

    public Particle setTilesCount(int tilesCount) {
        this.tilesCount = tilesCount;
        return this;
    }

    public void update(){
        position.add(velocity);

        if(tilesCount < tiles * 0.95){
            newSize += 0.5;
        } else if(tilesCount> tiles * 1.05){
            newSize -= 0.5;
        }
    }

    public void attracted(List<Particle> targets){

        /*
            Extracting positions of particles from objects and excluding instance position
         */
        List<PVector> positions = targets.stream()
                .map(Particle::getPosition)
                .filter(position -> !position.equals(this.position))
                .collect(Collectors.toList());

        /*
            Attraction is defined by sigmoid -like function
            y = ( -g / (0.5 + exp( k*( x-(t/2) ) ) ) ) + g
            where
                g is a step value (denotes max and min)
                k is a slope value
                t is a 0 point (Equilibrium point) roughly x = 2*t
         */

        velocity = new PVector();

        for(PVector target:positions) {
            PVector targetVector = new PVector();
            targetVector.set(target).sub(position).normalize();

            float x = target.dist(position) / 20;
            float t =  2500/size;//20;//1800/size;//size / 10 ;//3000/size;//30;
            float g = 1f;
            float k = 2;
            float y = (float) (-g / (0.5 + Math.exp((k * (x - (t / 2))))) + g);

            velocity.add(targetVector.mult(y));
        }

        // add attraction to the center
        PVector toCenter = new PVector().set(0, 0).sub(position).mult(0.005f);
        velocity.add(toCenter);
    }

    @Override
    public String toString() {
        return "Physic.Particle{" +
                "index=" + index +
                ", size=" + size +
                '}';
    }
}
