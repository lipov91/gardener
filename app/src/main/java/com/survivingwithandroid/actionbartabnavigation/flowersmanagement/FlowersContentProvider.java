package com.survivingwithandroid.actionbartabnavigation.flowersmanagement;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by Jan Lipka on 2015-08-02.
 */
public class FlowersContentProvider extends ContentProvider {

    // columns
    public static final String _ID = "_id";
    public static final String FLOWERS_TITLE = "title";
    public static final String FLOWERS_BODY = "description";
    // data
    public static final String AUTHORITY =
            "com.survivingwithandroid.actionbartabnavigation.flowersmanagement.FlowersContentProvider";
    // content mime types
    public static final String BASE_DATA_NAME = "flowers";
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE
                    + "/vnd.survivingwithandroid.search." + BASE_DATA_NAME;
    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE
                    + "/vnd.survivingwithandroid.search." + BASE_DATA_NAME;
    // common URIs
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/"
                    + BASE_DATA_NAME);
    public static final Uri SEARCH_SUGGEST_URI =
            Uri.parse("content://" + AUTHORITY + "/"
                    + BASE_DATA_NAME + "/"
                    + SearchManager.SUGGEST_URI_PATH_QUERY);
    // matcher
    private static final int FLOWERS = 0x1000;
    private static final int FLOWER_ITEM = 0x1001;
    private static final int FLOWERS_SEARCH_SUGGEST = 0x1200;
    private static final UriMatcher sURIMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_DATA_NAME, FLOWERS);
        sURIMatcher.addURI(AUTHORITY, BASE_DATA_NAME + "/#", FLOWER_ITEM);
        sURIMatcher.addURI(AUTHORITY, BASE_DATA_NAME + "/"
                + SearchManager.SUGGEST_URI_PATH_QUERY, FLOWERS_SEARCH_SUGGEST);
        sURIMatcher.addURI(AUTHORITY, BASE_DATA_NAME + "/"
                        + SearchManager.SUGGEST_URI_PATH_QUERY + "/*",
                FLOWERS_SEARCH_SUGGEST);
    }

    // custom search suggest column mapping
    private static final HashMap<String, String> FLOWERS_SEARCH_SUGGEST_PROJECTION_MAP;

    static {
        FLOWERS_SEARCH_SUGGEST_PROJECTION_MAP = new HashMap<String, String>();
        FLOWERS_SEARCH_SUGGEST_PROJECTION_MAP.put(_ID, _ID);
        FLOWERS_SEARCH_SUGGEST_PROJECTION_MAP.put(
                SearchManager.SUGGEST_COLUMN_TEXT_1, FLOWERS_TITLE + " AS "
                        + SearchManager.SUGGEST_COLUMN_TEXT_1);
        // this one is only necessary if a full search on the story is needed
        // during suggest time
        // might be too slow to be worthwhile
        // FLOWERS_SEARCH_SUGGEST_PROJECTION_MAP.put(FLOWERS_BODY,
        // FLOWERS_BODY);
        FLOWERS_SEARCH_SUGGEST_PROJECTION_MAP.put(
                SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, _ID + " AS "
                        + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
    }

    // the database
    private FlowersDatabase database;

    @Override
    public int delete(Uri uri, String whereClause, String[] whereArgs) {

        int match = sURIMatcher.match(uri);
        SQLiteDatabase sql = database.getWritableDatabase();
        int rowsAffected = 0;

        switch (match) {

            case FLOWERS:
                rowsAffected =
                        sql.delete(FlowersDatabase.FLOWERS_TABLE,
                                whereClause,
                                whereArgs);
                break;
            case FLOWER_ITEM:
                String id = uri.getLastPathSegment();

                if (TextUtils.isEmpty(whereClause)) {

                    rowsAffected =
                            sql.delete(FlowersDatabase.FLOWERS_TABLE, _ID
                                    + "="
                                    + id, null);
                } else {

                    rowsAffected =
                            sql.delete(FlowersDatabase.FLOWERS_TABLE,
                                    whereClause
                                            + " and " + _ID + "=" + id, whereArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        return rowsAffected;
    }

    @Override
    public String getType(Uri uri) {

        int matchType = sURIMatcher.match(uri);

        switch (matchType) {

            case FLOWERS:
                return CONTENT_TYPE;
            case FLOWER_ITEM:
                return CONTENT_ITEM_TYPE;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        Uri newUri = null;
        int match = sURIMatcher.match(uri);

        if (match == FLOWERS) {

            SQLiteDatabase sql = database.getWritableDatabase();
            long newId =
                    sql.insert(FlowersDatabase.FLOWERS_TABLE, null,
                            values);

            if (newId > 0) {

                newUri = ContentUris.withAppendedId(uri, newId);
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }

        return newUri;
    }

    @Override
    public boolean onCreate() {

        database = new FlowersDatabase(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(FlowersDatabase.FLOWERS_TABLE);
        int match = sURIMatcher.match(uri);

        switch (match) {

            case FLOWERS_SEARCH_SUGGEST:
                // selectionArgs has a single item; the query
                // add wildcards around it
                selectionArgs = new String[]{"%" + selectionArgs[0] + "%"};
                queryBuilder
                        .setProjectionMap(FLOWERS_SEARCH_SUGGEST_PROJECTION_MAP);
                break;
            case FLOWERS:
                break;
            case FLOWER_ITEM:
                String id = uri.getLastPathSegment();
                queryBuilder.appendWhere(_ID + "=" + id);
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri);
        }
        SQLiteDatabase sql = database.getReadableDatabase();
        Cursor cursor =
                queryBuilder.query(sql, projection, selection, selectionArgs, null,
                        null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // no updates allowed :)
        return 0;
    }

    private class FlowersDatabase extends SQLiteOpenHelper {

        private static final String FLOWERS_TABLE = "flowers";
        private static final String FLOWERS_DB_NAME = "flowers_db";
        private static final int SCHEMA_VERSION = 1;
        private static final String FLOWERS_SCHEMA =
                "CREATE TABLE " + FLOWERS_TABLE + "("
                        + _ID + " integer primary key autoincrement, "
                        + FLOWERS_TITLE + " text NOT NULL, "
                        + FLOWERS_BODY + " text NOT NULL"
                        + ");";
        private static final String UPGRADE_DB_SCHEMA =
                "DROP TABLE IF EXISTS " + FLOWERS_TABLE;

        public FlowersDatabase(Context context) {

            super(context, FLOWERS_DB_NAME, null, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(FLOWERS_SCHEMA);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL(UPGRADE_DB_SCHEMA);
            onCreate(db);
        }
    }
}
