package com.krystian.dodgeball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class BallView extends View {

    Paint paint;

    public BallView(Context context) {
        super(context);
        Ball.balls.add(new Ball(300, 200));
        Ball.balls.add(new Ball(500, 132));
        Ball.balls.add(new Ball(212, 602));
        Ball.balls.add(new Ball(245, 308));
        Ball.balls.add(new Ball(145, 100));

        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for(Ball b : Ball.balls) {
            canvas.drawCircle(b.getxPosition(), b.getyPosition(),
                    b.getRadius(), paint);
        }

    }
}
