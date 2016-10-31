package com.avielniego.openhvr.ui.restaurantDetails;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.avielniego.openhvr.R;
import com.avielniego.openhvr.data.storedData.RestaurantContract;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import static com.avielniego.openhvr.ui.main.MainActivity.AD_MOD_APP_ID;

public class RestaurantsDetailsActivity extends AppCompatActivity
{
    public static final String LONG_ARG = "LONG_ARG";
    public static final String LAT_ARG = "LAT_ARG";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);
        setToolbar();
        addAds();
        addFragment();
    }

    private void addAds() {
        MobileAds.initialize(getApplicationContext(), AD_MOD_APP_ID);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void setToolbar()
    {
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void addFragment()
    {

        Uri restaurantUri = getIntent().getData();
        Location l = getLocation();
        getSupportFragmentManager().beginTransaction().add(R.id.restaurant_details_fragment_container,
                                                           RestaurantDetailsFragment.newInstance(restaurantUri, l))
                .commit();
    }

    private Location getLocation()
    {
        Location location = new Location("");
        location.setLongitude(getIntent().getDoubleExtra(LONG_ARG, 0));
        location.setLatitude(getIntent().getDoubleExtra(LAT_ARG, 0));
        return location;
    }

    public static Intent getIntent(Context context, @Nullable Location location, long restaurantId)
    {
        Intent intent = new Intent(context, RestaurantsDetailsActivity.class)
                .setData(RestaurantContract.RestaurantEntry.buildRestaurantUri(restaurantId));
        if (location == null)
            return intent;
        return intent.putExtra(RestaurantsDetailsActivity.LAT_ARG, location.getLatitude())
            .putExtra(RestaurantsDetailsActivity.LONG_ARG, location.getLongitude());
    }
}
