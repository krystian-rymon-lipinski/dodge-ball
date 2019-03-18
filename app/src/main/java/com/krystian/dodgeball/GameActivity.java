package com.krystian.dodgeball;

import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements SensorEventListener {

    public static int screenWidth; //game screen parameters
    public static int screenHeight;
    public static int time = 0; //time of playing; needs to be updated in handler and showed in BallView
    BallView ballView;
    SensorManager sensorManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        getScreenSize();
        ballView = new BallView(this);
        setContentView(ballView);
        displayMessage(); //on how to orient the device before playing
    }

    public void getScreenSize() {
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
    }

    public void displayMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.got_it, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startGame();
                    }
                });
        builder.show();
    }

    public void startGame() {
        Ball.balls.clear(); //if there is a restart
        final int stepTime = 10; //ms
        Sensor rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR); //set sensor
        sensorManager.registerListener(this, rotationSensor, stepTime * 1000); // 10ns * 1000 = 10ms
        Ball.balls.add(new PlayBall(screenWidth / 2, screenHeight / 2));

        final PlayBall playBall = (PlayBall) Ball.balls.get(0);
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                playBall.measureAcceleration(stepTime);
                Ball.calculatePositions(Ball.balls, stepTime);
                ballView.invalidate();
                if (time % 10000 == 0) { //add another ball to avoid every 10 seconds - including time 0
                    Random random = new Random();
                    Ball.balls.add(new Ball(random.nextInt(screenWidth), random.nextInt(screenHeight)));
                }
                time += stepTime;

                handler.postDelayed(this, stepTime);

            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }


    boolean sensorActivated = false; // to measure X-axis angle only once - at the start
    float xAxisStart = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            float[] angles = SensorManager.getOrientation(rotationMatrix, event.values); //rotation angles in degrees around three axis

            PlayBall playBall = (PlayBall) Ball.balls.get(0);

            //Y-axis rotation, axis pointing up, <-pi/2; pi/2> angle
            float xAcceleration = angles[1]/(float)(Math.PI/2); //left/right device movement is a rotation around y-axis
            playBall.setxAcceleration(xAcceleration);

            /*X-axis rotation, axis pointing right, <-pi; pi> angle; 0 degree is flat
            user may hold the device more or less tilted around X-axis, so y acceleration
            needs to be calculated as a difference between an actual degree and the one from the start */
            if(!sensorActivated) {
                xAxisStart = angles[2];
                sensorActivated = true;
            }
            float yAcceleration = (angles[2] - xAxisStart) / (float) (Math.PI); //up/down device movement is a rotation around x-axis
            playBall.setyAcceleration(yAcceleration);
        }
    }
}
