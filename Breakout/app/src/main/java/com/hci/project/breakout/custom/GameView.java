package com.hci.project.breakout.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
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
    boolean isNewLife = true;
    boolean isPause = false;
    private int xVelocity = 20;
    private int yVelocity = 10;
    private int ticker = 0;
    private int deltaY = 120;
    private Handler h;
    private final int FRAME_RATE = 30;
    View paddle;
    GameActivity activity;
    int[][] bricks;
    BitmapDrawable ball, brick;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        h = new Handler();
        activity = null;
        ball = (BitmapDrawable) ContextCompat.getDrawable(mContext, R.drawable.volleyball);
        brick = (BitmapDrawable) ContextCompat.getDrawable(mContext, R.drawable.brick);
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

    public void initialize(boolean fullRestart) {
        isNewLife = true;
        isRestart = fullRestart;
        invalidate();
    }

    public void pauseGame() {
        isPause = true;
    }

    public void resumeGame() {
        isPause = false;
        h.postDelayed(r, FRAME_RATE);
    }

    protected void onDraw(Canvas c) {
        if(isPause) {
            c.drawBitmap(ball.getBitmap(), ballX, ballY, null);
            drawBricks(c);
            return;
        }
        if(isRestart) {
            int rows = 4;
            int columns = this.getWidth() / ball.getBitmap().getWidth();
            bricks = new int[rows][columns];
            for(int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    bricks[i][j] = 1;
                }
            }
            isRestart = false;
        }
        if ((ballX < 0 && ballY < 0) || isNewLife) {
            if(this.activity != null) {
                ballX = activity.getPaddleMean();
            }
            else
                ballX = 50;
            ballY = this.getHeight() - ball.getBitmap().getHeight();
            c.drawBitmap(ball.getBitmap(), ballX, ballY, null);
            drawBricks(c);
            isNewLife = false;
        } else {
            if(ticker % 50 == 0)
                activity.increaseScore(-1);
            ballX += xVelocity;
            ballY += yVelocity;
            if(checkIfBrickIntersects(ballX, ballY)) {
                xVelocity = xVelocity * -1;
                yVelocity = yVelocity * -1;
            }
            else {
                if ((ballX > this.getWidth() - ball.getBitmap().getWidth()) || (ballX < 0)) {
                    xVelocity = xVelocity * -1;
                }
                if (ballY < 0) {
                    yVelocity = yVelocity * -1;
                }
                if ((ballY > this.getHeight() - ball.getBitmap().getHeight())) {
                    if (!activity.isOnPaddle(ballX - xVelocity)) {
                        xVelocity = 20;
                        yVelocity = 10;
                        activity.decreaseLife();
                        initialize(false);
                        return;
                    } else {
                        yVelocity = yVelocity * -1;
                    }
                }
            }
            c.drawBitmap(ball.getBitmap(), ballX, ballY, null);
            if(!drawBricks(c)) {
                activity.endGame();
            }
            h.postDelayed(r, FRAME_RATE);
        }
        ticker++;
    }

    private boolean checkIfBrickIntersects(int ballX, int ballY) {
        boolean hasIntersect = false;
        RectF ballRect, brickRect;
        ballRect = new RectF(ballX, ballY, ballX + ball.getBitmap().getWidth(), ballY + ball.getBitmap().getHeight());
        for(int i = 0; i < bricks.length; i++) {
            for(int j = 0; j < bricks[i].length; j++) {
                if(bricks[i][j] == 1) {
                    brickRect = new RectF(j * brick.getBitmap().getWidth(), (i * brick.getBitmap().getHeight()) + deltaY, (j + 1) * brick.getBitmap().getWidth(), ((i + 1) * brick.getBitmap().getHeight()) + deltaY);
                    if(RectF.intersects(ballRect, brickRect)) {
                        bricks[i][j] = 0;
                        activity.increaseScore(100);
                        hasIntersect = true;
                    }
                }
            }
        }
        return hasIntersect;
    }

    private boolean drawBricks(Canvas c) {
        boolean brickDrawn = false;
        for (int j = 0; j < bricks.length; j++) {
            for (int i = 0; i < bricks[j].length; i++) {
                if(bricks[j][i] == 1) {
                    c.drawBitmap(brick.getBitmap(), i * brick.getBitmap().getWidth(), (j * brick.getBitmap().getHeight()) + deltaY, null);
                    brickDrawn = true;
                }
            }
        }
        return brickDrawn;
    }
}
