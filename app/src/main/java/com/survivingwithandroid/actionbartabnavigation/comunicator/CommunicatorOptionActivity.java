package com.survivingwithandroid.actionbartabnavigation.comunicator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.survivingwithandroid.actionbartabnavigation.R;

/**
 * Created by Jan Lipka on 2015-07-31.
 */
public class CommunicatorOptionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communicator_option);
    }

    public void onClick(View view) {

        Intent intent = null;

        if (view.getId() == R.id.btnForm) {

            intent = new Intent(CommunicatorOptionActivity.this, ComunicatorActivity.class);

        } else if (view.getId() == R.id.btnBluetooth) {

            intent = new Intent(CommunicatorOptionActivity.this, BluetoothActivity.class);
        }

        startActivity(intent);
    }
}
