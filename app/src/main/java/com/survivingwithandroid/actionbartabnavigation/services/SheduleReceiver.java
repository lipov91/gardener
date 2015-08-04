package com.survivingwithandroid.actionbartabnavigation.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by lipov91 on 2015-07-22.
 */
public class SheduleReceiver extends BroadcastReceiver {

    // Restartowanie serwisu co 30 sekund
    private static final long REPEAT_TIME = 1000 * 30;

    @Override
    public void onReceive(Context context, Intent intent) {

        AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, StartServiceReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar calendar = Calendar.getInstance();

        // Rozpoczêcie po 30 sekundach
        calendar.add(Calendar.SECOND, 30);
        // Optymalizacja poboru energii
        service.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                REPEAT_TIME, pendingIntent);
    }
}
