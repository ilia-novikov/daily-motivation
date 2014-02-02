package com.novikov.motivation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: Ilia
 * Date: 02.02.14
 */
public class DatabaseConnector extends SQLiteOpenHelper {

    public class WidgetSettingsFields {
        public static final String WIDGET_ID = "widget_id";
        public static final String TEXT_SIZE = "text_size";
    }

    private static final String DB_NAME = "motivation_db";
    public static final String WIDGETS_SETTINGS_TABLE = "widgets_settings";
    private static final int VERSION = 1;
    private static final String TAG = SettingsActivity.TAG;

    public DatabaseConnector(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "First run, creating DB");
        Log.i(TAG, "Creating table " + WIDGETS_SETTINGS_TABLE + " in " + DB_NAME + " database");
        db.execSQL("create table " + WIDGETS_SETTINGS_TABLE + " ("
                + "widget_id integer primary key,"
                + "text_size integer" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
