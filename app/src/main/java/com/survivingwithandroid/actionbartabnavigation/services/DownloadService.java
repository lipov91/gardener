package com.survivingwithandroid.actionbartabnavigation.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * IntentService umo¿liwia pracê w tle
 *
 * Created by lipov91 on 2015-07-22.
 */
public class DownloadService extends IntentService {

    private int result = Activity.RESULT_CANCELED;
    public static final String _URL = "urlpath";
    public static final String FILENAME = "filename";
    public static final String FILEPATH = "filepath";
    public static final String RESULT = "result";
    public static final String NOTIFICATION =
            "com.survivingwithandroid.actionbartabnavigation.services";

    public DownloadService() {

        super("DownloadService");
    }

    // Wywo³ywana asynchronicznie przez Androida
    @Override
    protected void onHandleIntent(Intent intent) {

        String urlPath = intent.getStringExtra(_URL);
        String fileName = intent.getStringExtra(FILENAME);
        File output = new File(Environment.getExternalStorageDirectory(), fileName);
        InputStream inputStream = null;
        InputStreamReader inputStreamReader;
        FileOutputStream fileOutputStream = null;
        URL url;

        if (output.exists()) {

            output.delete();
        }

        try {

            url = new URL(urlPath);
            inputStream = url.openConnection().getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            fileOutputStream = new FileOutputStream(output.getPath());
            int next = -1;

            while ((next = inputStreamReader.read()) != -1) {

                fileOutputStream.write(next);
            }

            result = Activity.RESULT_OK;

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (inputStream != null) {

                try {

                    inputStream.close();

                } catch (IOException e) {

                    e.printStackTrace();
                }
            }

            if (fileOutputStream != null) {

                try {

                    fileOutputStream.close();

                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }

        publishResults(output.getAbsolutePath(), result);
    }

    private void publishResults(String absoluteFile, int result) {

        Intent intent = new Intent(NOTIFICATION);

        intent.putExtra(FILEPATH, absoluteFile);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }
}
