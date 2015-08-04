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
public class Updater2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updater);

        final TextView tvUpdater = (TextView) findViewById(R.id.tvUpdater);

        new Thread(new Runnable() {

            public void run() {

                int i = 0;

                while (i < 100) {

                    SystemClock.sleep(250);
                    i++;

                    final int curCount = i;

                    if (curCount % 5 == 0) {

                        tvUpdater.post(new Runnable() {

                            @Override
                            public void run() {

                                tvUpdater.setText("Pobieranie aktualizacji ( " +
                                        curCount + "% )...");
                            }
                        });
                    }
                }

                tvUpdater.post(new Runnable() {

                    @Override
                    public void run() {

                        tvUpdater.setText("Zakoñczono pobieranie wszystkich aktualizacji.");
                    }
                });
            }
        }).start();
    }
}
