package com.novikov.motivation;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {
    public static final String TAG = "daily_motivation";
    public static final String PREFS = "SettingsPreferences";
    private SharedPreferences settings;
    private Resources resources;
    private int ID = AppWidgetManager.INVALID_APPWIDGET_ID;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        if (!initialize()) {
            Log.d(TAG, "Activity was launched not from widget instance");
            Toast.makeText(this, resources.getString(R.string.direct_launch), Toast.LENGTH_LONG).show();
            finish();
        }
        attachTextSizeListener();
    }

    private boolean initialize() {
        resources = getResources();
        settings = getSharedPreferences(PREFS, 0);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e(TAG, "Error grabbing ID");
            return false;
        }
        ID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if (ID != AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.d(TAG, "Resources initialized");
            return true;
        } else {
            Log.e(TAG, "Error grabbing ID");
            return false;
        }
    }

    private void attachTextSizeListener() {
        final SeekBar bar = (SeekBar) findViewById(R.id.text_size_bar);
        final EditText edit = (EditText) findViewById(R.id.text_size_field);
        final int minTextSize = resources.getInteger(R.integer.min_text_size);
        // Загружаем прошлые настройки
        DatabaseConnector connector = new DatabaseConnector(this);
        SQLiteDatabase database = connector.getReadableDatabase();
        Cursor cursor = database.query(
                DatabaseConnector.WIDGETS_SETTINGS_TABLE,
                new String[]{DatabaseConnector.WidgetSettingsFields.WIDGET_ID, DatabaseConnector.WidgetSettingsFields.TEXT_SIZE},
                DatabaseConnector.WidgetSettingsFields.WIDGET_ID + "=?",
                new String[]{String.valueOf(ID)},
                null,
                null,
                null,
                null);
        int textSize = -1;
        if (cursor.moveToFirst()) {
            textSize = Integer.parseInt(cursor.getString(1));
        }
        cursor.close();
        database.close();
        connector.close();
        if (textSize != -1) {
            bar.setProgress(textSize);
            edit.setText(String.valueOf(textSize));
        }
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i < minTextSize)
                    seekBar.setProgress(minTextSize);
                else
                    edit.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                int value = resources.getInteger(R.integer.default_text_size);
                try {
                    value = Integer.parseInt(textView.getText().toString());
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                    textView.setText(String.valueOf(value));
                }
                if (value > bar.getMax()) {
                    bar.setProgress(bar.getMax());
                    textView.setText(String.valueOf(bar.getMax()));
                    return false;
                }
                if (value < minTextSize) {
                    bar.setProgress(minTextSize);
                    textView.setText(String.valueOf(minTextSize));
                    return false;
                }
                bar.setProgress(value);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void saveSettings() {
        Intent updateWidget = new Intent(this, WidgetProvider.class);
        updateWidget.setAction("FORCE_UPDATE");
        int textSize = ((SeekBar) findViewById(R.id.text_size_bar)).getProgress();
        DatabaseConnector connector = new DatabaseConnector(this);
        SQLiteDatabase database = connector.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseConnector.WidgetSettingsFields.WIDGET_ID, ID);
        values.put(DatabaseConnector.WidgetSettingsFields.TEXT_SIZE, textSize);
        database.insertWithOnConflict(DatabaseConnector.WIDGETS_SETTINGS_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        updateWidget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, ID);
        sendBroadcast(updateWidget);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.action_save): {
                saveSettings();
                finish();
                return true;
            }
            default: {
                Log.e(TAG, "Unexpected option id: " + item.getItemId());
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //saveSettings();
    }
}