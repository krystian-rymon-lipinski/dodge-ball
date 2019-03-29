package com.krystian.dodgeball;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Random;

public class GameActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    public static int screenWidth; //game screen parameters
    public static int screenHeight;
    public static int time = 0; //time of playing; needs to be updated in handler and showed in BallView
    public static GameState gameState = GameState.STARTING;

    BallView ballView;
    SensorManager sensorManager;
    Sensor rotationSensor;
    Handler handler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if(isScreenOn()) {
            getScreenSize();
            ballView = new BallView(this);
            ballView.setOnClickListener(this);
            setContentView(ballView);
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            displayMessage(); //on how to orient the device before playing
            Log.v("Created", "now");
        //}
    }

    public boolean isScreenOn() {
        boolean on;
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH)
            on = powerManager.isInteractive();
        else on = powerManager.isScreenOn();
        return on;
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
        time = 0;
        gameState = GameState.RUNNING;
        Ball.balls.add(new PlayBall(screenWidth / 2, screenHeight / 2));
        handler = new Handler();

        play();
    }

    public void play() {

        final PlayBall playBall = (PlayBall) Ball.balls.get(0);
        final int stepTime = 10; //ms
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME);

        handler.post(new Runnable() {
            @Override
            public void run() {
                playBall.measureAcceleration(stepTime);
                playBall.calculatePositions(Ball.balls, stepTime);
                if (time % 10000 == 0) { //add another ball to avoid every 10 seconds - including time 0
                    Random random = new Random();
                    Ball.balls.add(new Ball(random.nextInt(screenWidth), random.nextInt(screenHeight)));
                }
                //if(time == 500) gameState = GameState.LOST;
                time += stepTime;
                ballView.invalidate();

                if(gameState == GameState.RUNNING) handler.postDelayed(this, stepTime);
                else {
                    handler.removeCallbacks(this); //game is paused
                    if(gameState == GameState.LOST) finishGame(); //or lost
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.equals(ballView)) {
            if(gameState == GameState.RUNNING) {
                pauseGame();
            }
            else if(gameState == GameState.PAUSED) {
                gameState = GameState.RUNNING;
                play();
            }
        }
    }

    public void pauseGame() {
        gameState = GameState.PAUSED;
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameState == GameState.PAUSED)
            ballView.setOnClickListener(this); //user returned to this activity; game is currently paused
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseGame();
        ballView.setOnClickListener(null);
        Log.v("Paused", "now");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        time = 0; //if key back is pressed and game started again
        Ball.balls.clear();
        Log.v("Destroyed", "now");
    }

    public void finishGame() {
        HighscoreDatabase customDb = new HighscoreDatabase();
        customDb.accessDatabase(this);
        boolean isHighscore = customDb.checkForHighscore((float)time/1000);
        displayDialog(isHighscore);

    }

    public void displayDialog(boolean isHighscore) {
        EndgameDialogFragment dialog = new EndgameDialogFragment();
        Bundle result = new Bundle();
        //show a "normal" dialog or the custom one with editText if score is good enough for table
        result.putBoolean(EndgameDialogFragment.isHighscore, isHighscore);
        dialog.setArguments(result);
        dialog.show(getSupportFragmentManager(), "endgame_dialog");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }


    boolean sensorActivated = false; // to measure X-axis angle only once - at the start
    float xAxisStart = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR && Ball.balls.size() > 0) {

            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            float[] angles = SensorManager.getOrientation(rotationMatrix, event.values); //rotation angles in degrees around three axis

            PlayBall playBall = (PlayBall) Ball.balls.get(0);

            //Y-axis rotation, axis pointing up, <-pi/2; pi/2> angle
            float xAcceleration = angles[1]/(float)(Math.PI/2); //left/right device movement is a rotation around y-axis
            playBall.setxAcceleration(xAcceleration);

            /*X-axis rotation, axis pointing right, <-pi; pi> angle; 0 degree is flat
            user may hold the device more or less tilted around X-axis when starting game, so y acceleration
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
