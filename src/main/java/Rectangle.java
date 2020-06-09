import processing.core.PApplet;
import processing.core.PVector;

public class Rectangle {
    static PApplet p;
    PVector position;
    static float SIZE = 4;
    int[] color;

    public Rectangle(PVector position, int... color) {
        this.position = position;
        this.color = color;
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
