package com.survivingwithandroid.actionbartabnavigation.flowersmanagement;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.survivingwithandroid.actionbartabnavigation.R;
import com.survivingwithandroid.actionbartabnavigation.database.FlowersDatabase;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by lipov91 on 2015-07-23.
 */
public class FlowersManagementActivity extends FlowersDatabaseActivity
        implements TextToSpeech.OnInitListener {

    private static final int VOICE_RECOGNITION_REQUEST = 1;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flowers_management);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        fillAutocompleteFromDatabase();

        textToSpeech = new TextToSpeech(this, this);
    }

    @Override
    protected void onDestroy() {

        textToSpeech.shutdown();
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {

        Button btnSpeak = (Button) findViewById(R.id.btnSpeak);

        if (status == TextToSpeech.SUCCESS) {

            int result = textToSpeech.setLanguage(Locale.ENGLISH);

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {

                Toast.makeText(this, "Wybrany jêzyk jest nieobs³ugiwany.",
                        Toast.LENGTH_LONG).show();
                btnSpeak.setEnabled(false);

            } else {

                btnSpeak.setEnabled(true);
            }

        } else {

            Toast.makeText(this, "Wyst¹pi³ problem zwi¹zany z silnikiem TTS.",
                    Toast.LENGTH_LONG).show();
            btnSpeak.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == VOICE_RECOGNITION_REQUEST && resultCode == RESULT_OK) {

            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            EditText etFlowerName = (EditText) findViewById(R.id.etFlowerName);
            etFlowerName.setText(matches.get(0));

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void fillAutocompleteFromDatabase() {

        cursor = db.query(FlowersDatabase.Categories.CATEGORIES_TABLE_NAME,
                new String[]{FlowersDatabase.Categories.CATEGORY_NAME,
                        FlowersDatabase.Categories._ID},
                null, null, null, null,
                FlowersDatabase.Categories.DEFAULT_SORT_ORDER);

        startManagingCursor(cursor);

        int numberOfCategories = cursor.getCount();
        String result[] = new String[numberOfCategories];

        if ((numberOfCategories > 0) && cursor.moveToFirst()) {

            for (int i = 0; i < numberOfCategories; i++) {

                result[i] =
                        cursor.getString(cursor.getColumnIndex(
                                FlowersDatabase.Categories.CATEGORY_NAME));
                cursor.moveToNext();
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, result);

            AutoCompleteTextView autoCompleteTV =
                    (AutoCompleteTextView) findViewById(R.id.autoCompleteTV);
            autoCompleteTV.setAdapter(adapter);
        }
    }

    public void onClick(View view) {

        if (view.getId() == R.id.btnSpeak) {

            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Proszê mówiæ powoli i wyraŸnie");
            startActivityForResult(intent, VOICE_RECOGNITION_REQUEST);

        } else if (view.getId() == R.id.btnSave) {

            final EditText etFlowerName = (EditText) findViewById(R.id.etFlowerName);
            final EditText etCategory = (EditText) findViewById(R.id.autoCompleteTV);
            long rowId = 0;
            Cursor c;

            db.beginTransaction();

            try {

                String category = etCategory.getText().toString().toLowerCase();

                SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
                queryBuilder.setTables(FlowersDatabase.Categories.CATEGORIES_TABLE_NAME);
                queryBuilder.appendWhere(FlowersDatabase.Categories.CATEGORY_NAME + "='"
                        + category + "'");
                c = queryBuilder.query(db, null, null, null, null, null, null);

                if (c.getCount() == 0) {

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(FlowersDatabase.Categories.CATEGORY_NAME, category);
                    rowId = db.insert(FlowersDatabase.Categories.CATEGORIES_TABLE_NAME,
                            FlowersDatabase.Categories.CATEGORY_NAME, contentValues);
                    fillAutocompleteFromDatabase();

                } else {

                    c.moveToFirst();
                    rowId = c.getLong(c.getColumnIndex(FlowersDatabase.Categories._ID));
                }

                c.close();

                ContentValues contentValues = new ContentValues();
                contentValues.put(FlowersDatabase.Flowers.FLOWER_NAME,
                        etFlowerName.getText().toString());
                contentValues.put(FlowersDatabase.Flowers.FLOWER_CATEGORY_ID, rowId);
                db.insert(FlowersDatabase.Flowers.FLOWERS_TABLE_NAME,
                        FlowersDatabase.Flowers.FLOWER_CATEGORY_ID, contentValues);
                db.setTransactionSuccessful();

            } finally {

                db.endTransaction();
            }

            etFlowerName.setText(null);
            etCategory.setText(null);

        } else {

            Intent intent = new Intent(FlowersManagementActivity.this, OwnFlowersListActivity.class);
            startActivity(intent);
        }
    }
}
