package com.lesson20.converterlab.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConverterDBHelper extends SQLiteOpenHelper{

    private static String DBNAME = "convertersqlite";
    private static int VERSION = 1;
    public static final String FIELD_ROW_ID = "_id";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_REGION = "region";
    public static final String FIELD_CITY = "city";
    public static final String FIELD_PHONE = "phone";
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_LINK = "link";
    private static final String ORGANIZATION_TABLE = "organizations";
    private static final String DETAIL_TABLE = "details";
    private static final String CURRENCY_TABLE = "currencies";
    private SQLiteDatabase mDB;

    public ConverterDBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
        this.mDB = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql =     "create table " + ORGANIZATION_TABLE + " ( " +
                FIELD_ROW_ID + " text primary key , " +
                FIELD_TITLE + " text , " +
                FIELD_REGION + " text , " +
                FIELD_CITY + " text , " +
                FIELD_PHONE + " text , " +
                FIELD_ADDRESS + " text , " +
                FIELD_LINK + " text " +
                " ) ";

        db.execSQL(sql);
    }

    public long insert(ContentValues contentValues){
        long rowID = mDB.insert(ORGANIZATION_TABLE, null, contentValues);
        return rowID;
    }

    public int del(){
        int cnt = mDB.delete(ORGANIZATION_TABLE, null , null);
        return cnt;
    }

    public Cursor getOrganizations(){
        return mDB.query(ORGANIZATION_TABLE, new String[] {
                FIELD_ROW_ID,
                FIELD_TITLE,
                FIELD_REGION,
                FIELD_CITY,
                FIELD_PHONE,
                FIELD_ADDRESS,
                FIELD_LINK
        } , null, null, null, null, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}