package com.krystian.dodgeball;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;

public class PlayBall extends Ball {

    private static final float ACCELERATION_COEFFICIENT = 0.001f; //max value (when rotation angle is maxed)

    private float xAcceleration;
    private float yAcceleration;

    public PlayBall(int width, int height) {
        super(width, height);
        xAcceleration = 0;
        yAcceleration = 0;
    }

    public float getxAccelereation() { return xAcceleration; }
    public void setxAcceleration(float xAcceleration) {
        this.xAcceleration = xAcceleration * ACCELERATION_COEFFICIENT;
    }

    public float getyAcceleration() { return yAcceleration; }
    public void setyAcceleration(float yAcceleration) {
        this.yAcceleration = yAcceleration * ACCELERATION_COEFFICIENT; }

    public void measureAcceleration(int stepTime) {


/*
        if(getxPosition() <=0 || getxPosition() >= MainActivity.screenWidth)
            xAcceleration *= -1;
        if(getyPosition() <=0 || getyPosition() >= MainActivity.screenHeight)
            yAcceleration *= -1;
*/

        //if(xVelocity > 0) {
            xVelocity += xAcceleration * stepTime; //update main ball's velocity
            yVelocity += yAcceleration * stepTime;
        //} else {
         //   xVelocity -= xAcceleration * stepTime;
         //   yVelocity -= yAcceleration * stepTime;
        //}
    }

}
