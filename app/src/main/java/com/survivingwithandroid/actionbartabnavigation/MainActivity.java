package com.survivingwithandroid.actionbartabnavigation;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.survivingwithandroid.actionbartabnavigation.comunicator.CommunicatorOptionActivity;
import com.survivingwithandroid.actionbartabnavigation.comunicator.ComunicatorActivity;
import com.survivingwithandroid.actionbartabnavigation.flowersmanagement.FlowersManagementActivity;
import com.survivingwithandroid.actionbartabnavigation.flowersmanagement.MoviesActivity;
import com.survivingwithandroid.actionbartabnavigation.flowersmanagement.SearchActivity;
import com.survivingwithandroid.actionbartabnavigation.flowersmanagement.SlideshowActivity;
import com.survivingwithandroid.actionbartabnavigation.gardeningtools.GardeningToolsActivity;
import com.survivingwithandroid.actionbartabnavigation.news.NewsActivity;
import com.survivingwithandroid.actionbartabnavigation.tasksscheduling.DeviceSettingsActivity;

public class MainActivity extends Activity {

    // Notifications
    public static final int NOTIFY_UPDATE_1 = 0x1001;
    // Broadcasts
    public static String ACTION_UPDATES =
            "com.survivingwithandroid.actionbartabnavigation.ACTION_UPDATES";
    private NotificationManager notify = null;
    UpdatesBroadcastReceiver broadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notify = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final Notification notification = new Notification(R.drawable.ic_flower,
                "Aktualizacje", System.currentTimeMillis());
        notification.icon = R.drawable.ic_flower;
        notification.tickerText = "Dostêpne s¹ nowe aktualizacje.";
        notification.when = System.currentTimeMillis();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        Intent intentToLaunch = new Intent(MainActivity.this, MainActivity.class);
        PendingIntent intentBack =
                PendingIntent.getActivity(MainActivity.this, 0, intentToLaunch, 0);
        notification.setLatestEventInfo(this, "Aktualizacje", "Dostêpne s¹ nowe aktualizacje.",
                intentBack);
        notify.notify(NOTIFY_UPDATE_1, notification);

        broadcastReceiver = new UpdatesBroadcastReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
//        IntentFilter intentFilter = new IntentFilter(ACTION_UPDATES);
//        registerReceiver(broadcastReceiver, intentFilter);
    }

    public void onMenuOptionClick(MenuItem item) {

        if (item.getItemId() == R.id.action_base_flowers) {

            Intent intent = new Intent(this, FlowerListActivity.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.action_flowers_management) {

            Intent intent = new Intent(this, FlowersManagementActivity.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.action_search_flowers) {

            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.action_slideshow) {

            Intent intent = new Intent(this, SlideshowActivity.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.action_movies) {

            Intent intent = new Intent(this, MoviesActivity.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.action_updater) {

            Toast.makeText(this, "Pobieranie aktualizacji...", Toast.LENGTH_LONG).show();
//            Intent intent = new Intent(this, Updater2Activity.class);
//            startActivity(intent);

            Intent intent = new Intent(ACTION_UPDATES);
            sendBroadcast(intent);

        } else if (item.getItemId() == R.id.action_news) {

            Intent intent = new Intent(this, NewsActivity.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.action_gardening_tools) {

            Intent intent = new Intent(this, GardeningToolsActivity.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.action_communicator) {

            Intent intent = new Intent(this, CommunicatorOptionActivity.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.action_job_shedule) {

            Intent intent = new Intent(this, DeviceSettingsActivity.class);
            startActivity(intent);
        }
    }


    public static class UpdatesBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(context, "Pobrano wszystkie aktualizacje.", Toast.LENGTH_LONG).show();
        }
    }
}
