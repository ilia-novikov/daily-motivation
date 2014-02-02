package com.novikov.motivation;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

/**
 * Created with IntelliJ IDEA.
 * User: Ilia
 * Date: 31.01.14
 */
public class WidgetProvider extends AppWidgetProvider {
    private static final String TAG = SettingsActivity.TAG;

    private void updateWidget(Context context, int id) {
        DatabaseConnector connector = new DatabaseConnector(context);
        SQLiteDatabase database = connector.getReadableDatabase();
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.setAction("LAUNCH_SETTINGS" + id);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Cursor cursor = database.query(
                DatabaseConnector.WIDGETS_SETTINGS_TABLE,
                new String[]{DatabaseConnector.WidgetSettingsFields.WIDGET_ID, DatabaseConnector.WidgetSettingsFields.TEXT_SIZE},
                DatabaseConnector.WidgetSettingsFields.WIDGET_ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null);
        int textSize = context.getResources().getInteger(R.integer.default_text_size);
        if (cursor.moveToFirst()) {
            textSize = Integer.parseInt(cursor.getString(1), textSize);
        }
        cursor.close();
        database.close();
        connector.close();
        views.setTextViewTextSize(R.id.large_text_id, TypedValue.COMPLEX_UNIT_SP, textSize);
        views.setOnClickPendingIntent(R.id.settings_icon, pendingIntent);
        AppWidgetManager.getInstance(context).updateAppWidget(id, views);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        for (int id : ids) {
            Intent intent = new Intent(context, SettingsActivity.class);
            intent.setAction("LAUNCH_SETTINGS" + String.valueOf(id));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.settings_icon, pendingIntent);
            manager.updateAppWidget(id, views);
        }
        Log.d(TAG, "Widgets was updated");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if ("FORCE_UPDATE".equals(intent.getAction())) {
            int id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if (id != AppWidgetManager.INVALID_APPWIDGET_ID)
                updateWidget(context, id);
        }
    }
}
