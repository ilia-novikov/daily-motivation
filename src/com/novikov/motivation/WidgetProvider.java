package com.novikov.motivation;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
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

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        SharedPreferences preferences = context.getSharedPreferences(SettingsActivity.PREFS, 0);
        int textSize = preferences.getInt("text_size", context.getResources().getInteger(R.integer.default_text_size));
        for (int id : ids) {
            // Действия по клику на иконке настроек
            Intent intent = new Intent(context, SettingsActivity.class);
            intent.setAction("LAUNCH_SETTINGS");
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setTextViewTextSize(R.id.large_text_id, TypedValue.COMPLEX_UNIT_SP, textSize);
            views.setOnClickPendingIntent(R.id.settings_icon, pendingIntent);
            manager.updateAppWidget(id, views);
        }
        Log.d(TAG, "Widget was updated");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if ("FORCE_UPDATE".equals(intent.getAction())) {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int[] ids = manager.getAppWidgetIds(new ComponentName(context, this.getClass()));
            int id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (id != -1)
                onUpdate(context, manager, new int[]{id});
        }
    }
}
