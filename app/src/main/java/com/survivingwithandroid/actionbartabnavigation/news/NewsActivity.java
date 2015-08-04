package com.survivingwithandroid.actionbartabnavigation.news;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.survivingwithandroid.actionbartabnavigation.R;
import com.survivingwithandroid.actionbartabnavigation.services.DownloadService;

/**
 * Komunikacja z serwerem zewn�trznym.
 * Wywo�ywanie serwisu odpowiedzialnego za pobieranie nowinek.
 * Odbieranie rezultatu z dzia�ania serwisu.
 * <p/>
 * Created by lipov91 on 2015-07-22.
 */
public class NewsActivity extends Activity {

    private TextView tvDownloadStatus;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();
            String filePath;
            int resultCode;

            if (bundle != null) {

                filePath = bundle.getString(DownloadService.FILEPATH);
                resultCode = bundle.getInt(DownloadService.RESULT);

                if (resultCode == RESULT_OK) {

                    Toast.makeText(NewsActivity.this,
                            "Pobieranie nowinek z pliku " + filePath + " zako�czono powodzeniem.",
                            Toast.LENGTH_LONG).show();
                    tvDownloadStatus.setText("Pobieranie zako�czone");

                } else {

                    Toast.makeText(NewsActivity.this,
                            "Wyst�pi� b��d podczas pobierania nowinek.", Toast.LENGTH_LONG).show();
                    tvDownloadStatus.setText("Wyst�pi� b��d. Przepraszamy.");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        tvDownloadStatus = (TextView) findViewById(R.id.tvDownloadStatus);
    }

    // Zlecenie wykonania zadania w serwisie
    @Override
    protected void onResume() {

        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(DownloadService.NOTIFICATION));
    }

    @Override
    protected void onPause() {

        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }


    public void onClick(View view) {

        Intent intent = new Intent(this, DownloadService.class);

        // Przekazanie informacji do serwisu o tym, z kt�rego pliku maj� by� pobierane informacje
        // oraz w kt�rym miejscu ma by� zapisywany rezultat.
        intent.putExtra(DownloadService.FILENAME, "index.html");
        intent.putExtra(DownloadService._URL, "http://www.vogella.com/index.html");
        startService(intent);

        tvDownloadStatus.setText("Pobieranie...");
    }
}
