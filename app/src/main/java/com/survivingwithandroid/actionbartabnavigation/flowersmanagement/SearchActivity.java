package com.survivingwithandroid.actionbartabnavigation.flowersmanagement;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.SearchView;

import com.survivingwithandroid.actionbartabnavigation.R;

/**
 * Created by Jan Lipka on 2015-08-02.
 */
public class SearchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(this, SearchableActivity.class)));
        searchView.setIconified(true);

        return true;
    }

    public void onClick(View view) {

        if (view.getId() == R.id.btnResetData) {

            deleteAllFlowers();
            addFlower("Berberys Thunberga",
                    "Pochodz¹cy z Japonii gatunek krzewu ozdobnego o ciernistych pêdach i owalnych, "
                            + "sezonowych liœciach.");
            addFlower("Sosna górska var. pumilio",
                    "Odmiana wyj¹tkowo odporna na niesprzyjaj¹ce warunki.");

        } else if (view.getId() == R.id.btnSearch) {

            onSearchRequested();

        } else if (view.getId() == R.id.btnDeviceSearchSettings) {

            Intent intent = new Intent(SearchManager.INTENT_ACTION_SEARCH_SETTINGS);
            startActivity(intent);
        }
    }

    private void deleteAllFlowers() {

        getContentResolver().delete(FlowersContentProvider.CONTENT_URI, null, null);
    }

    private void addFlower(String name, String description) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(FlowersContentProvider.FLOWERS_TITLE, name);
        contentValues.put(FlowersContentProvider.FLOWERS_BODY, description);

        getContentResolver().insert(FlowersContentProvider.CONTENT_URI, contentValues);
    }
}
