package com.lesson20.converterlab.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import java.sql.SQLException;

public class ConverterContentProvider extends ContentProvider{

    public static final String PROVIDER_NAME = "com.lesson20.converterlab.organiations";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/organiations" );
    private static final int CONVERTER = 1;
    private static final UriMatcher uriMatcher ;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "organiations", CONVERTER);
    }

    ConverterDBHelper mConverterDBHelper;

    @Override
    public boolean onCreate() {
        mConverterDBHelper = new ConverterDBHelper (getContext());
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID = mConverterDBHelper.insert(values);
        Uri _uri=null;
        if(rowID>0){
            _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
        }else {
            try {
                throw new SQLException("Failed to insert : " + uri);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return _uri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int cnt = 0;
        cnt = mConverterDBHelper.del();
        return cnt;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        if(uriMatcher.match(uri)== CONVERTER){
            return mConverterDBHelper.getOrganizations();
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}