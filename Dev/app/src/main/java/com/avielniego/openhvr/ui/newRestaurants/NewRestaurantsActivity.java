package com.avielniego.openhvr.ui.newRestaurants;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.avielniego.openhvr.R;
import com.avielniego.openhvr.ui.analytics.AnalyticsApplication;
import com.avielniego.openhvr.ui.analytics.AnalyticsLogger;
import com.avielniego.openhvr.ui.restaurantDetails.RestaurantsDetailsActivity;
import com.avielniego.openhvr.ui.restaurantListFragment.RestaurantListFragment;

import java.util.ArrayList;

public class NewRestaurantsActivity extends AppCompatActivity {

    public static final String RESTAURANTS_IDS_ARG_KEY = "RESTAURANTS_IDS_ARG_KEY";
    private AnalyticsLogger logger;

    public static Intent newIntent(Context context, @Nullable ArrayList<Integer> newRestaurantsIds) {
        return new Intent(context, NewRestaurantsActivity.class)
                .putExtra(RESTAURANTS_IDS_ARG_KEY, newRestaurantsIds);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger = new AnalyticsLogger(((AnalyticsApplication) getApplication()));
        setContentView(R.layout.activity_new_restaurants);
        setToolbar();
        addFragment();
    }

    private void setToolbar() {
        if (getSupportActionBar() == null) {
            return;
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.new_restaurants);
    }

    private void addFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.new_restaurants_list_container,
                RestaurantListFragment.newInstance(getRestaurantsIds()))
                .commit();
    }

    private ArrayList<Integer> getRestaurantsIds() {
        return getIntent().getExtras().getIntegerArrayList(RESTAURANTS_IDS_ARG_KEY);
    }


    @Override
    protected void onResume() {
        super.onResume();
        logger.logScreen(RestaurantsDetailsActivity.class.getSimpleName());
    }
}
