package com.krystian.dodgeball;

import java.util.ArrayList;

public class Ball {
    public static ArrayList<Ball> balls = new ArrayList<>();

    private float radius; //in relation to device's measurements
    private float mass; //to calculate bouncing
    private float xPosition, yPosition;
    private float xVelocity, yVelocity;


    public Ball(int xPosition, int yPosition) {
        mass = 10;
        radius = 0.02f * Math.min(MainActivity.screenWidth, MainActivity.screenHeight);
        this.xPosition = xPosition;
        this.yPosition = yPosition;

        float angle = (float) Math.random();
        float velocity = 1;
        xVelocity = velocity * angle;
        yVelocity = velocity * (1-angle);
    }

    public float getRadius() {
        return radius;
    }

    public float getMass() {
        return mass;
    }

    public float getxPosition() {
        return xPosition;
    }

    public float getyPosition() {
        return yPosition;
    }

    public float getxVelocity() {
        return xVelocity;
    }

    public float getyVelocity() {
        return yVelocity;
    }

    public static void calculatePositions(ArrayList<Ball> balls, int stepTime) {
        for(Ball b : balls) {
            if(b.getxPosition() - b.radius <= 0 || b.getxPosition() + b.radius >= MainActivity.screenWidth)
                b.xVelocity *= -1; //hitting the wall
            if(b.getyPosition() - b.radius <= 0 || b.getyPosition() + b.radius >= MainActivity.screenHeight)
                b.yVelocity *= -1;

            b.xPosition += b.xVelocity * stepTime;
            b.yPosition += b.yVelocity * stepTime;
        }
    }
}

