package com.lesson20.converterlab.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.lesson20.converterlab.MainActivity;

import java.sql.SQLException;

public class ConverterDBHelper extends SQLiteOpenHelper{

    private static String DBNAME = "convertersqlite.db";
    private static final   int VERSION = 1;
    public static final String FIELD_ROW_ID = "_id";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_REGION = "region";
    public static final String FIELD_CITY = "city";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_LINK = "link";

    public static final String ORGANIZATION_TABLE = "organizations";
    public static final String CURRENCY_TABLE = "currencies";
    private SQLiteDatabase     mDB;

    public ConverterDBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
        this.mDB = getWritableDatabase();
    }

    private static final String CREATE_TABLE_INFO = "create table " +
            ORGANIZATION_TABLE + " ( " +
            FIELD_ROW_ID + " text primary key , " +
            FIELD_TITLE + " text , " +
            FIELD_REGION + " text , " +
            FIELD_CITY + " text , " +
            FIELD_PHONE + " text , " +
            FIELD_ADDRESS + " text , " +
            FIELD_LINK + " text " + " ) ";

    private static final String CREATE_TABLE_CURR = "create table " +
            CURRENCY_TABLE + " ( " +
            FIELD_ROW_ID + " text primary key ) ";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_INFO);
        db.execSQL(CREATE_TABLE_CURR);
        Log.d(MainActivity.TAG, db.getPath());
    }

    public long insert(ContentValues contentValues){
        long rowID = mDB.replace(ORGANIZATION_TABLE, null, contentValues);

        return rowID;
    }

    public long insert(String _currencyTable, ContentValues _values) {
        return mDB.replace(_currencyTable, null, _values);
    }

    public int del(){
        return mDB.delete(ORGANIZATION_TABLE, null, null);
    }

    public Cursor getOrganizations(){
        return mDB.query(ORGANIZATION_TABLE, new String[]{
                FIELD_ROW_ID,
                FIELD_TITLE,
                FIELD_REGION,
                FIELD_CITY,
                FIELD_PHONE,
                FIELD_ADDRESS,
                FIELD_LINK
        }, null, null, null, null, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ORGANIZATION_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CURRENCY_TABLE);
        onCreate(sqLiteDatabase);
    }

    public void addColumn(String _column){
        if(!isColumnExist(_column))
            mDB.execSQL("ALTER TABLE " + CURRENCY_TABLE
                    + " ADD COLUMN " + _column
                    + " TEXT;");
    }



    public Cursor getCurrency(String _id) {
        String selection = ConverterDBHelper.FIELD_ROW_ID + "=?";
        String[] selectionArgs = { _id };
        return mDB.query(CURRENCY_TABLE,
                null,
                selection,
                selectionArgs,
                null, null, null);
    }

    public boolean isColumnExist(String _column){
        int count = -1;
        Cursor c = null;
        try {
            String query = "SELECT " + _column +" FROM " + CURRENCY_TABLE + " ;";
            c = mDB.rawQuery("pragma table_info ( " + CURRENCY_TABLE + " )", null);
            while (c.moveToNext()) {
                if(c.getString(1).equals(_column))
                    count = 1;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (c != null) {
                c.close();
            }
        }
        return count > 0;
    }

    public Cursor getOrganizationDetail(String _id) {
        String selection = ConverterDBHelper.FIELD_ROW_ID + "=?";
        String[] selectionArgs = { _id };
        return mDB.query(ORGANIZATION_TABLE,
                null,
                selection,
                selectionArgs,
                null, null, null);
    }


}