package com.avielniego.openhvr.ui.main;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.avielniego.openhvr.R;
import com.avielniego.openhvr.alerts.NewRestaurantAlert;
import com.avielniego.openhvr.data.sync.OpenHvrSyncAdapter;
import com.avielniego.openhvr.location.LocationPermissionVerifier;
import com.avielniego.openhvr.ui.restaurantListFragment.RestaurantListFragment;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
{
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private RestaurantListFragment restaurantListFragment;
    private MainPagerAdapter mainPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        OpenHvrSyncAdapter.initializeSyncAdapter(this);
        setViewPager();
        getLocation();
    }

    private void setViewPager()
    {
        ViewPager viewPager = (ViewPager) findViewById(R.id.main_pager);
        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(mainPagerAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.mainTabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void getLocation()
    {
        LocationPermissionVerifier locationPermissionVerifier = new LocationPermissionVerifier(this);
        if (locationPermissionVerifier.hasLocationPermission())
        {
            requestLocation();
        }
        else
        {
            locationPermissionVerifier.askForPermission();
        }
    }

    private void requestLocation()
    {
        if (ActivityCompat.checkSelfPermission(this,
                                               Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        LocationListener locationListener = new LocationListener()
        {
            public void onLocationChanged(Location location)
            {
                onLocationReceived(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras)
            {
            }

            public void onProviderEnabled(String provider)
            {
            }

            public void onProviderDisabled(String provider)
            {
            }
        };

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        onLocationReceived(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
    }

    private void onLocationReceived(Location location)
    {
        mainPagerAdapter.onLocationReceived(location);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (!isGotLocationPermission(requestCode, grantResults))
            return;
        requestLocation();
    }

    private boolean isGotLocationPermission(int requestCode, @NonNull int[] grantResults)
    {
        return requestCode == LocationPermissionVerifier.LOCATION_PERMISSION_REQUEST_CODE &&
                (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        initNotifyNewRestaurantMenuItem(menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initNotifyNewRestaurantMenuItem(Menu menu) {
        MenuItem actionView = menu.findItem(R.id.action_notify_new_restaurants);
        actionView.setChecked(NewRestaurantAlert.isNotificationsAllowed(this));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_notify_new_restaurants:
                onNotifyNewRestaurantsMenuCheck(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onNotifyNewRestaurantsMenuCheck(MenuItem item) {
        item.setChecked(!item.isChecked());
        if (item.isChecked())
            NewRestaurantAlert.allowNotifications(this);
        else
            NewRestaurantAlert.forbidNotifications(this);
    }
}
