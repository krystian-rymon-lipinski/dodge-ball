package com.krystian.dodgeball;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    public static int screenWidth; //game screen parameters
    public static int screenHeight;
    BallView ballView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                Ball.calculatePositions(Ball.balls, stepTime);
                ballView.invalidate();
                handler.postDelayed(this, stepTime);
            }
        });
    }
}
