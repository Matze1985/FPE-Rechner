package de.crazyinfo.fpe_rechner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by l.boettcher on 04.10.2017.
 */

public class FoodDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FoodDB.db";
    private static final String TABLE_NAME = "Food";
    private static final String Col1 = "id";
    private static final String Col2 = "name";
    private static final String Col3 = "cho";
    private static final String Col4 = "kcal";

    public FoodDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + Col1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Col2 + " TEXT UNIQUE, "+Col3+" REAL, "+Col4+" REAL"+")");

        Cursor res = db.rawQuery("select * from " + TABLE_NAME + ";", null);
        if (res.getCount() == 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(Col2, "Steak \uD83D\uDC37 100g");
            contentValues.put(Col3, "0");
            contentValues.put(Col4, "112");
            db.insert(TABLE_NAME, null, contentValues);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    public boolean addDataFood(String NAME, String CHO, String KCAL){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col2, NAME);
        contentValues.put(Col3, CHO);
        contentValues.put(Col4, KCAL);
        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1){
            db.update(TABLE_NAME, contentValues, "name = ?", new String[] {String.valueOf(NAME)});
            return true;
        } else {
            return true;
        }

    }

    public boolean addFood(String NAME, String CHO, String KCAL){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col2, NAME);
        contentValues.put(Col3, CHO);
        contentValues.put(Col4, KCAL);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;

    }

    public boolean updateFood(long id, String NAME, String CHO, String KCAL){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col2, NAME);
        contentValues.put(Col3, CHO);
        contentValues.put(Col4, KCAL);
        db.update(TABLE_NAME, contentValues, "id = ?", new String[] {String.valueOf(id)});
        return true;

    }

    public Cursor getFood(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res1 = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id = "+ id + ";",null);
        return res1;
    }

    public Cursor checkFood(String chkname){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res1 = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE name = '"+ chkname + "';",null);
        return res1;
    }

    public Cursor getAllFood() {
        SQLiteDatabase db = this.getWritableDatabase();
        //Cursor resFood = db.rawQuery("select rowid _id,* from " + TABLE_NAME + ";", null);
        Cursor resFood = db.rawQuery("SELECT rowid _id,* FROM " + TABLE_NAME + " ORDER BY name;", null);
        return resFood;
    }

    public Cursor getAllDataFood() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT NAME, CHO, KCAL FROM " + TABLE_NAME + ";", null);
        return res;
    }

    public boolean removeFood(long rowId){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,"id = " + rowId + ";", null) > 0;
    }
}
