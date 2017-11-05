package de.crazyinfo.fpe_rechner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by l.boettcher on 28.09.2017.
 */

public class FactorDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FactorDB.db";
    private static final String TABLE_NAME = "Factor";
    private static final String COL1 = "hour";
    private static final String COL2 = "factor";


    public FactorDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COL1  + " INTEGER PRIMARY KEY, " + COL2 + " REAL)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public boolean addDataFactor(int Hour,String Factor){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, Hour);
        contentValues.put(COL2, Factor);
        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1){
            db.update(TABLE_NAME, contentValues, "hour = ?", new String[] {String.valueOf(Hour)});
            return true;
        } else {
            return true;
        }

    }

    public Cursor checkFactor(int Hour){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res1 = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE hour = "+ Hour + ";",null);
        return res1;
    }

    public Cursor getAllDataFactor() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME + ";", null);
        return res;
    }

}

