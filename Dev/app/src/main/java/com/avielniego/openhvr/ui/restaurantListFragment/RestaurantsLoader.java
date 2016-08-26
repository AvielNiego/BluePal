package com.avielniego.openhvr.ui.restaurantListFragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.avielniego.openhvr.data.loadData.RestaurantCursorParser;
import com.avielniego.openhvr.data.storedData.RestaurantContract;
import com.avielniego.openhvr.entities.RestaurantContent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RestaurantsLoader implements LoaderManager.LoaderCallbacks<Cursor>
{
    public static final int RESTAURANT_LOADER_ID = 0;

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
                return new CursorLoader(context, RestaurantContract.RestaurantEntry.CONTENT_URI, RestaurantCursorParser.PROJECTION, null, null, null);
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
        restaurantLoadListener.onRestaurantLoaded(clearDuplicates(new RestaurantCursorParser().parseMany(data)));
    }

    private List<RestaurantContent> clearDuplicates(List<RestaurantContent> restaurantContents)
    {
        return new ArrayList<>(new HashSet<>(restaurantContents));
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
