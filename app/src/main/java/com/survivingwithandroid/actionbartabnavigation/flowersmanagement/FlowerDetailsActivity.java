package com.survivingwithandroid.actionbartabnavigation.flowersmanagement;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;
import android.widget.Toast;

import com.survivingwithandroid.actionbartabnavigation.R;

/**
 * Created by Jan Lipka on 2015-08-02.
 */
public class FlowerDetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flower_details);

        try {

            Intent launchIntent = getIntent();
            Uri launchData = launchIntent.getData();
            String id = launchData.getLastPathSegment();
            Uri dataDetails =
                    Uri.withAppendedPath(FlowersContentProvider.CONTENT_URI,
                            id);
            Cursor cursor = managedQuery(dataDetails, null, null, null, null);
            cursor.moveToFirst();
            String name =
                    cursor.getString(cursor
                            .getColumnIndex(FlowersContentProvider.FLOWERS_TITLE));
            String description =
                    cursor.getString(cursor.getColumnIndex(FlowersContentProvider.FLOWERS_BODY));
            TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
            TextView tvBody = (TextView) findViewById(R.id.tvBody);

            tvTitle.setText(name);
            tvBody.setLinksClickable(true);
            tvBody.setAutoLinkMask(Linkify.ALL);
            tvBody.setText(description);

        } catch (Exception e) {

            Toast.makeText(this, "B³¹d.", Toast.LENGTH_LONG).show();
        }
    }
}
