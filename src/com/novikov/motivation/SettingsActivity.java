package com.novikov.motivation;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingsActivity extends Activity {
    public static final String TAG = "daily_motivation";
    public static final String PREFS = "SettingsPreferences";
    private SharedPreferences settings;
    private Resources resources;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initialize();
        attachTextSizeListener();
    }

    private void initialize() {
        resources = getResources();
        settings = getSharedPreferences(PREFS, 0);
    }

    private void attachTextSizeListener() {
        final SeekBar bar = (SeekBar) findViewById(R.id.text_size_bar);
        final EditText edit = (EditText) findViewById(R.id.text_size_field);
        final int minTextSize = resources.getInteger(R.integer.min_text_size);
        // Загружаем прошлые настройки
        int textSize = settings.getInt("text_size", -1);
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
                int value = getResources().getInteger(R.integer.default_text_size);
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
        int textSize = ((SeekBar) findViewById(R.id.text_size_bar)).getProgress();
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("text_size", textSize);
        editor.commit();
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        int[] ids = manager.getAppWidgetIds(new ComponentName(this, WidgetProvider.class));
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_layout);
        manager.updateAppWidget(ids, views);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.action_save): {
                saveSettings();
                return true;
            }
            default: {
                Log.d(TAG, "Unexpected option id: " + item.getItemId());
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        saveSettings();
    }
}