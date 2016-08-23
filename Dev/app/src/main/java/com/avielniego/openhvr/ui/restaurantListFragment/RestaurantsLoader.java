package com.avielniego.openhvr.ui.restaurantListFragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.avielniego.openhvr.data.storedData.RestaurantContract;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RestaurantsLoader implements LoaderManager.LoaderCallbacks<Cursor>
{
    public static final int RESTAURANT_LOADER_ID = 0;

    private static final String[] PROJECTION = new String[]{
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry._ID,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_IMAGE,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_NAME,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_DESC,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_AREA,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_CITY,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_ADDRESS,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_PHONE,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_CATEGORY,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_TYPE,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_WEEK_OPEN_HOURS,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_FRIDAY_OPEN_HOURS,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_SAT_OPEN_HOURS,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_IS_KOSHER,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_HANDICAP,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_WEBSITE,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_LATITUDE,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_LONGITUDE};

    private static final int RESTAURANT_ID_COLUMN_INDEX          = 0;
    private static final int COLUMN_RESTAURANT_IMAGE             = 1;
    private static final int RESTAURANT_NAME_COLUMN_INDEX        = 2;
    private static final int COLUMN_RESTAURANT_DESC              = 3;
    private static final int COLUMN_RESTAURANT_AREA              = 4;
    private static final int COLUMN_RESTAURANT_CITY              = 5;
    private static final int COLUMN_RESTAURANT_ADDRESS           = 6;
    private static final int COLUMN_RESTAURANT_PHONE             = 7;
    private static final int COLUMN_RESTAURANT_CATEGORY          = 8;
    private static final int COLUMN_RESTAURANT_TYPE              = 9;
    private static final int COLUMN_RESTAURANT_WEEK_OPEN_HOURS   = 10;
    private static final int COLUMN_RESTAURANT_FRIDAY_OPEN_HOURS = 11;
    private static final int COLUMN_RESTAURANT_SAT_OPEN_HOURS    = 12;
    private static final int COLUMN_RESTAURANT_IS_KOSHER         = 13;
    private static final int COLUMN_RESTAURANT_HANDICAP          = 14;
    private static final int COLUMN_RESTAURANT_WEBSITE           = 15;
    private static final int COLUMN_RESTAURANT_LATITUDE          = 16;
    private static final int COLUMN_RESTAURANT_LONGITUDE         = 17;

    private Context       context;
    private RestaurantLoadListener restaurantLoadListener;

    public RestaurantsLoader(Context context, RestaurantLoadListener restaurantLoadListener)
    {
        this.context = context;
        this.restaurantLoadListener = restaurantLoadListener;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        switch (id)
        {
            case RESTAURANT_LOADER_ID:
                return new CursorLoader(context, RestaurantContract.RestaurantEntry.CONTENT_URI, PROJECTION, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        switch (loader.getId())
        {
            case RESTAURANT_LOADER_ID:
                onRestaurantLoadFinished(data);
                break;
        }
    }

    private void onRestaurantLoadFinished(Cursor data)
    {
        restaurantLoadListener.onRestaurantLoaded(clearDuplicates(cursorDataToRestaurantsContent(data)));
    }

    private List<RestaurantContent> clearDuplicates(List<RestaurantContent> restaurantContents)
    {
        return new ArrayList<>(new HashSet<>(restaurantContents));
    }

    private List<RestaurantContent> cursorDataToRestaurantsContent(Cursor data)
    {
        List<RestaurantContent> restaurantsContent = new ArrayList<>();
        data.moveToFirst();
        while (!data.isAfterLast())
        {
            restaurantsContent.add(cursorDataToRestaurantContent(data));
            data.moveToNext();
        }
        return restaurantsContent;
    }

    private RestaurantContent cursorDataToRestaurantContent(Cursor data)
    {
        RestaurantContent content = new RestaurantContent();
        content.id = data.getLong(RESTAURANT_ID_COLUMN_INDEX);
        content.image = data.getString(COLUMN_RESTAURANT_IMAGE);
        content.name = data.getString(RESTAURANT_NAME_COLUMN_INDEX);
        content.desc = data.getString(COLUMN_RESTAURANT_DESC);
        content.area = data.getString(COLUMN_RESTAURANT_AREA);
        content.city = data.getString(COLUMN_RESTAURANT_CITY);
        content.address = data.getString(COLUMN_RESTAURANT_ADDRESS);
        content.phone = data.getString(COLUMN_RESTAURANT_PHONE);
        content.category = data.getString(COLUMN_RESTAURANT_CATEGORY);
        content.type = data.getString(COLUMN_RESTAURANT_TYPE);
        content.weekOpenHours = data.getString(COLUMN_RESTAURANT_WEEK_OPEN_HOURS);
        content.fridayOpenHours = data.getString(COLUMN_RESTAURANT_FRIDAY_OPEN_HOURS);
        content.satOpenHours = data.getString(COLUMN_RESTAURANT_SAT_OPEN_HOURS);
        content.kosher = data.getString(COLUMN_RESTAURANT_IS_KOSHER);
        content.handicap = data.getString(COLUMN_RESTAURANT_HANDICAP);
        content.website = data.getString(COLUMN_RESTAURANT_WEBSITE);
        content.latitude = data.getDouble(COLUMN_RESTAURANT_LATITUDE);
        content.longitude = data.getDouble(COLUMN_RESTAURANT_LONGITUDE);
        return content;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        restaurantLoadListener.onRestaurantLoaded(new ArrayList<RestaurantContent>());
    }

    public interface RestaurantLoadListener
    {
        void onRestaurantLoaded(List<RestaurantContent> restaurants);
    }
}
