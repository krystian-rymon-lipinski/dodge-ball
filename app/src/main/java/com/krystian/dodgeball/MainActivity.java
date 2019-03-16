package com.krystian.dodgeball;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static int screenWidth; //game screen parameters
    public static int screenHeight;
    BallView ballView;
    SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        getScreenSize();
        ballView = new BallView(this);
        setContentView(ballView);
        startGame();
    }

    public void getScreenSize() {
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
    }

    public void startGame() {
        final int stepTime = 10; //ms
        setSensors(stepTime);
        Ball.balls.add(new PlayBall(screenWidth/2, screenHeight/2));


        final PlayBall playBall = (PlayBall) Ball.balls.get(0);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                playBall.measureAcceleration(stepTime);
                Ball.calculatePositions(Ball.balls, stepTime);
                ballView.invalidate();
                handler.postDelayed(this, stepTime);
            }
        });
    }

    public void setSensors(int stepTime) {
        Sensor rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, rotation, stepTime*1000); // 10ns * 1000 = 10ms
        //Sensor magnetSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //sensorManager.registerListener(this, magnetSensor, stepTime*1000);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
        float[] angles = SensorManager.getOrientation(rotationMatrix, event.values);
        //Log.v("X", ""+angles[0]/*Math.PI*/); //X-axis rotation, axis pointing right, <-pi; pi> angle
        Log.v("Y-axis", ""+angles[1]/Math.PI); //Y-axis rotation, axis pointing up, <-pi/2; pi/2> angle
        PlayBall playBall = (PlayBall) Ball.balls.get(0);
        float xAcceleration = angles[1]/(float)(Math.PI/2); //left-right device movement is a rotation around y-axis
        float yAcceleration = angles[0]/(float)(Math.PI); //up/right device movement is a rotation around x-axis
        playBall.setxAcceleration(xAcceleration);
        //playBall.setyAcceleration(angles[0]/(float)Math.PI);

    }
}
