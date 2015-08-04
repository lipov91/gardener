package com.survivingwithandroid.actionbartabnavigation.flowersmanagement;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.survivingwithandroid.actionbartabnavigation.database.FlowersDatabaseHelper;

/**
 * Created by lipov91 on 2015-07-23.
 */
public class FlowersDatabaseActivity extends Activity {

    protected FlowersDatabaseHelper dbHelper = null;
    protected Cursor cursor = null;
    protected SQLiteDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        dbHelper = new FlowersDatabaseHelper(this.getApplicationContext());
        db = dbHelper.getWritableDatabase();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (dbHelper != null) {

            dbHelper.close();
        }

        if (db != null) {

            db.close();
        }
    }
}
