import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

import java.util.*;
import java.util.stream.Collectors;

public class Main extends PApplet {

    public static void main(String[] args) {
        //
        PApplet.main("Main", args);
    }

    public void settings(){
        size(800,800, P2D);
        Particle.p = this;
        Rectangle.p = this;
    }

    float zoom = 1f;
    PVector cameraPosition = new PVector();
    PVector centerPosition = new PVector();
    PVector centerPositionClouds = new PVector();
    PVector previousMousePosition = new PVector();

    Particle particleA;
    Particle particleB;
    Particle particleC;
    Particle particleD;
    Particle particleE;

    List<Particle> particles = new ArrayList<>();
    List<Rectangle> rectangles = new ArrayList<>();

    public void setup(){
        particleA = new Particle(0, -200)
                .setSize(80)
                .setColor(40, 140, 7)
                .setIndex(0);

        particleB = new Particle(0, 200)
                .setSize(100)
                .setColor(99, 13, 122)
                .setIndex(1);

        particleC = new Particle(200, 0)
                .setSize(90)
                .setColor(209, 17, 119)
                .setIndex(2);

        particleD = new Particle(-200, -100)
                .setSize(110)
                .setColor(209, 17, 119)
                .setIndex(3);

        particleE = new Particle(0, 0)
                .setSize(100)
                .setColor(78, 54, 90)
                .setIndex(4);

        particles.add(particleA);
        particles.add(particleB);
        particles.add(particleC);
        particles.add(particleD);
        particles.add(particleE);
        particles.forEach(p -> p.setTarget(particles));

        rectMode(CENTER);
    }

    public void draw(){
        background(255);
        centerPosition.set(-cameraPosition.x, -cameraPosition.y);
        centerPositionClouds.set(centerPosition);

        pushMatrix();
        translate(width/2f, height/2f);
        scale(zoom);
        translate(-width/2f, -height/2f);
        translate(cameraPosition.x + width/2f, cameraPosition.y + height/2f);

        rectangles.forEach(Rectangle::show);

        particles.forEach(particle -> {
            particle.attracted();
            particle.update();
            particle.show();
        });

        particles.forEach(p -> p.tilesCount = 0);
        rectangles = createTiles(700, particles);

        popMatrix();
    }

    public List<Rectangle> createTiles(float area, List<Particle> particles){
        ArrayList<Rectangle> rectangles = new ArrayList<>();

        float rowsOfRectanglesToConsider = (area / Rectangle.SIZE);
        for (int col = 0; col < rowsOfRectanglesToConsider; col++) {
            for (int row = 0; row < rowsOfRectanglesToConsider; row++) {

                PVector pos = new PVector();
                float centerXValue = -(rowsOfRectanglesToConsider/2) * Rectangle.SIZE + col * Rectangle.SIZE;
                pos.x = round(centerXValue, Rectangle.SIZE);
                float centerYValue = -(rowsOfRectanglesToConsider/2) * Rectangle.SIZE + row * Rectangle.SIZE;
                pos.y = round(centerYValue, Rectangle.SIZE);

                int[] color = new int[3];

                boolean isInsideParticle = particles.stream().anyMatch(p -> p.getPosition().dist(pos) < p.getSize());
                if(isInsideParticle){

                    List<Particle> twoClosest = particles.stream()
                            .sorted((particle1, particle2) -> {
                                float dist1 = pos.dist(particle1.getPosition());
                                float dist2 = pos.dist(particle2.getPosition());
                                return Float.compare(dist1, dist2);
                            })
                            .collect(Collectors.toList()).subList(0, 2);

                    Particle particleA = twoClosest.get(0);
                    Particle particleB = twoClosest.get(1);

                    if(particleB.getPosition().dist(pos) < particleB.getSize()){

                        float distanceBetweenTwoClosest = particleA.getPosition().dist(particleB.getPosition());
                        float size1 = particleA.getSize();
                        float size2 = particleB.getSize();


                        // semi-perimeter
                        float s = (size1 + size2 + distanceBetweenTwoClosest) / 2;

                        // area of triangle
                        float A = (float) Math.sqrt(s * (s - size1) * (s - size2) * (s - distanceBetweenTwoClosest));

                        // height of triangle
                        float h = A / (distanceBetweenTwoClosest / 2);

                        // k as a optimal distance of intersection
                        Float k = (float) (Math.sqrt(1 - Math.pow(h / size1, 2)) * size1);
                        if(!k.isNaN()) {

                            float d1 = pos.dist(particleA.getPosition());
                            float d2 = pos.dist(particleB.getPosition());

                            // semi-perimeter
                            float s2 = (d1 + d2 + distanceBetweenTwoClosest) / 2;

                            // area of triangle
                            float A2 = (float) Math.sqrt(s2 * (s2 - d1) * (s2 - d2) * (s2 - distanceBetweenTwoClosest));

                            // height of triangle
                            float h2 = A2 / (distanceBetweenTwoClosest / 2);

                            // k as a optimal distance of intersection
                            float k2 = (float) (Math.sqrt(1 - Math.pow(h2 / d1, 2)) * d1);

                            if(k2 < k){
                                color = particleA.getColor();
                                particleA.tilesCount++;
                            } else if(k2 > k){
                                color = particleB.getColor();
                                particleB.tilesCount++;
                            }
                        }

                    } else {
                        color = particleA.getColor();
                        particleA.tilesCount++;
                    }

                } else {
                    color =  new int[]{59, 184, 179}; // blue color of the background
                }

                rectangles.add(new Rectangle(pos, color));
            }
        }
        return rectangles;
    }

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