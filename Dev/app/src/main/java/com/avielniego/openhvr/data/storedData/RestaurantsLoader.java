package com.avielniego.openhvr.data.storedData;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.avielniego.openhvr.data.loadData.RestaurantCursorParser;
import com.avielniego.openhvr.data.loadData.RestaurantDataDownloadService;
import com.avielniego.openhvr.entities.RestaurantContent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RestaurantsLoader implements LoaderManager.LoaderCallbacks<Cursor>
{
    public static final int RESTAURANT_LOADER_ID = 0;

    private Context       context;
    private RestaurantLoadListener restaurantLoadListener;
    private boolean isFirstLoadTry = true;
    private String selection;
    private String[] selectionArgs;

    public RestaurantsLoader(Context context, RestaurantLoadListener restaurantLoadListener)
    {
        this.context = context;
        this.restaurantLoadListener = restaurantLoadListener;
    }

    public void loadSpecificRestaurants(@Nullable List<Integer> restaurantsIds)
    {
        if (restaurantsIds == null) {
            return;
        }
        selection = RestaurantContract.RestaurantEntry._ID + " in (" + makePlaceHolders(restaurantsIds.size()) + ")";
        selectionArgs = toStringArray(restaurantsIds);
    }

    private String makePlaceHolders(int size) {
        StringBuilder placeHolders = new StringBuilder();
        for (int i = 0; i < size - 1; i++) {
            placeHolders.append("? ,");
        }
        placeHolders.append("?");
        return placeHolders.toString();
    }

    private String[] toStringArray(List<Integer> restaurantsIds) {
        String[] ids = new String[restaurantsIds.size()];
        for (int i = 0; i < restaurantsIds.size(); i++) {
            ids[i] = String.valueOf(restaurantsIds.get(i));
        }
        return ids;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        switch (id)
        {
            case RESTAURANT_LOADER_ID:
                return new CursorLoader(context, RestaurantContract.RestaurantEntry.CONTENT_URI, RestaurantCursorParser.PROJECTION, selection, selectionArgs, null);
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
        if(data.getCount() == 0 && isFirstLoadTry) {
            context.startService(new Intent(context, RestaurantDataDownloadService.class));
            isFirstLoadTry = false;
        }
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
