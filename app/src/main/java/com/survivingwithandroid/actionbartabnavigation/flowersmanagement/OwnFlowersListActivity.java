package com.survivingwithandroid.actionbartabnavigation.flowersmanagement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.survivingwithandroid.actionbartabnavigation.R;
import com.survivingwithandroid.actionbartabnavigation.database.FlowersDatabase;

/**
 * Created by lipov91 on 2015-07-23.
 */
public class OwnFlowersListActivity extends FlowersDatabaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_flowers_list);

        fillFlowersList();
    }

    public void fillFlowersList() {

        ListAdapter adapter;
        ListView lvMyFlowers;

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(FlowersDatabase.Flowers.FLOWERS_TABLE_NAME + ", "
                + FlowersDatabase.Categories.CATEGORIES_TABLE_NAME);
        queryBuilder.appendWhere(FlowersDatabase.Flowers.FLOWERS_TABLE_NAME + "."
                + FlowersDatabase.Flowers.FLOWER_CATEGORY_ID + " = "
                + FlowersDatabase.Categories.CATEGORIES_TABLE_NAME + ". "
                + FlowersDatabase.Categories._ID);

        String columnsToReturn[] = {
                FlowersDatabase.Flowers.FLOWERS_TABLE_NAME + "."
                        + FlowersDatabase.Flowers.FLOWER_NAME,
                FlowersDatabase.Flowers.FLOWERS_TABLE_NAME + "."
                        + FlowersDatabase.Flowers._ID,
                FlowersDatabase.Categories.CATEGORIES_TABLE_NAME + "."
                        + FlowersDatabase.Categories.CATEGORY_NAME
        };

        cursor = queryBuilder.query(db, columnsToReturn, null, null, null, null,
                FlowersDatabase.Flowers.DEFAULT_SORT_ORDER);
        startManagingCursor(cursor);
        adapter = new SimpleCursorAdapter(this,
                R.layout.item_own_flower,
                cursor, new String[]{
                FlowersDatabase.Flowers.FLOWER_NAME,
                FlowersDatabase.Categories.CATEGORY_NAME
        },
                new int[]{
                        R.id.tvFlowerName,
                        R.id.tvFlowerCategory
                });
        lvMyFlowers = (ListView) findViewById(R.id.lvMyFlowers);
        lvMyFlowers.setAdapter(adapter);
        lvMyFlowers.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final long deleteId = id;
                RelativeLayout layout = (RelativeLayout) view;
                TextView tvFlowerName = (TextView) layout.findViewById(R.id.tvFlowerName);
                String flowerName = tvFlowerName.getText().toString();

                new AlertDialog.Builder(OwnFlowersListActivity.this)
                        .setMessage("Czy na pewno chcesz usun¹æ roœlinê " + flowerName + "?")
                        .setPositiveButton("Usuñ",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        deleteFlower(deleteId);
                                        cursor.requery();
                                    }
                                }).show();
            }
        });
    }

    public void deleteFlower(Long deleteId) {

        String strArgs[] = { deleteId.toString() };
        db.delete(FlowersDatabase.Flowers.FLOWERS_TABLE_NAME,
                FlowersDatabase.Flowers._ID + "=?", strArgs);

    }

    public void onClick(View view) {

        finish();
    }
}
