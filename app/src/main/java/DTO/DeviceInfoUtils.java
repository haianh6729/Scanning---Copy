package DTO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeviceInfoUtils {
    SQLiteDatabase db;
    DatabaseHandler dbHandler;

    public String[] AddToStringArray(String[] oldArray, String newString)
    {
        String[] newArray = Arrays.copyOf(oldArray, oldArray.length+1);
        newArray[oldArray.length] = newString;
        return newArray;
    }
    public DeviceInfoUtils(Context context){
        dbHandler = new DatabaseHandler(context);
    }

    public void createScannedTable(){
        String createTableCmd = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)", DatabaseHandler.TABLE_NAME, DatabaseHandler.KEY_ID, DatabaseHandler.KEY_MODEL_NAME, DatabaseHandler.KEY_IMEI, DatabaseHandler.KEY_SERI, DatabaseHandler.KEY_LABEL);
        db.execSQL(createTableCmd);
    }

    public void replaceTable(){
        String dropTable = String.format("DROP TABLE IF EXISTS %s", DatabaseHandler.TABLE_NAME);
        db.execSQL(dropTable);
        String createTableCmd = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)", DatabaseHandler.TABLE_NAME, DatabaseHandler.KEY_ID, DatabaseHandler.KEY_MODEL_NAME, DatabaseHandler.KEY_IMEI, DatabaseHandler.KEY_SERI, DatabaseHandler.KEY_LABEL);
        db.execSQL(createTableCmd);

    }
    public void openDB(){
        db = dbHandler.getWritableDatabase();
    }

    public void closeDB(){
        dbHandler.close();
    }

    public boolean isMatch(String imeiSeriLabel){
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHandler.TABLE_NAME + " WHERE " + DatabaseHandler.KEY_IMEI + " = ? OR " + DatabaseHandler.KEY_SERI + " = ? OR " + DatabaseHandler.KEY_LABEL + " = ?",  new String[]{imeiSeriLabel, imeiSeriLabel, imeiSeriLabel});
        if ((cursor.getCount()) == 1) {
            Log.i("DDD", "cur =1, " + imeiSeriLabel);
            return true;
        } else {
            return false;
        }
    }

    public String getModelNameListString(){

        Cursor cursor = db.rawQuery("SELECT DISTINCT "+ DatabaseHandler.KEY_MODEL_NAME +" FROM " + DatabaseHandler.TABLE_NAME, null);
        cursor.moveToFirst();
//        String[] modelNameList ={} ;
        String modelNameList ="";
        while (!(cursor.isAfterLast())){
            Cursor cursorCount = db.rawQuery(String.format("SELECT %s FROM %s WHERE %s='%s'", DatabaseHandler.KEY_MODEL_NAME , DatabaseHandler.TABLE_NAME, DatabaseHandler.KEY_MODEL_NAME , cursor.getString(0)) , null);
//            Cursor cursorCount = db.rawQuery("SELECT COUNT(" + DatabaseHandler.KEY_MODEL_NAME + ")"  +" FROM " + DatabaseHandler.TABLE_NAME , null);


//            modelNameList = AddToStringArray(modelNameList ,cursor.getString(1));
            modelNameList = modelNameList + "\n" + String.format("%s : %s", cursor.getString(0), String.valueOf(cursorCount.getCount()));
            cursor.moveToNext();
        }

        return modelNameList;
    }

    public String[] getModelNameList(){

        Cursor cursor = db.rawQuery("SELECT DISTINCT "+ DatabaseHandler.KEY_MODEL_NAME +" FROM " + DatabaseHandler.TABLE_NAME, null);
        cursor.moveToFirst();
        String[] modelNameList ={} ;
//        String modelNameList ="";
        List<String> modelNameLList = new ArrayList<String>();
        while (!(cursor.isAfterLast())){
            Cursor cursorCount = db.rawQuery(String.format("SELECT %s FROM %s WHERE %s='%s'", DatabaseHandler.KEY_MODEL_NAME , DatabaseHandler.TABLE_NAME, DatabaseHandler.KEY_MODEL_NAME , cursor.getString(0)) , null);
//            Cursor cursorCount = db.rawQuery("SELECT COUNT(" + DatabaseHandler.KEY_MODEL_NAME + ")"  +" FROM " + DatabaseHandler.TABLE_NAME , null);


//            modelNameLList = AddToStringArray(modelNameLList ,cursor.getString(1));
//            modelNameLList = modelNameLList + "\n" + String.format("%s : %s", cursor.getString(0), String.valueOf(cursorCount.getCount()));
            modelNameLList.add(cursor.getString(0) + " : "+ String.valueOf(cursorCount.getCount()));

            cursor.moveToNext();
        }
        modelNameList = modelNameLList.toArray(modelNameList);
        return modelNameList;
    }

    public int getAmountOnModel(String modelName){
        String[] arrOfStr = modelName.split(":", 0);
        modelName = arrOfStr[0].trim();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHandler.TABLE_NAME + " WHERE " + DatabaseHandler.KEY_MODEL_NAME + "=" + "'" +
                modelName + "'", null);
        Log.i("getAmountOnModel","SELECT * FROM " + DatabaseHandler.TABLE_NAME + " WHERE " + DatabaseHandler.KEY_MODEL_NAME + "=" + modelName);
        Log.i("getAmountOnModel",String.valueOf(cursor.getCount()));
        return cursor.getCount();
    }

    public int getTotalModel(){
        Cursor cursor = db.rawQuery("SELECT DISTINCT "+ DatabaseHandler.KEY_MODEL_NAME +" FROM " + DatabaseHandler.TABLE_NAME, null);
        cursor.moveToFirst();
        while (!(cursor.isAfterLast())){
            Log.i("CCCCCCC", cursor.getString(0));
            cursor.moveToNext();
        }
        return cursor.getCount();
    }
    public int getTotalSet(){
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHandler.TABLE_NAME, null);

        return cursor.getCount();
    }

    public String[] getInfoOfDev(String info){
//        Cursor cursor = db.rawQuery("SELECT * FROM "+ DatabaseHandler.TABLE_NAME , null);
        Cursor cursor = db.rawQuery("SELECT * FROM "+ DatabaseHandler.TABLE_NAME +" WHERE " + DatabaseHandler.KEY_IMEI + " = ? OR " + DatabaseHandler.KEY_SERI + " = ? OR " + DatabaseHandler.KEY_LABEL + " = ?", new String[]{info, info, info});
        Log.i("SSSSSSSSSSSSSSS", String.valueOf(cursor.getCount()));
        Log.i("SSSSSSSSSSSSSSS", info);
        cursor.moveToFirst();
        String[] infoL = {cursor.getString(2), cursor.getString(3), cursor.getString(4)};
        return infoL;
    }

    public boolean addDeviceInfo(DeviceInfo dv) {
        openDB();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHandler.KEY_MODEL_NAME, dv.getModelName());
        contentValues.put(DatabaseHandler.KEY_IMEI, dv.getImei());
        contentValues.put(DatabaseHandler.KEY_SERI, dv.getSeriNum());
        contentValues.put(DatabaseHandler.KEY_LABEL, dv.getLabel());

        long idDev;
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM "+ DatabaseHandler.TABLE_NAME +" WHERE " + DatabaseHandler.KEY_IMEI + " = ? OR " + DatabaseHandler.KEY_SERI + " = ? OR " + DatabaseHandler.KEY_LABEL + " = ?", new String[]{dv.getImei(), dv.getSeriNum(), dv.getLabel()});

            idDev = 0;
            Log.i("DDD", "cursor"+ cursor.getCount());
            if ((cursor.getCount()) == 0) {
                idDev = db.insert(DatabaseHandler.TABLE_NAME, null, contentValues);
                Log.i("DDD", "cur > 0" + String.valueOf(idDev));
            }
        } catch (SQLiteException e) {
            idDev = db.insert(DatabaseHandler.TABLE_NAME, null, contentValues);
            Log.i("DDD exception", String.valueOf(idDev));
        }


        if (idDev != 0) {
            return true;
        } else {
            return false;
        }
    }
}

