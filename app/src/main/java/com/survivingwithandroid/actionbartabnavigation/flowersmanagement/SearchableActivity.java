package com.survivingwithandroid.actionbartabnavigation.flowersmanagement;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class SearchableActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        checkIntent(intent);
    }

    @Override
    protected void onNewIntent(Intent newIntent) {

        setIntent(newIntent);
        checkIntent(newIntent);
    }

    private void checkIntent(Intent intent) {

        String query = "";
        String intentAction = intent.getAction();

        if (Intent.ACTION_SEARCH.equals(intentAction)) {

            query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, query, Toast.LENGTH_LONG)
                    .show();

        } else if (Intent.ACTION_VIEW.equals(intentAction)) {

            Uri details = intent.getData();
            Intent detailsIntent = new Intent(Intent.ACTION_VIEW, details);
            startActivity(detailsIntent);
            finish();
        }
        fillList(query);
    }

    private void fillList(String query) {

        String wildcardQuery = "%" + query + "%";
        Cursor cursor =
                managedQuery(
                        FlowersContentProvider.CONTENT_URI,
                        null,
                        FlowersContentProvider.FLOWERS_TITLE + " LIKE ? OR "
                                + FlowersContentProvider.FLOWERS_BODY + " LIKE ?",
                        new String[]{wildcardQuery, wildcardQuery}, null);
        ListAdapter adapter =
                new SimpleCursorAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        cursor,
                        new String[]{FlowersContentProvider.FLOWERS_TITLE},
                        new int[]{android.R.id.text1});
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        Uri description = Uri.withAppendedPath(FlowersContentProvider.CONTENT_URI, "" + id);
        Intent intent = new Intent(Intent.ACTION_VIEW, description);

        startActivity(intent);
    }
}
