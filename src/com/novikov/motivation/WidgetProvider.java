package com.novikov.motivation;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

/**
 * Created with IntelliJ IDEA.
 * User: Ilia
 * Date: 31.01.14
 */
public class WidgetProvider extends AppWidgetProvider {
    private static final String TAG = "daily_motivation";

    static {
        Log.d(TAG, "OMG static debug message here");
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        SharedPreferences preferences = context.getSharedPreferences(SettingsActivity.PREFS, 0);
        int textSize = preferences.getInt("text_size", context.getResources().getInteger(R.integer.default_text_size));
        for (int id : ids) {
            // Магия
            Intent intent = new Intent(context, SettingsActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setTextViewTextSize(R.id.large_text_id, TypedValue.COMPLEX_UNIT_DIP, textSize);
            views.setOnClickPendingIntent(R.id.settings_icon, pendingIntent);
            manager.updateAppWidget(id, views);
        }
        Log.d(TAG, "Widget was updated");
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "First widget was created");
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "Last widget was deleted");
    }

    @Override
    public void onDeleted(Context context, int[] ids) {
        Log.d(TAG, "Widget was deleted");
    }
}
