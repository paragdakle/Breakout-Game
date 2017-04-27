package com.hci.project.breakout.activity;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.hci.project.breakout.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderBoard extends AppCompatActivity {

    public List<Scorer> scorerList= new ArrayList<Scorer>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("ScorerPref", 0);
        for (int i=0;i<5;i++)
        {
            String scorerString = pref.getString("Scorer"+i, null);
            scorerList.add(Scorer.stringToScorer(scorerString));
        }
        Collections.sort(scorerList);
        loadTextViews();
    }

    public void loadTextViews()
    {
        TextView tv1;
        for (int i=0;i<scorerList.size();i++) {
            String scoreId = "score"+(i+1)+"Title";
            int res = getResources().getIdentifier(scoreId, "id", getPackageName());
            tv1 = (TextView)findViewById(res);
            tv1.setText((i+1)+". " + scorerList.get(i).getName());

            scoreId = "score"+(i+1)+"Score";
            System.out.println("Scoreid is: "+scoreId);
            res = getResources().getIdentifier(scoreId, "id", getPackageName());
            tv1 = (TextView)findViewById(res);
            tv1.setText(Integer.toString(scorerList.get(i).getScore()));
        }
    }
}