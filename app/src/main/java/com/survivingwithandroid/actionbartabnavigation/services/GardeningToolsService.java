package com.survivingwithandroid.actionbartabnavigation.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;

/**
 * Serwis lokalny
 * <p/>
 * Created by lipov91 on 2015-07-22.
 */
public class GardeningToolsService extends Service {

    private final IBinder mBinder = new MyBinder();
    private ArrayList<String> toolsList = new ArrayList<String>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        toolsList.add("Pilarka elektryczna");
        toolsList.add("Kosiarka elektryczna");
        toolsList.add("Kosa spalinowa");

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    public List<String> getToolsList() {

        return toolsList;
    }


    public class MyBinder extends Binder {

        public GardeningToolsService getService() {

            return GardeningToolsService.this;
        }
    }
}
