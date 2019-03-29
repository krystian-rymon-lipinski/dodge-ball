package com.krystian.dodgeball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class BallView extends View {

    Paint paint, paint2, paint3;

    public BallView(Context context) {
        super(context);

        paint = new Paint();
        paint.setColor(getResources().getColor(R.color.playBallColor));
        paint2 = new Paint(); //other balls color - black by default
        paint3 = new Paint();
        paint3.setColor(getResources().getColor(R.color.timeTextColor));
        paint3.setTextSize(40);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, 10, 10, paint); //show left up corner
        for(Ball b : Ball.balls) {
            if(b.getClass() == PlayBall.class)
                canvas.drawCircle(b.getxPosition(), b.getyPosition(), b.getRadius(), paint); //playBall
            else
                canvas.drawCircle(b.getxPosition(), b.getyPosition(), b.getRadius(), paint2); //other balls
        }
        float time = (float) GameActivity.time/1000;
        canvas.drawText(getResources().getString(R.string.game_time, time),
                (float)GameActivity.screenHeight/7, (float)GameActivity.screenHeight/7, paint3);
        if(GameActivity.gameState == GameState.PAUSED) {
            canvas.drawText(getResources().getString(R.string.paused), (float)GameActivity.screenWidth/2,
                    (float)GameActivity.screenHeight/2, paint3);
            setAlpha(0.5f);
        }
        else setAlpha(1);

    }
}
