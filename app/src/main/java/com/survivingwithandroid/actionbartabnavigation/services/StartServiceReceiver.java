package com.survivingwithandroid.actionbartabnavigation.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by lipov91 on 2015-07-22.
 */
public class StartServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service = new Intent(context, GardeningToolsService.class);
        context.startService(service);
    }
}
