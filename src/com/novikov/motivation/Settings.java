package com.novikov.motivation;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;


public class Settings extends Activity {
    public static final String TAG = "daily_motivation";
    private static final int MIN_TEXT_SIZE = 6;
    private static final int DEFAULT_TEXT_SIZE = 20;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final SeekBar bar = (SeekBar) findViewById(R.id.text_size_bar);
        final EditText editText = (EditText) findViewById(R.id.text_size_field);
        bar.setProgress(DEFAULT_TEXT_SIZE);
        editText.setText("" + DEFAULT_TEXT_SIZE);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i < MIN_TEXT_SIZE)
                    seekBar.setProgress(MIN_TEXT_SIZE);
                else
                    editText.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                int value = DEFAULT_TEXT_SIZE;
                try {
                    value = Integer.parseInt(textView.getText().toString());
                } catch (Exception ex) {
                    Log.d(TAG, ex.getMessage());
                    textView.setText(String.valueOf(value));
                }
                if (value > bar.getMax()) {
                    bar.setProgress(bar.getMax());
                    textView.setText(String.valueOf(bar.getMax()));
                    return false;
                }
                if (value < MIN_TEXT_SIZE) {
                    bar.setProgress(MIN_TEXT_SIZE);
                    textView.setText(String.valueOf(MIN_TEXT_SIZE));
                    return false;
                }
                bar.setProgress(value);
                return false;
            }
        });
    }

    public void save(View v) {
        int textSize = ((SeekBar) findViewById(R.id.text_size_bar)).getProgress();
        Intent update = new Intent(getApplicationContext(), WidgetProvider.class);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        int mAppWidgetId = 0;
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        update.setAction("WIDGET_CONFIGURED");
        update.putExtra("text_size", textSize);
        update.putExtra("widget_id", mAppWidgetId);
        getApplicationContext().sendBroadcast(update);
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

}