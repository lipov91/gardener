package com.survivingwithandroid.actionbartabnavigation.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lipov91 on 2015-07-23.
 */
public class FlowersDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "flowers.db";
    private static final int DATABASE_VERSION = 1;

    public FlowersDatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + FlowersDatabase.Categories.CATEGORIES_TABLE_NAME
                + "(" + FlowersDatabase.Categories._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FlowersDatabase.Categories.CATEGORY_NAME + " TEXT"
                + ");");

        db.execSQL("CREATE TABLE " + FlowersDatabase.Flowers.FLOWERS_TABLE_NAME
                + "(" + FlowersDatabase.Flowers._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FlowersDatabase.Flowers.FLOWER_NAME + " TEXT, "
                + FlowersDatabase.Flowers.FLOWER_CATEGORY_ID + " INTEGER"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
