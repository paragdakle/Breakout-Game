package com.example.hci.breakout;

import android.graphics.RectF;

/**
 * Created by rauna on 4/14/2017.
 */

public class Paddle {
    private RectF rect;
    private float length;
    private float height;

    private float x;
    private float y;

    private float paddleSpeed;

    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;

    private int paddleMoving = STOPPED;

    public Paddle(int screenX, int screenY)
    {
        length = 130;
        height = 20;

        x = screenX/2;
        //y = screenY - 20;
        y = 1000;
        rect = new RectF(x,y,x+length,y+height);

        paddleSpeed = 1000;
    }

    public void setMovementState(int state)
    {
        paddleMoving = state;
    }

    public RectF getRect()
    {
        return rect;
    }

    public void update(long fps)
    {
        if (paddleMoving == LEFT)
        {
            x =x - paddleSpeed/fps;
        }

        if (paddleMoving == RIGHT)
        {
            x = x + paddleSpeed/fps;
        }

        rect.left = x;
        rect.right = x+length;
    }
}
