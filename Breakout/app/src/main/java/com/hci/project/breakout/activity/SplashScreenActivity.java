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
            ScorersManager manager;
            List<Scorer> listScorer = new ArrayList<Scorer>();
            int score = 10;
            for (int i=0;i<5;i++)
            {
                Scorer newScorer = new Scorer("Raunak", score);
                score = score + 10;
                listScorer.add(newScorer);
            }
            System.out.println(listScorer.size());
            manager = new ScorersManager(mContext);
            manager.addScorers(listScorer);
            return manager.getScorers();
        }

        /*Notify on success
          Author: Parag Dakle
         */
        @Override
        protected void onPostExecute(List<Scorer> object) {
            super.onPostExecute(object);

            SharedPreferences pref = getApplicationContext().getSharedPreferences("ScorerPref", 0);
            SharedPreferences.Editor editor = pref.edit();
            String keyname = "Scorer";
            for (int i=0;i<object.size();i++)
            {
                editor.putString(keyname+i, object.get(i).toSharedPreferenceString());
            }
            editor.commit();
            Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

}
