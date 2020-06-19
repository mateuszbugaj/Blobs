package Graphics;

import Utils.Color;
import processing.core.PApplet;
import processing.core.PVector;

public class Rectangle {
    public static PApplet p;
    PVector position;
    public static float SIZE = 5;
    int[] color;


    public Rectangle(PVector position, Color color) {
        this.position = position;
        this.color = color.getValues();
    }

    public PVector getPosition() {
        return position;
    }

    public void show(){
        p.pushMatrix();
        p.translate(position.x, position.y);
        p.noStroke();

        p.fill(color[0], color[1], color[2]);

        p.rect(-1, -1, SIZE+1, SIZE+1);

        p.popMatrix();
    }
}
