package com.avielniego.openhvr.ui.restaurantDetails;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.avielniego.openhvr.R;

public class RestaurantsDetailsActivity extends AppCompatActivity
{
    public static final String LONG_ARG = "LONG_ARG";
    public static final String LAT_ARG = "LAT_ARG";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);
        addFragment();
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
}
