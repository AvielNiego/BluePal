package com.avielniego.openhvr.data.storedData;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.avielniego.openhvr.data.storedData.RestaurantContract.RestaurantEntry;

public class RestaurantDbHelper extends SQLiteOpenHelper
{
    private static final int    DATABASE_VERSION = 1;
    public static final  String DATABASE_NAME    = "restaurant.db";

    public RestaurantDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + RestaurantEntry.TABLE_NAME + " (" +
                RestaurantEntry._ID + " INTEGER  PRIMARY KEY AUTOINCREMENT, " +
                RestaurantEntry.COLUMN_RESTAURANT_NAME + " TEXT NOT NULL, " +
                RestaurantEntry.COLUMN_RESTAURANT_ADDRESS + " TEXT NOT NULL, " +
                RestaurantEntry.COLUMN_RESTAURANT_AREA + " TEXT NOT NULL, " +
                RestaurantEntry.COLUMN_RESTAURANT_CATEGORY + " TEXT NOT NULL, " +
                RestaurantEntry.COLUMN_RESTAURANT_CITY + " TEXT NOT NULL, " +
                RestaurantEntry.COLUMN_RESTAURANT_WEBSITE + " TEXT NOT NULL, " +
                RestaurantEntry.COLUMN_RESTAURANT_TYPE + " TEXT NOT NULL, " +
                RestaurantEntry.COLUMN_RESTAURANT_DESC + " TEXT NOT NULL, " +
                RestaurantEntry.COLUMN_RESTAURANT_HANDICAP + " TEXT NOT NULL, " +
                RestaurantEntry.COLUMN_RESTAURANT_IS_KOSHER + " TEXT NOT NULL, " +
                RestaurantEntry.COLUMN_RESTAURANT_PHONE + " TEXT NOT NULL, " +
                RestaurantEntry.COLUMN_RESTAURANT_IMAGE + " TEXT NOT NULL, " +
                RestaurantEntry.COLUMN_RESTAURANT_WEEK_OPEN_HOURS + " TEXT NOT NULL, " +
                RestaurantEntry.COLUMN_RESTAURANT_FRIDAY_OPEN_HOURS + " TEXT NOT NULL, " +
                RestaurantEntry.COLUMN_RESTAURANT_SAT_OPEN_HOURS + " TEXT NOT NULL, " +
                RestaurantEntry.COLUMN_RESTAURANT_LONGITUDE + " REAL NOT NULL, " +
                RestaurantEntry.COLUMN_RESTAURANT_LATITUDE + " REAL NOT NULL" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RestaurantEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
