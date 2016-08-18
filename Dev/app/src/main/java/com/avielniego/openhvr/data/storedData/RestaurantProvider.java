package com.avielniego.openhvr.data.storedData;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.avielniego.openhvr.data.storedData.RestaurantContract.RestaurantEntry;

public class RestaurantProvider extends ContentProvider
{
    private static final int RESTAURANT = 100;

    private UriMatcher uriMatcher = buildUriMatcher();
    private RestaurantDbHelper restaurantDbHelper;

    public static UriMatcher buildUriMatcher()
    {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        String contentAuthority = RestaurantContract.CONTENT_AUTHORITY;
        uriMatcher.addURI(contentAuthority, RestaurantContract.PATH_RESTAURANT, RESTAURANT);

        return uriMatcher;
    }

    @Override
    public boolean onCreate()
    {
        restaurantDbHelper = new RestaurantDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        switch (uriMatcher.match(uri))
        {
            case RESTAURANT:
                return restaurantDbHelper.getReadableDatabase()
                        .query(RestaurantEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri)
    {
        switch (uriMatcher.match(uri))
        {
            case RESTAURANT:
                return RestaurantEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues)
    {
        notifyChange(uri);
        switch (uriMatcher.match(uri))
        {
            case RESTAURANT:
                long _id = restaurantDbHelper.getWritableDatabase().insertWithOnConflict(RestaurantEntry.TABLE_NAME,
                                                                                         null,
                                                                                         contentValues,
                                                                                         SQLiteDatabase.CONFLICT_REPLACE);
                return RestaurantContract.RestaurantEntry.buildRestaurantUri(_id);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    public void notifyChange(@NonNull Uri uri)
    {
        Context context = getContext();
        if (context != null)
        {
            context.getContentResolver().notifyChange(uri, null);
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values)
    {
        notifyChange(uri);

        SQLiteDatabase movieWritableDatabase = restaurantDbHelper.getWritableDatabase();
        try
        {
            return tryStartingTransaction(uri, values, movieWritableDatabase);
        } finally
        {
            movieWritableDatabase.endTransaction();
        }
    }

    private int tryStartingTransaction(Uri uri, ContentValues[] values, SQLiteDatabase movieWritableDatabase)
    {
        movieWritableDatabase.beginTransaction();
        int rowsInserted = 0;
        for (ContentValues value : values)
        {
            long id = movieWritableDatabase
                    .insertWithOnConflict(getTableName(uri), null, value, SQLiteDatabase.CONFLICT_REPLACE);
            if (id != -1)
            {
                rowsInserted++;
            }
        }
        movieWritableDatabase.setTransactionSuccessful();
        return rowsInserted;
    }

    public String getTableName(@NonNull Uri uri)
    {
        switch (uriMatcher.match(uri))
        {
            case RESTAURANT:
                return RestaurantContract.RestaurantEntry.TABLE_NAME;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs)
    {
        notifyChange(uri);
        switch (uriMatcher.match(uri))
        {
            case RESTAURANT:
                return restaurantDbHelper.getWritableDatabase().delete(getTableName(uri),
                                                                       selection,
                                                                       selectionArgs);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        notifyChange(uri);
        // For getting updated rows count
        selection = selection == null ? "1" : selection;
        return restaurantDbHelper.getWritableDatabase().update(getTableName(uri), values, selection, selectionArgs);
    }
}
