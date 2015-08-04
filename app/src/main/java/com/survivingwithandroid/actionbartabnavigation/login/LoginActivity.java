package com.survivingwithandroid.actionbartabnavigation.login;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.survivingwithandroid.actionbartabnavigation.R;


public class LoginActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FrameLayout frame = (FrameLayout) findViewById(R.id.flLogin);
        LoginView image = new LoginView(this, R.mipmap.ic_login);
        frame.addView(image);
    }
}