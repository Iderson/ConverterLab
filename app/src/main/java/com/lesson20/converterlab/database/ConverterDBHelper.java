package com.lesson20.converterlab.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
            FIELD_ROW_ID + " text primary key , " +
            "AED_ASK" + " text , " +
            "AED_BID" + " text , " +
            "AMD_ASK" + " text , " +
            "AMD_BID" + " text , " +
            "AUD_ASK" + " text , " +
            "AUD_BID" + " text , " +
            "AZN_ASK" + " text , " +
            "AZN_BID" + " text , " +
            "BGN_ASK" + " text , " +
            "BGN_BID" + " text , " +
            "BRL_ASK" + " text , " +
            "BRL_BID" + " text , " +
            "BYR_ASK" + " text , " +
            "BYR_BID" + " text , " +
            "CAD_ASK" + " text , " +
            "CAD_BID" + " text , " +
            "CHF_ASK" + " text , " +
            "CHF_BID" + " text , " +
            "CLP_ASK" + " text , " +
            "CLP_BID" + " text , " +
            "CNY_ASK" + " text , " +
            "CNY_BID" + " text , " +
            "CYP_ASK" + " text , " +
            "CYP_BID" + " text , " +
            "CZK_ASK" + " text , " +
            "CZK_BID" + " text , " +
            "DKK_ASK" + " text , " +
            "DKK_BID" + " text , " +
            "EEK_ASK" + " text , " +
            "EEK_BID" + " text , " +
            "EGP_ASK" + " text , " +
            "EGP_BID" + " text , " +
            "EUR_ASK" + " text , " +
            "EUR_BID" + " text , " +
            "GBP_ASK" + " text , " +
            "GBP_BID" + " text , " +
            "GEL_ASK" + " text , " +
            "GEL_BID" + " text , " +
            "HKD_ASK" + " text , " +
            "HKD_BID" + " text , " +
            "HRK_ASK" + " text , " +
            "HRK_BID" + " text , " +
            "HUF_ASK" + " text , " +
            "HUF_BID" + " text , " +
            "ILS_ASK" + " text , " +
            "ILS_BID" + " text , " +
            "INR_ASK" + " text , " +
            "INR_BID" + " text , " +
            "IQD_ASK" + " text , " +
            "IQD_BID" + " text , " +
            "ISK_ASK" + " text , " +
            "ISK_BID" + " text , " +
            "JPY_ASK" + " text , " +
            "JPY_BID" + " text , " +
            "KGS_ASK" + " text , " +
            "KGS_BID" + " text , " +
            "KRW_ASK" + " text , " +
            "KRW_BID" + " text , " +
            "KWD_ASK" + " text , " +
            "KWD_BID" + " text , " +
            "KZT_ASK" + " text , " +
            "KZT_BID" + " text , " +
            "LBP_ASK" + " text , " +
            "LBP_BID" + " text , " +
            "LTL_ASK" + " text , " +
            "LTL_BID" + " text , " +
            "LVL_ASK" + " text , " +
            "LVL_BID" + " text , " +
            "MDL_ASK" + " text , " +
            "MDL_BID" + " text , " +
            "MTL_ASK" + " text , " +
            "MTL_BID" + " text , " +
            "MXN_ASK" + " text , " +
            "MXN_BID" + " text , " +
            "NOK_ASK" + " text , " +
            "NOK_BID" + " text , " +
            "NZD_ASK" + " text , " +
            "NZD_BID" + " text , " +
            "PKR_ASK" + " text , " +
            "PKR_BID" + " text , " +
            "PLN_ASK" + " text , " +
            "PLN_BID" + " text , " +
            "ROL_ASK" + " text , " +
            "ROL_BID" + " text , " +
            "RUB_ASK" + " text , " +
            "RUB_BID" + " text , " +
            "SAR_ASK" + " text , " +
            "SAR_BID" + " text , " +
            "SEK_ASK" + " text , " +
            "SEK_BID" + " text , " +
            "SGD_ASK" + " text , " +
            "SGD_BID" + " text , " +
            "SKK_ASK" + " text , " +
            "SKK_BID" + " text , " +
            "THB_ASK" + " text , " +
            "THB_BID" + " text , " +
            "TJS_ASK" + " text , " +
            "TJS_BID" + " text , " +
            "TMT_ASK" + " text , " +
            "TMT_BID" + " text , " +
            "TRY_ASK" + " text , " +
            "TRY_BID" + " text , " +
            "TWD_ASK" + " text , " +
            "TWD_BID" + " text , " +
            "USD_ASK" + " text , " +
            "USD_BID" + " text , " +
            "VND_ASK" + " text , " +
            "VND_BID" + " text  " + " ) ";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_INFO);
        db.execSQL(CREATE_TABLE_CURR);
    }

    public long insert(ContentValues contentValues){
        long rowID = mDB.insert(ORGANIZATION_TABLE, null, contentValues);
        return rowID;
    }

    public long insert(String _currencyTable, ContentValues _values) {
        return mDB.insert(_currencyTable, null, _values);
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

    public Cursor getCurrency(String _id) {
        String selection = ConverterDBHelper.FIELD_ROW_ID + "=?";
        String[] selectionArgs = { _id };
        return mDB.query(CURRENCY_TABLE,
                null,
                selection,
                selectionArgs,
                null, null, null);
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