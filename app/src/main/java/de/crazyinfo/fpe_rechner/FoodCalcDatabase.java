package de.crazyinfo.fpe_rechner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by l.boettcher on 06.10.2017.
 */

public class FoodCalcDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FoodCalcDB.db";
    private static final String TABLE_NAME = "FoodCalc";
    private static final String Col1 = "name";
    private static final String Col2 = "cho";
    private static final String Col3 = "kcal";

    public FoodCalcDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + Col1 + " TEXT, "+Col2+" REAL, "+Col3+" REAL"+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public boolean addFoodCalc(String NAME, String CHO, String KCAL){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col1, NAME);
        contentValues.put(Col2, CHO);
        contentValues.put(Col3, KCAL);
        long result = db.insert(TABLE_NAME, null, contentValues);

        return result != -1;

    }

    public Cursor getFoodCalc(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res1 = db.rawQuery("SELECT rowid _id, * FROM " + TABLE_NAME + " WHERE rowid = "+ id + ";",null);
        return res1;
    }

    public boolean removeFoodCalc(String rowId){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,"rowid = " + rowId + ";", null) > 0;
    }

    public Cursor getAllFoodCalc() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor resFood = db.rawQuery("SELECT rowid _id,* FROM " + TABLE_NAME + ";", null);
        return resFood;
    }

    public void removeAllFoodCalc(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);
        db.execSQL("delete from "+ TABLE_NAME);
        db.close();
    }
}