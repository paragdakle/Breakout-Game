package com.hci.project.breakout.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hci.project.breakout.R;

/**
 * Created by root on 13/4/17.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnStartNewGame, btnShowLeaderBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartNewGame = (Button) findViewById(R.id.btnNewGame);
        btnShowLeaderBoard = (Button) findViewById(R.id.btnShowLeaderBoard);

        btnStartNewGame.setOnClickListener(this);
        btnShowLeaderBoard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNewGame:
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
                break;

            case R.id.btnShowLeaderBoard:
                Intent i = new Intent(MainActivity.this, LeaderBoard.class);
                startActivity(i);
                //Toast.makeText(MainActivity.this, "First play to see high scores!", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
