package com.avielniego.openhvr.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

public class LocationAvailableReceiver extends BroadcastReceiver {

    public static final String GPS_AVAILABLE = "GPS_AVAILABLE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isLocationAvailabilityChanged(intent) && isLocationAvailable(context)) {
            context.sendBroadcast(new Intent(GPS_AVAILABLE));
        }
    }

    protected boolean isLocationAvailabilityChanged(Intent intent) {
        return intent.getAction().matches("android.location.PROVIDERS_CHANGED");
    }

    private boolean isLocationAvailable(Context context){
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return isGpsEnabled(locationManager) || isNetworkEnabled(locationManager);
    }

    private boolean isNetworkEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isGpsEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}