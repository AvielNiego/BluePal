package com.avielniego.openhvr.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.avielniego.openhvr.R;
import com.avielniego.openhvr.data.sync.OpenHvrSyncAdapter;
import com.avielniego.openhvr.location.LocationPermissionVerifier;
import com.avielniego.openhvr.ui.restaurantListFragment.RestaurantListFragment;

public class MainActivity extends AppCompatActivity
{
    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private RestaurantListFragment restaurantListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OpenHvrSyncAdapter.initializeSyncAdapter(this);
        addFragment();
        getLocation();
    }

    private void addFragment()
    {
        restaurantListFragment = RestaurantListFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.restaurant_list_fragment_container, restaurantListFragment).commit();
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
        restaurantListFragment.onLocationReceived(location);
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
        return requestCode == LocationPermissionVerifier.LOCATION_PERMISSION_REQUEST_CODE && (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
    }
}
