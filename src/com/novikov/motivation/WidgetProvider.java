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

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        DatabaseConnector connector = new DatabaseConnector(context);
        SQLiteDatabase database = connector.getReadableDatabase();
        for (int id : ids) {
            // Действия по клику на иконке настроек
            Intent intent = new Intent(context, SettingsActivity.class);
            intent.setAction("LAUNCH_SETTINGS");
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
                Log.d(TAG, "Found widget id in DB, load OK");
            }
            else {
                Log.d(TAG, "New widget found, loading default text size");
            }
            cursor.close();
            Log.d(TAG, "Adding intent extra id - " + String.valueOf(id));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setTextViewTextSize(R.id.large_text_id, TypedValue.COMPLEX_UNIT_SP, textSize);
            views.setOnClickPendingIntent(R.id.settings_icon, pendingIntent);
            manager.updateAppWidget(id, views);
        }
        database.close();
        connector.close();
        Log.d(TAG, "Widget was updated");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if ("FORCE_UPDATE".equals(intent.getAction())) {
            AppWidgetManager manager = AppWidgetManager.getInstance(context);
            int id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if (id != AppWidgetManager.INVALID_APPWIDGET_ID)
                onUpdate(context, manager, new int[]{id});
        }
    }
}
