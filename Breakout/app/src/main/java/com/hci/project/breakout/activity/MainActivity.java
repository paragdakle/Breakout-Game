package com.hci.project.breakout.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.hci.project.breakout.R;

/**
 * Created by root on 13/4/17.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnStartNewGame, btnShowLeaderBoard;
    ImageButton btnMute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartNewGame = (Button) findViewById(R.id.btnNewGame);
        btnShowLeaderBoard = (Button) findViewById(R.id.btnShowLeaderBoard);
        btnMute = (ImageButton) findViewById(R.id.imgBtnMute);

        btnStartNewGame.setOnClickListener(this);
        btnShowLeaderBoard.setOnClickListener(this);
        btnMute.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNewGame:
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
                break;

            case R.id.btnShowLeaderBoard:
                Intent i = new Intent(MainActivity.this, LeaderBoardActivity.class);
                startActivity(i);
                //Toast.makeText(MainActivity.this, "First play to see high scores!", Toast.LENGTH_LONG).show();
                break;

            case R.id.imgBtnMute:
                if(!GameActivity.isMute) {
                    btnMute.setImageResource(R.mipmap.ic_mute);
                }
                else {
                    btnMute.setImageResource(R.mipmap.ic_unmute);
                }
                GameActivity.isMute = !GameActivity.isMute;
                break;
        }
    }
}
