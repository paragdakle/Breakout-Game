package com.hci.project.breakout.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.hci.project.breakout.R;
import com.hci.project.breakout.model.Scorer;

import java.util.ArrayList;
import java.util.List;

public class SplashScreenActivity extends AppCompatActivity {

    public static List<Scorer> listScorer = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        GetScorers getScorers = new GetScorers(this);
        getScorers.execute();
        /*new Handler().postDelayed(new Runnable() {

            // Using handler with postDelayed called runnable run method

            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, 3 * 1000);*/
    }

    private class GetScorers extends AsyncTask<Void, Void, List<Scorer>>
    {
        private Context mContext;

        public GetScorers(Context context)
        {
            this.mContext = context;
        }
        @Override
        protected List<Scorer> doInBackground(Void... params) {
            List<Scorer> listScorer = new ArrayList<Scorer>();

            SharedPreferences pref = getApplicationContext().getSharedPreferences("ScorerPref", 0);
            for (int i=0;i<5;i++)
            {
                String scorerString = pref.getString("Scorer"+i, null);
                if (scorerString != null)
                {
                    listScorer.add(Scorer.stringToScorer(scorerString));
                }
            }
            SplashScreenActivity.listScorer = listScorer;
            return listScorer;
        }

        /*Notify on success
          Author: Parag Dakle
         */
        @Override
        protected void onPostExecute(List<Scorer> object) {
            super.onPostExecute(object);

            Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

}
