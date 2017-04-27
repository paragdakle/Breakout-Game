package com.hci.project.breakout.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.hci.project.breakout.R;
import com.hci.project.breakout.activity.GameActivity;

/**
 * Created by root on 24/4/17.
 */

public class GameView extends View {

    private Context mContext;
    int ballX = -1;
    int ballY = -1;
    boolean isRestart = true;
    int paddleX = -1;
    int paddleY = -1;
    int paddleWidth = 50;
    int paddleHeight = 20;
    private int xVelocity = 20;
    private int yVelocity = 10;
    private Handler h;
    private final int FRAME_RATE = 30;
    View paddle;
    GameActivity activity;
    float[][] bricks;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        h = new Handler();
        activity = null;
    }

    private Runnable r = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    public void setPaddle(GameActivity activity, View paddle) {
        this.paddle = paddle;
        this.activity = activity;
    }

    public void startGame() {
        h.postDelayed(r, FRAME_RATE);
    }

    public void initialize() {
        isRestart = true;
        invalidate();
    }

    protected void onDraw(Canvas c) {
        BitmapDrawable ball = (BitmapDrawable) ContextCompat.getDrawable(mContext, R.drawable.ic_ball);
        if(isRestart) {
            int rows = 4;
            int columns = this.getWidth() / ball.getBitmap().getWidth();
            bricks = new float[rows][columns];
            for(int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    bricks[i][j] = 1;
                }
            }
        }
        drawBricks(c);
        if ((ballX < 0 && ballY < 0) || isRestart) {
            if(this.activity != null) {
                ballX = activity.getPaddleMean();
            }
            else
                ballX = 50;
            ballY = this.getHeight() - ball.getBitmap().getHeight();
            c.drawBitmap(ball.getBitmap(), ballX, ballY, null);
            isRestart = false;
        } else {
            ballX += xVelocity;
            ballY += yVelocity;
            if ((ballX > this.getWidth() - ball.getBitmap().getWidth()) || (ballX < 0)) {
                xVelocity = xVelocity * -1;
            }
            if (ballY < 0) {
                yVelocity = yVelocity * -1;
            }
            if((ballY > this.getHeight() - ball.getBitmap().getHeight())) {
                if(!activity.isOnPaddle(ballX - xVelocity)) {
                    activity.decreaseLife();
                    initialize();
                    return;
                }
                else {
                    yVelocity = yVelocity * -1;
                }
            }
            c.drawBitmap(ball.getBitmap(), ballX, ballY, null);
            h.postDelayed(r, FRAME_RATE);
        }

    }

    private void checkIfBrickIntersects(int ballX, int ballY) {

    }

    private void drawBricks(Canvas c) {
        for (int j = 0; j < bricks.length; j++) {
            for (int i = 0; i < bricks[j].length; i++) {
                if(bricks[j][i] > 0) {
                    BitmapDrawable brick = (BitmapDrawable) ContextCompat.getDrawable(mContext, R.drawable.brick64);
                    c.drawBitmap(brick.getBitmap(), i * brick.getBitmap().getWidth(), 100 + (j * brick.getBitmap().getHeight()), null);
                }
            }
        }
    }
}
