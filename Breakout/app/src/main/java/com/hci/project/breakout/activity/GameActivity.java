package com.hci.project.breakout.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hci.project.breakout.R;
import com.hci.project.breakout.custom.GameView;

import java.util.HashMap;

public class GameActivity extends Activity implements View.OnTouchListener, View.OnClickListener, SensorEventListener {

    View paddleContainer;
    View paddle;
    ImageView imgPause, imgLife1, imgLife2, imgLife3;
    TextView txtScore;
    GameView gameView;
    ConstraintLayout container;
    RelativeLayout parentLayout;
    PopupWindow popWindow;

    boolean isPause = false;
    boolean hasGameStarted = false;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    private int currentLifes = 3;
    private int score = 0;
    private long lastUpdateTimestamp = 0;
    private float recent_x = 0, recent_y = 0;
    private int paddleWidth = 300;
    public int[] paddleXRange = {0, paddleWidth};

    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundPoolMap;

    public final int GAME_START = 1;
    public final int GAME_OVER = 2;
    public final int GAME_PAUSE = 3;
    public final int GAME_RESUME = 4;
    public final int BRICK_BROKEN = 5;
    public final int LIFE_GONE = 6;
    public final int VICTORY = 7;

    public static boolean isMute = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        paddleContainer = findViewById(R.id.paddleContainer);
        paddle = findViewById(R.id.paddle);
        imgPause = (ImageView) findViewById(R.id.imgPause);
        imgLife1 = (ImageView) findViewById(R.id.imgLife1);
        imgLife2 = (ImageView) findViewById(R.id.imgLife2);
        imgLife3 = (ImageView) findViewById(R.id.imgLife3);
        gameView = (GameView) findViewById(R.id.gameView);
        txtScore = (TextView) findViewById(R.id.textViewScore);
        container = (ConstraintLayout) findViewById(R.id.container);
        parentLayout = (RelativeLayout) findViewById(R.id.parentLayout);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) paddle.getLayoutParams();
        lParams.leftMargin = (paddleContainer.getWidth() / 2) - (paddle.getWidth() / 2);
        paddle.setLayoutParams(lParams);

        gameView.setPaddle(this, paddle);

        soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap(7);
        soundPoolMap.put(GAME_START, soundPool.load(this, R.raw.game_start, 1));
        soundPoolMap.put(GAME_OVER, soundPool.load(this, R.raw.game_over, 1));
        soundPoolMap.put(GAME_PAUSE, soundPool.load(this, R.raw.game_pause, 1));
        soundPoolMap.put(GAME_RESUME, soundPool.load(this, R.raw.game_pause, 1));
        soundPoolMap.put(BRICK_BROKEN, soundPool.load(this, R.raw.brick_break, 1));
        soundPoolMap.put(LIFE_GONE, soundPool.load(this, R.raw.life_gone, 1));
        soundPoolMap.put(VICTORY, soundPool.load(this, R.raw.victory, 1));

        initializeGame();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && !hasGameStarted) {
            showPopup(new LinearLayout(this), getResources().getString(R.string.start_game_text), R.mipmap.ic_start);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(hasGameStarted) {
            paddleContainer.setOnTouchListener(this);
            paddle.setOnTouchListener(this);
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
            gameView.resumeGame();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        paddleContainer.setOnTouchListener(null);
        paddle.setOnTouchListener(null);
        sensorManager.unregisterListener(this);
        gameView.pauseGame();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(soundPool != null) {
            soundPool.release();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (view.getId()) {
            case R.id.paddleContainer:
                final int x = (int) event.getRawX();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        updatePaddlePosition(x);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updatePaddlePosition(x);
                        break;
                }
                paddleContainer.invalidate();
                return true;

            case R.id.paddle:
                return false;
        }
        return true;
    }

    private void updatePaddlePosition(int x) {
        RelativeLayout.LayoutParams lParams;
        lParams = (RelativeLayout.LayoutParams) paddle.getLayoutParams();
        lParams.leftMargin = x > paddle.getWidth() ? x - paddle.getWidth() : 0;
        if ((lParams.leftMargin + paddle.getWidth()) > paddleContainer.getWidth())
            lParams.leftMargin = paddleContainer.getWidth() - paddle.getWidth();
        paddleXRange[0] = lParams.leftMargin;
        paddleXRange[1] = paddleXRange[0] + paddleWidth;
        paddle.setLayoutParams(lParams);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgPause:
                if(isPause) {
                    resumeGame();
                    gameView.resumeGame();
                    playMusic(GAME_RESUME);
                }
                else {
                    pauseGame();
                    gameView.pauseGame();
                    playMusic(GAME_PAUSE);
                }
                isPause = !isPause;
                break;
        }
    }

    private void resumeGame() {
        imgPause.setImageResource(R.mipmap.ic_pause);
        paddleContainer.setOnTouchListener(this);
        paddle.setOnTouchListener(this);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        imgPause.setOnClickListener(this);
    }

    private void pauseGame() {
        paddleContainer.setOnTouchListener(null);
        paddle.setOnTouchListener(null);
        sensorManager.unregisterListener(this);
        showPopup(new LinearLayout(this), getResources().getString(R.string.resume_game_text), R.mipmap.ic_start);
        imgPause.setOnClickListener(null);
    }

    public void decreaseLife() {
        currentLifes--;
        playMusic(LIFE_GONE);
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
        this.increaseScore(-200);
        if(currentLifes == 0) endGame();
        else
            showPopup(new LinearLayout(this), getResources().getString(R.string.start_game_text), R.mipmap.ic_start);
    }

    private void initializeGame() {
        if(score != 0) {
            if(checkIfHighScore(score)) {

            }
            else {
                showPopup(new LinearLayout(this), String.format(getResources().getString(R.string.score_text_message), score), R.mipmap.ic_restart);
            }
        }
        currentLifes = 3;
        imgLife1.setImageResource(R.mipmap.ic_life);
        imgLife2.setImageResource(R.mipmap.ic_life);
        imgLife3.setImageResource(R.mipmap.ic_life);
        gameView.initialize(true);
        increaseScore(0);
        playMusic(GAME_START);
    }

    public void increaseScore(int delta) {
        score += delta;
        txtScore.setText(String.valueOf(score));
    }

    public boolean isOnPaddle(int x) {
        return (x > paddleXRange[0] && paddleXRange[1] > x);
    }

    public int getPaddleMean() {
        return (paddleXRange[0] + paddleXRange[1]) / 2;
    }

    public void endGame() {
        hasGameStarted = false;
        initializeGame();
    }

    private boolean checkIfHighScore(int score) {
        return false;
    }

    public void playMusic(int type) {
        if(isMute) return;
        switch (type) {
            case GAME_START:
                soundPool.play(soundPoolMap.get(type), (float) 0.8, (float) 0.8, 1, 0, 1f);
                break;

            case LIFE_GONE:
                soundPool.play(soundPoolMap.get(type), 1, 1, 1, 0, 1f);
                ((Vibrator)getSystemService(VIBRATOR_SERVICE)).vibrate(300);
                break;

            default:
                soundPool.play(soundPoolMap.get(type), 1, 1, 1, 0, 1f);
                break;
        }
    }

    private void startGame() {
        hasGameStarted = true;
        resumeGame();
        gameView.startGame();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float new_x = event.values[0];
            float new_y = event.values[1];

            long currentTimestamp = System.currentTimeMillis();

            if((currentTimestamp - lastUpdateTimestamp) > 100) {
                lastUpdateTimestamp = currentTimestamp;

                if(Math.abs(recent_x - new_x) > 2) {
                    gameView.xVelocity += (gameView.xVelocity / Math.abs(gameView.xVelocity));
                }
                if(Math.abs(recent_y - new_y) > 3) {
                    gameView.yVelocity += (gameView.yVelocity / Math.abs(gameView.yVelocity));
                }

                recent_x = new_x;
                recent_y = new_y;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void showPopup(View v, String message, final int buttonId) {

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View inflatedView = layoutInflater.inflate(R.layout.start_game_popup, parentLayout, false);

        TextView popupTextView = (TextView) inflatedView.findViewById(R.id.txtViewContent);
        popupTextView.setText(message);


        ImageView actionButton = (ImageView) inflatedView.findViewById(R.id.btnGo);
        actionButton.setImageResource(buttonId);

        Display display = getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        // set height depends on the device size
        popWindow = new PopupWindow(inflatedView, (int)(size.x * 0.6), (int) (size.y * 0.4), true);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
                if(buttonId == R.mipmap.ic_start && !hasGameStarted) {
                    startGame();
                }
                else if(buttonId == R.mipmap.ic_start) {
                    resumeGame();
                    gameView.startGame();
                }
                else {
                    score = 0;
                    initializeGame();
                }
            }
        });

//        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                if(buttonId == R.mipmap.ic_start && !hasGameStarted) {
//                    hasGameStarted = true;
//                }
//            }
//        });

        popWindow.setAnimationStyle(R.style.popupAnimation);
        popWindow.setFocusable(true);
        popWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.popup_background_drawable));
        popWindow.setOutsideTouchable(false);
        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        popWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }
}
