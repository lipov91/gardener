package com.survivingwithandroid.actionbartabnavigation.comunicator;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.survivingwithandroid.actionbartabnavigation.R;

/**
 * Created by Jan Lipka on 2015-07-29.
 */
public class ComunicatorActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communicator);

        final WebView webView = (WebView) findViewById(R.id.webView);
        WebChromeClient webChromeClient = new WebChromeClient();
        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(webChromeClient);
        webView.addJavascriptInterface(new JavaScriptExtensions(), "jse");
        webView.loadUrl("file:///android_asset/communicator.html");
    }

    @Override
    protected void onPause() {

        WebView webView = (WebView) findViewById(R.id.webView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            webView.onPause();

        } else {

            webView.pauseTimers();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();

        WebView webView = (WebView) findViewById(R.id.webView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            webView.onResume();

        } else {

            webView.resumeTimers();
        }
    }

    public void onClick(View view) {

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("javascript:getMessage('Test');");
    }


    class JavaScriptExtensions {

        public static final int TOAST_LONG = Toast.LENGTH_LONG;
        public static final int TOAST_SHORT = Toast.LENGTH_SHORT;

        @android.webkit.JavascriptInterface
        public void toast(String message, int length) {

            Toast.makeText(ComunicatorActivity.this, message, length).show();
        }

        @android.webkit.JavascriptInterface
        public void getAdminMessage(String message) {

            TextView tvResult = (TextView) findViewById(R.id.tvResult);
            tvResult.setText("Admin: " + message);
        }
    }
}
