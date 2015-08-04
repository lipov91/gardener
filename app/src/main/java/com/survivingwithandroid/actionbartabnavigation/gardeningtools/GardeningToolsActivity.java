package com.survivingwithandroid.actionbartabnavigation.gardeningtools;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.survivingwithandroid.actionbartabnavigation.R;
import com.survivingwithandroid.actionbartabnavigation.services.GardeningToolsService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lipov91 on 2015-07-22.
 */
public class GardeningToolsActivity extends ListActivity {

    private GardeningToolsService service;
    private List<String> tools;
    private ArrayAdapter<String> adapter;
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            GardeningToolsService.MyBinder binder = (GardeningToolsService.MyBinder) iBinder;
            service = binder.getService();
            Toast.makeText(GardeningToolsActivity.this,
                    "Nazwi¹zano po³¹czenie z serwisem wewnêtrznym.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            service = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gardening_tools);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        tools = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1);
        setListAdapter(adapter);
    }

    @Override
    protected void onResume() {

        super.onResume();
        Intent intent = new Intent(this, GardeningToolsService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {

        super.onPause();
        unbindService(serviceConnection);
    }

    public void onClick(View view) {

        if (service != null) {

            tools.clear();
            tools.addAll(service.getToolsList());
            adapter.notifyDataSetChanged();
        }
    }
}
