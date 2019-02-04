package com.deneme.professionalartbook;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

public class ArtContentProvider extends ContentProvider {

    static final String PROVIDER_NAME="com.deneme.professionalartbook.ArtContentProvider";
    static final String URL="content://"+PROVIDER_NAME+"/arts";
    static final Uri CONTENT_URI=Uri.parse(URL);

    static final String NAME="name";
    static final String IMAGE="image";

    static final UriMatcher MyUriMatcher;
    static final int ARTS=1;

    static {
        MyUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        MyUriMatcher.addURI(PROVIDER_NAME,"arts",ARTS);
    }

    private static HashMap<String,String> ART_PROJECTION_MAP;


    private SQLiteDatabase mySqLiteDatabase;
    static final String DATABASE_NAME="Arts";
    static final String ARTS_TABLE_NAME="arts";
    static final int DATABASE_VERSION=1;
    static final String CREATE_TABLE_DATABASE="CREATE TABLE "+ARTS_TABLE_NAME+
            " (name TEXT NOT NULL,"+
            " image BLOB NOT NULL)";

    private static class DatabaseHelper extends SQLiteOpenHelper{

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE_DATABASE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ARTS_TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }


    @Override
    public boolean onCreate() {
        Context context=getContext();
        DatabaseHelper databaseHelper=new DatabaseHelper(context);
        mySqLiteDatabase=databaseHelper.getWritableDatabase();
        return mySqLiteDatabase!=null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteQueryBuilder sqLiteQueryBuilder=new SQLiteQueryBuilder();
        sqLiteQueryBuilder.setTables(ARTS_TABLE_NAME);

        switch (MyUriMatcher.match(uri)){
            case ARTS:
                sqLiteQueryBuilder.setProjectionMap(ART_PROJECTION_MAP);


            break;

            default:
        }

        if (sortOrder==null||sortOrder.matches("")){
            sortOrder=NAME;
        }

        Cursor cursor=sqLiteQueryBuilder.query(mySqLiteDatabase,projection,selection,selectionArgs,null,null,sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        long rowId=mySqLiteDatabase.insert(ARTS_TABLE_NAME,"",contentValues);

        if (rowId>0){
            Uri newUri= ContentUris.withAppendedId(CONTENT_URI,rowId);
            getContext().getContentResolver().notifyChange(newUri,null);
            return newUri;
        }

        throw new SQLException("Error!");
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        int rowAffected=0;
        switch (MyUriMatcher.match(uri)){
            case ARTS:
                //DELETE
                rowAffected=mySqLiteDatabase.delete(ARTS_TABLE_NAME,s,strings);
                getContext().getContentResolver().notifyChange(uri,null);
                break;

                default:
                    throw new IllegalArgumentException("Failed URL");
        }

        return rowAffected;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {

        int rowAffected=0;
        switch (MyUriMatcher.match(uri)){
            case ARTS:
                //UPDATE
                rowAffected=mySqLiteDatabase.update(ARTS_TABLE_NAME,contentValues,s,strings);

                break;

            default:
                throw new IllegalArgumentException("Failed URL");
        }
        getContext().getContentResolver().notifyChange(uri,null);

        return rowAffected;
    }
}
