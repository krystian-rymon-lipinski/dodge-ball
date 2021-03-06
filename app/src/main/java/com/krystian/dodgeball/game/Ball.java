package com.krystian.dodgeball.game;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import static com.krystian.dodgeball.game.GameActivity.gameState;

public class Ball implements Parcelable {

    private float radius; //in relation to device's measurements
    private float xPosition, yPosition;
    protected float xVelocity, yVelocity;


    public Ball(int xPosition, int yPosition) {
        radius = 0.02f * Math.min(GameActivity.screenWidth, GameActivity.screenHeight);
        this.xPosition = xPosition;
        this.yPosition = yPosition;

        float angle = (float) Math.random(); // 0 < angle < 1; sin(alfa)
        float velocity = 0.5f;
        xVelocity = velocity * angle; //angle * velocity = sin(alfa) * velocity = velocity_x
        yVelocity = velocity * (1-angle);
    }

    protected Ball(Parcel in) {
        radius = in.readFloat();
        xPosition = in.readFloat();
        yPosition = in.readFloat();
        xVelocity = in.readFloat();
        yVelocity = in.readFloat();
    }

    public static final Creator<Ball> CREATOR = new Creator<Ball>() {
        @Override
        public Ball createFromParcel(Parcel in) {
            return new Ball(in);
        }

        @Override
        public Ball[] newArray(int size) {
            return new Ball[size];
        }
    };

    public float getRadius() {
        return radius;
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

    public void calculatePositions(ArrayList<Ball> balls, int stepTime) {
        for(int i=0; i<balls.size(); i++) {
            //check for collisions
            if(balls.get(i).getClass() == PlayBall.class) {
                for(Ball bb : balls) {
                    double xDiff = Math.abs(balls.get(i).xPosition - bb.xPosition);
                    double yDiff = Math.abs(balls.get(i).yPosition - bb.yPosition);
                    double distanceBetween = Math.sqrt(xDiff*xDiff + yDiff*yDiff); //between play ball and one of normal ones
                    if(distanceBetween < 2*radius && distanceBetween != 0) //you hit one of the balls
                        gameState = GameState.LOST;
                }
            }

            if(balls.get(i).getxPosition() - balls.get(i).radius <= 0 ||
                    balls.get(i).getxPosition() + balls.get(i).radius >= GameActivity.screenWidth)
                balls.get(i).xVelocity *= -1; //hitting the wall
            if(balls.get(i).getyPosition() - balls.get(i).radius <= 0 ||
                    balls.get(i).getyPosition() + balls.get(i).radius >= GameActivity.screenHeight)
                balls.get(i).yVelocity *= -1;

            balls.get(i).xPosition += balls.get(i).xVelocity * stepTime; //update balls' positions
            balls.get(i).yPosition += balls.get(i).yVelocity * stepTime;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(radius);
        dest.writeFloat(xPosition);
        dest.writeFloat(yPosition);
        dest.writeFloat(xVelocity);
        dest.writeFloat(yVelocity);
    }
}

