package com.avielniego.openhvr.ui.main;

import android.app.Activity;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.avielniego.openhvr.R;
import com.avielniego.openhvr.ui.restaurantListFragment.RestaurantListFragment;
import com.avielniego.openhvr.ui.restaurantMapFragment.RestaurantMapFragment;

public class MainPagerAdapter extends FragmentPagerAdapter
{
    public static final int TABS_COUNT = 2;
    public static final int RESTAURANT_LIST_TAB = 0;
    private static final int RESTAURANT_MAP_TAB = 1;

    private final Activity activity;
    private RestaurantListFragment restaurantListFragment = RestaurantListFragment.newInstance();
    private RestaurantMapFragment restaurantMapFragment = RestaurantMapFragment.newInstance();

    public MainPagerAdapter(FragmentManager fm, Activity activity) {
        super(fm);
        this.activity = activity;
    }



    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case RESTAURANT_LIST_TAB:
                return restaurantListFragment;
            case RESTAURANT_MAP_TAB:
                return restaurantMapFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount()
    {
        return TABS_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case RESTAURANT_LIST_TAB:
                return activity.getString(R.string.list_tab_name);
            case RESTAURANT_MAP_TAB:
                return activity.getString(R.string.map_tab_name);
            default:
                return null;
        }
    }

    public void onLocationReceived(Location location)
    {
        restaurantListFragment.onLocationReceived(location);
        restaurantMapFragment.onLocationReceived(location);
    }

    public String getItemName(int position) {
        return getItem(position).getClass().getSimpleName();
    }
}
