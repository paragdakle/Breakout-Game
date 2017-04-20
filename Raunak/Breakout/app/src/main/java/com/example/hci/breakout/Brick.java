package com.example.hci.breakout;

import android.graphics.RectF;

/**
 * Created by rauna on 4/14/2017.
 */

public class Brick {
    private RectF rect;
    private boolean isVisible;
    private int padding = 1;
    public Brick(int row, int column, int width, int height)
    {
        isVisible = true;
        rect = new RectF(column*width+padding, row*height + padding, column*width + width - padding, row*height+height-padding);
    }

    public RectF getRect()
    {
        return rect;
    }

    public boolean getVisibility()
    {
        return isVisible;
    }

    public void setInvisible()
    {
        isVisible = false;
    }
}
