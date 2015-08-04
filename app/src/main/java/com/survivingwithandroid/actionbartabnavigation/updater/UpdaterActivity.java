package com.survivingwithandroid.actionbartabnavigation.updater;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;

import com.survivingwithandroid.actionbartabnavigation.R;

/**
 * Created by lipov91 on 2015-07-08.
 */
public class UpdaterActivity extends Activity {

    TextView tvUpdater;
    UpdaterTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updater);

        tvUpdater = (TextView) findViewById(R.id.tvUpdater);
        task = new UpdaterTask();
        task.execute();
    }


    private class UpdaterTask extends AsyncTask<Void, Integer, Integer> {

        UpdaterTask() {}

        @Override
        protected Integer doInBackground(Void... unused) {

            int i = 0;

            while (i < 100) {

                SystemClock.sleep(250);
                i++;
                
                if (i % 5 == 0) {
                    
                    publishProgress(i);
                }
            }
            return i;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

            tvUpdater.setText("Pobieranie aktualizacji ( " + progress[0] + "% )...");
        }

        @Override
        protected void onPostExecute(Integer result) {

            tvUpdater.setText("Zakoñczono aktualizacjê. Pobrano " +
                    result.toString() + " aktualizacji.");
        }
    }
}
