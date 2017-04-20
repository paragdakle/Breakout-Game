package com.example.hci.breakout;

import android.graphics.RectF;

/**
 * Created by rauna on 4/19/2017.
 */

public class Life {
    RectF rect;
    private boolean isVisible;

    public Life(int left, int top, int right, int bottom) {

        rect = new RectF(left, top, right, bottom);
        isVisible = true;
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
