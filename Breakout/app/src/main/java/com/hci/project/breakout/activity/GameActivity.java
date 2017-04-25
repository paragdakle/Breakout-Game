package com.hci.project.breakout.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hci.project.breakout.Ball;
import com.hci.project.breakout.Brick;
import com.hci.project.breakout.Life;
import com.hci.project.breakout.Paddle;
import com.hci.project.breakout.R;
import com.hci.project.breakout.custom.GameView;

public class GameActivity extends Activity implements View.OnTouchListener, View.OnClickListener, SensorEventListener {

    BreakoutView breakoutView;
    View paddleContainer;
    View paddle;
    ImageView imgPause, imgLife1, imgLife2, imgLife3;
    GameView gameView;
    ConstraintLayout container;

    boolean isPause = false;
    boolean hasGameStarted = false;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    private int currentLifes = 3;
    private long lastUpdateTimestamp = 0;
    private float recent_x, recent_y, recent_z;
    private static final int MIN_SHAKE = 600;
    private int paddleWidth = 300;
    public int[] paddleXRange = {0, paddleWidth};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        breakoutView = new BreakoutView(this);
        setContentView(R.layout.activity_game);

        paddleContainer = findViewById(R.id.paddleContainer);
        paddle = findViewById(R.id.paddle);
        imgPause = (ImageView) findViewById(R.id.imgPause);
        imgLife1 = (ImageView) findViewById(R.id.imgLife1);
        imgLife2 = (ImageView) findViewById(R.id.imgLife2);
        imgLife3 = (ImageView) findViewById(R.id.imgLife3);
        gameView = (GameView) findViewById(R.id.gameView);
        container = (ConstraintLayout) findViewById(R.id.container);

//        int[] out = new int[2];
//        Rect outRect = new Rect();
//        container.getLocationOnScreen(out);
//        container.getDrawingRect(outRect);
//        ConstraintLayout.LayoutParams gameViewLayoutParams = (ConstraintLayout.LayoutParams) gameView.getLayoutParams();
//        gameViewLayoutParams.height = container.getHeight() - paddleContainer.getHeight();
//        gameView.setLayoutParams(gameViewLayoutParams);

        imgPause.setOnClickListener(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) paddle.getLayoutParams();
        lParams.leftMargin = (paddleContainer.getWidth() / 2) - (paddle.getWidth() / 2);
        paddle.setLayoutParams(lParams);

        gameView.setPaddle(this, paddle);

        initializeGame();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        paddleContainer.setOnTouchListener(this);
        paddle.setOnTouchListener(this);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
//        breakoutView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        paddleContainer.setOnTouchListener(null);
        paddle.setOnTouchListener(null);
        sensorManager.unregisterListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (view.getId()) {
            case R.id.paddleContainer:
                if(!hasGameStarted) {
                    gameView.startGame();
                    hasGameStarted = true;
                    return true;
                }
                final int X = (int) event.getRawX();
                RelativeLayout.LayoutParams lParams;
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        lParams = (RelativeLayout.LayoutParams) paddle.getLayoutParams();
                        lParams.leftMargin = X > paddle.getWidth() ? X - paddle.getWidth() : 0;
                        paddleXRange[0] = lParams.leftMargin;
                        paddleXRange[1] = paddleXRange[0] + paddleWidth;
                        paddle.setLayoutParams(lParams);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        lParams = (RelativeLayout.LayoutParams) paddle.getLayoutParams();
                        lParams.leftMargin = X > paddle.getWidth() ? X - paddle.getWidth() : 0;
                        if ((lParams.leftMargin + paddle.getWidth()) > view.getWidth())
                            lParams.leftMargin = view.getWidth() - paddle.getWidth();
                        paddleXRange[0] = lParams.leftMargin;
                        paddleXRange[1] = paddleXRange[0] + paddleWidth;
                        paddle.setLayoutParams(lParams);
                        break;
                }
                paddleContainer.invalidate();
                return true;

            case R.id.paddle:
                return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgPause:
                if(isPause) {
                    imgPause.setImageResource(R.mipmap.ic_pause);
                    paddleContainer.setOnTouchListener(this);
                    paddle.setOnTouchListener(this);
                    sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
                }
                else {
                    imgPause.setImageResource(R.mipmap.ic_play);
                    paddleContainer.setOnTouchListener(null);
                    paddle.setOnTouchListener(null);
                    sensorManager.unregisterListener(this);
                }
                isPause = !isPause;
                break;
        }
    }

    public void decreaseLife() {
        currentLifes--;
        switch (currentLifes) {
            case 0:
                imgLife1.setImageResource(R.mipmap.ic_dead);
                break;
            case 1:
                imgLife2.setImageResource(R.mipmap.ic_dead);
                break;
            case 2:
                imgLife3.setImageResource(R.mipmap.ic_dead);
        }
        hasGameStarted = false;
        if(currentLifes == 0) endGame();
    }

    private void initializeGame() {
        currentLifes = 3;
        imgLife1.setImageResource(R.mipmap.ic_life);
        imgLife2.setImageResource(R.mipmap.ic_life);
        imgLife3.setImageResource(R.mipmap.ic_life);

        gameView.initialize();
    }

    public boolean isOnPaddle(int x) {
        return (x > paddleXRange[0] && paddleXRange[1] > x);
    }

    public int getPaddleMean() {
        return (paddleXRange[0] + paddleXRange[1]) / 2;
    }

    public void endGame() {}


    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float new_x = event.values[0];
            float new_y = event.values[1];
            float new_z = event.values[2];

            long currentTimestamp = System.currentTimeMillis();

            if((currentTimestamp - lastUpdateTimestamp) > 100) {
                long timeDifference = currentTimestamp - lastUpdateTimestamp;
                lastUpdateTimestamp = currentTimestamp;

                float movementSpeed = Math.abs(new_x + new_y + new_z - recent_x - recent_y - recent_z) / (timeDifference * 1000);

                if(movementSpeed > MIN_SHAKE) {
                    System.out.print(movementSpeed);
                    System.out.print(new_x);
                    System.out.print(new_y);
                    System.out.print(new_z);
                }

                recent_x = new_x;
                recent_y = new_y;
                recent_z = new_z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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


}
