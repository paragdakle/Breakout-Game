/*=============================================================================
 |   Assignment:  CS6326 Project
 |       Author:  Parag Dakle, Raunak Sabhani
 |     Language:  Android
 |    File Name:  GameView.java
 |
 +-----------------------------------------------------------------------------
 |
 |  Description:  A breakout game
 |
 |  File Purpose: Class which contains the view of the game
 *===========================================================================*/
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

public class GameView extends View {

    private Context mContext;
    int ballX = -1;
    int ballY = -1;
    boolean isRestart = true;
    boolean isNewLife = true;
    boolean isPause = false;
    private int xVelocity = 30;
    private int yVelocity = 15;
    private int ticker = 0;
    private int deltaY = 0;
    private Handler h;
    private final int FRAME_RATE = 30;
    View paddle;
    GameActivity activity;
    int[][] bricks;
    BitmapDrawable ball, brick1, brick2;

    /*Constructor for gameview class
    * Author: Parag Dakle*/
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        h = new Handler();
        activity = null;
        ball = (BitmapDrawable) ContextCompat.getDrawable(mContext, R.drawable.volleyball);
        brick1 = (BitmapDrawable) ContextCompat.getDrawable(mContext, R.drawable.brick);
        brick2 = (BitmapDrawable) ContextCompat.getDrawable(mContext, R.drawable.brick64);
    }

    private Runnable r = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    /*Initialize paddle
      Author: Raunak Sabhani
     */
    public void setPaddle(GameActivity activity, View paddle) {
        this.paddle = paddle;
        this.activity = activity;
    }

    /*Start the game
      Author: Raunak Sabhani
     */
    public void startGame() {
        h.postDelayed(r, FRAME_RATE);
    }

    /*Initialize variables
    Author: Parag Dakle
     */
    public void initialize(boolean fullRestart) {
        isNewLife = true;
        isRestart = fullRestart;
        invalidate();
    }

    /*Pause the game
    * Author: Raunak Sabhani*/
    public void pauseGame() {
        isPause = true;
    }

    /*Resume the game
    * Author: Raunak Sabhani*/
    public void resumeGame() {
        isPause = false;
        h.postDelayed(r, FRAME_RATE);
    }

    /*Draw all the objects on the view
    * Author: Parag Dakle*/
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
                    bricks[i][j] = 2;
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
                activity.playMusic(activity.BRICK_BROKEN);
            }
            else {
                if ((ballX > this.getWidth() - ball.getBitmap().getWidth()) || (ballX < 0)) {
                    xVelocity = xVelocity * -1;
                }
                if (ballY < 0) {
                    yVelocity = yVelocity * -1;
                }
                if ((ballY > this.getHeight() - ball.getBitmap().getHeight())) {
                    if (!activity.isOnPaddle(ballX - xVelocity) || !activity.isOnPaddle(ballX)) {
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

    /*Check if the ball intersects with any of the bricks
    * Author: Parag Dakle*/
    private boolean checkIfBrickIntersects(int ballX, int ballY) {
        boolean hasIntersect = false;
        RectF ballRect, brickRect;
        ballRect = new RectF(ballX, ballY, ballX + ball.getBitmap().getWidth(), ballY + ball.getBitmap().getHeight());
        for(int i = 0; i < bricks.length; i++) {
            for(int j = 0; j < bricks[i].length; j++) {
                if(bricks[i][j] == 2) {
                    brickRect = new RectF(j * brick2.getBitmap().getWidth(), (i * brick2.getBitmap().getHeight()) + deltaY, (j + 1) * brick2.getBitmap().getWidth(), ((i + 1) * brick1.getBitmap().getHeight()) + deltaY);
                    if(RectF.intersects(ballRect, brickRect)) {
                        bricks[i][j] = 1;
                        activity.increaseScore(50);
                        hasIntersect = true;
                    }
                }
                else if(bricks[i][j] == 1) {
                    brickRect = new RectF(j * brick1.getBitmap().getWidth(), (i * brick1.getBitmap().getHeight()) + deltaY, (j + 1) * brick1.getBitmap().getWidth(), ((i + 1) * brick1.getBitmap().getHeight()) + deltaY);
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

    /*Draw the bricks on the screen
    * Author: Raunak Sabhani*/
    private boolean drawBricks(Canvas c) {
        boolean brickDrawn = false;
        for (int j = 0; j < bricks.length; j++) {
            for (int i = 0; i < bricks[j].length; i++) {
                if(bricks[j][i] == 2) {
                    c.drawBitmap(brick2.getBitmap(), i * brick2.getBitmap().getWidth(), (j * brick2.getBitmap().getHeight()) + deltaY, null);
                    brickDrawn = true;
                }
                else if(bricks[j][i] == 1) {
                    c.drawBitmap(brick1.getBitmap(), i * brick1.getBitmap().getWidth(), (j * brick1.getBitmap().getHeight()) + deltaY, null);
                    brickDrawn = true;
                }
            }
        }
        return brickDrawn;
    }
}
