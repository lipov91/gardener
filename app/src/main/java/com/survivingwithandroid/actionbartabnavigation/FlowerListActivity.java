package com.survivingwithandroid.actionbartabnavigation;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.survivingwithandroid.actionbartabnavigation.model.Flower;
import com.survivingwithandroid.actionbartabnavigation.parsers.FlowerJSONParser;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lipov91 on 2015-07-07.
 */
public class FlowerListActivity extends ListActivity {

    ProgressBar pb;
    List<Flower> flowers;
    List<FlowerListTask> tasks;
    public static final String PHOTOS_BASE_URL = "http://services.hanselandpetal.com/photos/";
    Intent intent;
    ConnectivityManager cm;
    FlowerListTask flowerListTask;
    FlowerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flower_list);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<FlowerListTask>();

        if (isOnline()) {

            // requestData("http://services.hanselandpetal.com/secure/feeds/flowers.xml");
            //requestData("http://services.hanselandpetal.com/secure/feeds/flowers.json");
            requestData("http://services.hanselandpetal.com/feeds/flowers.json");

        } else {

            Toast.makeText(this, "Wystąpił problem z połączeniem do sieci.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_mainpage) {

            intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            return true;

        } else {

            return false;
        }
    }

    protected boolean isOnline() {

        cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {

            return true;

        } else {

            return false;
        }
    }

    private void requestData(String uri) {

        flowerListTask = new FlowerListTask();
//        flowerListTask.execute("ogrodnik: Krzysztof Lipka",
//                "data: 2015-07-06 18:00",
//                "zadanie: Uzupełnienie listy roślin");
        // Progressbar zniknie w momencie zakończenia gł. wątku (bez znaczenia, czy
        // pozostałe wątki się zakończą)
        flowerListTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, uri);
    }

    protected void display(List<Flower> flowers) {

        adapter = new FlowerAdapter(this, R.layout.item_flower, flowers);
        setListAdapter(adapter);
    }


    private class FlowerListTask extends AsyncTask<String, String, List<Flower>> {

        String data;

        @Override
        protected void onPreExecute() {

            //sendMessage("Przygotowanie do pracy");
            if (tasks.size() == 0) {

                pb.setVisibility(View.VISIBLE);
            }

            tasks.add(this);
        }

        @Override
        protected List<Flower> doInBackground(String... params) {

            for (int i = 0; i < params.length; i++) {

                // data = HttpManager.getData(params[0]);
                //data = HttpUrlConnectionManager.getData(params[0], "feeduser", "feedpassword");
                data = HttpUrlConnectionManager.getData(params[0]);
                flowers = FlowerJSONParser.parseFeed(data);
            }

            return flowers;
        }

        @Override
        protected void onPostExecute(List<Flower> flowers) {

            tasks.remove(this);
            if (tasks.size() == 0) {

                pb.setVisibility(View.INVISIBLE);
            }

            if (flowers == null) {

                Toast.makeText(FlowerListActivity.this,
                        "Wystąpił błąd podczas próby połączenia z serwisem",
                        Toast.LENGTH_LONG).show();
            }

            display(flowers);
        }
    }
}
