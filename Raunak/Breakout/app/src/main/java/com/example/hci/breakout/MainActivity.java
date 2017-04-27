package com.example.hci.breakout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainActivity extends Activity {

    BreakoutView breakoutView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        breakoutView = new BreakoutView(this);
        setContentView(breakoutView);
    }

    class BreakoutView extends SurfaceView implements Runnable {

        Thread gameThread = null;
        SurfaceHolder holder = null;
        Paint paint = new Paint();
        volatile boolean isPlaying;
        boolean paused = true;
        Canvas canvas;

        Paddle paddle;
        Ball ball;

        int screenX;
        int screenY;

        private long currentFrameTime;
        long fps;

        Brick[] bricks = new Brick[200];
        int brickCount=0;
        Life[] lifeSymbols = new Life[3];
        int score;
        int lives;

        public BreakoutView(Context context)
        {
            super(context);

            holder = getHolder();
            paint = new Paint();

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            screenX = size.x;
            screenY = size.y;
            paddle = new Paddle(screenX,screenY);

            ball = new Ball(screenX, 1000);
            isPlaying = true;

            score = 0;
            lives = 3;

            int left = 0;
            for (int i=0;i<lives;i++)
            {
                lifeSymbols[i] = new Life(left, 1010, left + 10, 1020);
                left = left + 20;
            }

            createBricks();
        }

        public void createBricks()
        {
            ball.reset(screenX, 1000);
            int brickWidth = screenX/4;
            int brickHeight = 1000/10;

            brickCount = 0;

            for (int col=0;col<4;col++)
            {
                for (int row = 0;row<4;row++)
                {
                    bricks[brickCount] = new Brick(row+1, col, brickWidth, brickHeight);
                    brickCount++;
                }
            }
        }

        @Override
        public void run() {
            while(isPlaying) {
                long startTime = System.currentTimeMillis();
                if (!paused) {
                    update();
                }
                currentFrameTime = System.currentTimeMillis() - startTime;
                if (currentFrameTime>=1)
                    fps = 1000 / currentFrameTime;

                draw();
            }
        }

        public void draw()
        {
            if (holder.getSurface().isValid())
            {
                canvas = holder.lockCanvas();
                canvas.drawColor(Color.argb(255, 26, 128,182));
                paint.setColor(Color.argb(255,255,255,255));

                paint.setTextSize(40);
                canvas.drawText(Integer.toString(score), screenX - 100, 50, paint);
                //System.out.println(paddle.getRect());
                canvas.drawRect(paddle.getRect(), paint);
                canvas.drawRect(ball.getRect(), paint);

                paint.setColor(Color.argb(255,249,129,0));
                for(int i=0;i<brickCount;i++)
                {
                    if(bricks[i].getVisibility() == true)
                    {
                        canvas.drawRect(bricks[i].getRect(), paint);
                    }
                }

                if (score == brickCount * 10)
                {
                    paint.setTextSize(90);
                    canvas.drawText("You have won", 10, screenY/2, paint);
                }

                if (lives <= 0)
                {
                    paint.setTextSize(90);
                    canvas.drawText("You have lost", 10, screenY/2, paint);
                } else {
                    paint.setColor(Color.argb(255,255,255,255));
                    for (int i=0;i<lives;i++)
                    {
                        if (lifeSymbols[i].getVisibility())
                        {
                            canvas.drawRect(lifeSymbols[i].getRect(), paint);
                        }
                    }
                }
                holder.unlockCanvasAndPost(canvas);
            }
        }

        public void pause()
        {
            isPlaying = false;
            try {
                gameThread.join();
            } catch (InterruptedException e)
            {
                //Log.e("Error joing")
            }

        }

        public void resume()
        {
            isPlaying = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        public void update()
        {
            paddle.update(fps);
            ball.update(fps);
            for (int i=0;i<brickCount;i++)
            {
                if (bricks[i].getVisibility())
                {
                    if (RectF.intersects(bricks[i].getRect(), ball.getRect()))
                    {
                        bricks[i].setInvisible();
                        ball.reverseYVelocity();
                        score = score + 10;
                    }
                }
            }
            if (RectF.intersects(paddle.getRect(), ball.getRect()))
            {
                ball.setRandomXVelocity();
                ball.reverseYVelocity();
                ball.clearObstacleY(paddle.getRect().top -2);
            }

            if (ball.getRect().left < 0)
            {
                ball.reverseXVelocity();
                ball.clearObstacleX(2);
            }

            if (ball.getRect().right > screenX -10)
            {
                ball.reverseXVelocity();
                ball.clearObstacleX(screenX - 22);
            }

            if (ball.getRect().bottom > 1000)
            {
                ball.reverseYVelocity();
                //ball.clearObstacleY(screenY - 2);
                System.out.println("Lives is "+ lives);
                lives--;
                lifeSymbols[lives].setInvisible();
                if (lives == 0)
                {
                    paused = true;
                    createBricks();
                }
            }

            if (ball.getRect().top < (1000/10))
            {
                ball.reverseYVelocity();
                ball.clearObstacleY(12);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent)
        {
            switch(motionEvent.getAction() & MotionEvent.ACTION_MASK)
            {
                case MotionEvent.ACTION_DOWN:
                    paused = false;
                    if (motionEvent.getX() > screenX/2)
                    {
                        paddle.setMovementState(paddle.RIGHT);
                    } else {
                        paddle.setMovementState(paddle.LEFT);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    paddle.setMovementState(paddle.STOPPED);
                    break;
            }
            return true;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        breakoutView.resume();
    }
}