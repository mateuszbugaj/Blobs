import Graphics.Rectangle;
import Physic.Particle;
import Utils.Color;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Main extends PApplet {

    public static void main(String[] args) {
        //
        PApplet.main("Main", args);
    }

    public void settings(){
        size(800,800);
        Particle.p = this;
        Rectangle.p = this;
    }

    float zoom = 1f;
    PVector cameraPosition = new PVector();
    PVector centerPosition = new PVector();
    PVector previousMousePosition = new PVector();

    Particle particleA;
    Particle particleB;
    Particle particleC;
    Particle particleD;
    Particle particleE;

    List<Particle> particles = new ArrayList<>();


    public void setup(){
        particleA = new Particle(0, -200)
                .setSize(80)
                .setColor(40, 140, 7)
                .setIndex(0);

        particleB = new Particle(-100, 50)
                .setSize(80)
                .setColor(99, 13, 122)
                .setIndex(1);

        particleC = new Particle(200, 0)
                .setSize(130)
                .setColor(150, 50, 34)
                .setIndex(2);

        particleD = new Particle(-200, 0)
                .setSize(90)
                .setColor(209, 34, 119)
                .setIndex(3);

        particles.add(particleA);
        particles.add(particleB);
        particles.add(particleC);
        particles.add(particleD);

        rectMode(CENTER);
    }

    public void draw(){
        background(255);
        centerPosition.set(-cameraPosition.x, -cameraPosition.y);
        pushMatrix();

        translate(width/2f, height/2f);
        scale(zoom);
        translate(-width/2f, -height/2f);
        translate(cameraPosition.x + width/2f, cameraPosition.y + height/2f);

        createTiles3(800, particles).forEach(Rectangle::show);

        particles.forEach(particle -> {
            particle.attracted(particles);
            particle.update();
        });

//        particleC.setPosition(centerPosition);

        popMatrix();

        fill(0);
        text(frameRate, 0, 20);
    }

    /**
     *
     * @param area - width of rectangle created from generated tiles
     * @param particles - list of Physic.Particle objects
     * @return - list of Graphics.Rectangle objects with correct colors denoting each particle and background
     */
    public List<Rectangle> createTiles3(float area, List<Particle> particles){
        ArrayList<Rectangle> rectangles = new ArrayList<>();
        particles.forEach(p -> p.setTilesCount(0));
        Color backgroundColor = new Color(255, 255, 255);
        float rowsOfRectanglesToConsider = (area / Rectangle.SIZE);

        BiFunction<Integer, Integer, PVector> calculatePosition = new BiFunction<Integer, Integer, PVector>() {
            @Override
            public PVector apply(Integer col, Integer row) {
                PVector pos = new PVector();
                int centerXValue =(int) (-(rowsOfRectanglesToConsider/2) * Rectangle.SIZE + col * Rectangle.SIZE);
                pos.x = round(centerXValue, Rectangle.SIZE);
                int centerYValue =(int) (-(rowsOfRectanglesToConsider/2) * Rectangle.SIZE + row * Rectangle.SIZE);
                pos.y = round(centerYValue, Rectangle.SIZE);

                return pos;
            }
        };

        /*
            Placing blobs
         */
        for (int col = 0; col < rowsOfRectanglesToConsider; col++) {
            for (int row = 0; row < rowsOfRectanglesToConsider; row++) {

                PVector pos = calculatePosition.apply(col, row);

                /*
                    Check in which particles tile can be if it is in any.
                    Then sort them based on their distance to this tile divided by size.
                    Then take first and extract color.
                 */

                if(particles.stream().anyMatch(p -> p.getPosition().dist(pos) < p.getSize())){
                    Color color = particles.stream()
                        .filter(p -> p.getPosition().dist(pos) < p.getSize())
                        .min((particle1, particle2) -> {
                            float dist1 = pos.dist(particle1.getPosition()) / (particle1.getSize()/2);
                            float dist2 = pos.dist(particle2.getPosition()) / (particle2.getSize()/2);
                            return Float.compare(dist1, dist2);
                        })
                        .map(Particle::getColor).get();

//                    PVector posLeft = calculatePosition.apply(col, row);
//                    PVector posTop = calculatePosition.apply(col, row - 1);
//
//
//                    boolean isOnTheEdge = rectangles.stream().noneMatch(rectangle -> {
//                        PVector position = rectangle.getPosition();
//                        return (position.x == posLeft.x && position.y == posLeft.y) || (position.x == posTop.x && position.y == posTop.y);
//                    });
//
//                    if(isOnTheEdge){
//                        color = new Color(0, 0, 0);
//                    }

                    rectangles.add(new Rectangle(pos, color));
                }
            }
        }

        /*
            Placing borders
         */
//        for (int col = 0; col < rowsOfRectanglesToConsider; col++) {
//            for (int row = 0; row < rowsOfRectanglesToConsider; row++) {
//
//                PVector pos = calculatePosition.apply(col, row);
//
//                if(particles.stream().anyMatch(p -> p.getPosition().dist(pos) < p.getSize())){
//                    PVector posLeft = calculatePosition.apply(col - 1, row);
//                    PVector posRight = calculatePosition.apply(col + 1, row);
//                    PVector posTop = calculatePosition.apply(col, row - 1);
//                    PVector posBottom = calculatePosition.apply(col, row + 1);
//
//                    List<PVector> neighbourPosition = new ArrayList<>();
//                    neighbourPosition.add(posLeft);
//                    neighbourPosition.add(posRight);
//                    neighbourPosition.add(posTop);
//                    neighbourPosition.add(posBottom);
//
//                    rectangles.stream().
//
//
//
//                }
//
//            }
//        }



        return rectangles;
    }

    /**
     *
     * @param value1 - value to be rounded
     * @param value2 - value to which round
     * @return - value1 rounded to value2
     */
    public float round(float value1, float value2){
        if(value1%value2 < value2/2){
            value1 = (int)(value1/value2)*value2;
        } else {
            value1 = (int)(value1/value2)*value2 + value2;
        }
        return value1;
    }

    public void mousePressed(MouseEvent event){
        if(event.getButton()==LEFT){
            previousMousePosition.set(mouseX, mouseY);
        }
    }

    public void mouseDragged(MouseEvent event){
        if(event.getButton()==LEFT){
            PVector mouseDisplacement = new PVector(mouseX - previousMousePosition.x, mouseY - previousMousePosition.y);
            cameraPosition = cameraPosition.add(mouseDisplacement.div(zoom));
            previousMousePosition.set(mouseX, mouseY);
        }
    }

    public void mouseWheel(MouseEvent event){
        zoom += event.getCount()/10f;
        if(zoom<0.1){
            zoom = 0.1f;
        }
        if(zoom>2.5){
            zoom=2.5f;
        }
    }

}