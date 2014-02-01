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
    public static final String TAG = "daily_motivation";


    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        super.onReceive(context, intent);

        if ("FORCE_UPDATE".equals(intent.getAction())) {
            Log.d(TAG, "Force update called");
            forceUpdate(context);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        SharedPreferences preferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        int textSize = preferences.getInt("text_size", 20);
        Log.d(TAG, "Got textSize: " + textSize);
        for (int id : ids) {
            Intent intent = new Intent(context, Settings.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setTextViewTextSize(R.id.large_text_id, TypedValue.COMPLEX_UNIT_DIP, textSize);
            views.setOnClickPendingIntent(R.id.settings_icon, pendingIntent);
            manager.updateAppWidget(id, views);
        }
        Log.d(TAG, "Widgets were updated");
    }

    public void forceUpdate(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds =
                appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));
        onUpdate(context, appWidgetManager, appWidgetIds);
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
