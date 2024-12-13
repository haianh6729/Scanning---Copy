package DTO;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHandler extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "deviceManager";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "deviceInfo";
    public static final String KEY_ID = "id";
    public static final String KEY_MODEL_NAME = "modelName";
    public static final String KEY_IMEI = "imei";
    public static final String KEY_SERI = "seri";
    public static final String KEY_LABEL = "label";

    public DatabaseHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        @SuppressLint("DefaultLocale") String createTableCmd = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)", TABLE_NAME, KEY_ID, KEY_MODEL_NAME, KEY_IMEI, KEY_SERI, KEY_LABEL);
        db.execSQL(createTableCmd);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);
        db.execSQL(dropTable);
        onCreate(db);
    }


}
