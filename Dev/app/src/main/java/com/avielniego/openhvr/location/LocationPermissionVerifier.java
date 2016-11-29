package com.avielniego.openhvr.location;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.avielniego.openhvr.R;

public class LocationPermissionVerifier
{
    private static final String LOG_TAG = LocationPermissionVerifier.class.getSimpleName();

    private static final String SHARED_PREF_LOCATION_ASKS_COUNTER    = LOG_TAG.concat("SHARED_PREF_LOCATION_ASKS_COUNTER");
    private static final String SHARED_PREF_LAST_TIME_ASKED_LOCATION = LOG_TAG
            .concat("SHARED_PREF_LAST_TIME_ASKED_LOCATION");

    private static final  long MINUET                           = 1000 * 60;
    private static final long DAY                              = MINUET * 60 * 24;
    private static final long WEEK                             = DAY * 7;
    private static final long MONTH                            = WEEK * 4;
    public static final  int  LOCATION_PERMISSION_REQUEST_CODE = 0;

    private Activity activity;


    public LocationPermissionVerifier(Activity activity)
    {
        this.activity = activity;
    }

    public boolean hasLocationPermission()
    {
        return ActivityCompat.checkSelfPermission(activity,
                                                  Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat
                .checkSelfPermission(activity,
                                     Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void askForPermission()
    {
        if (!shouldAskForPermissions())
            return;

        incrementTimesAskedForPermission();
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION))
        {
            showLocationPermissionRationale();
        }
        else
        {
            requestPermission();
        }
    }

    private void showLocationPermissionRationale()
    {
        new AlertDialog.Builder(activity).setTitle(R.string.location_permission_title)
                .setMessage(R.string.location_permission_rational)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        requestPermission();
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
            }
        }).setIcon(android.R.drawable.ic_dialog_info).show();
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(activity,
                                          new String[]{
                                                  Manifest.permission.ACCESS_COARSE_LOCATION,
                                                  Manifest.permission.ACCESS_FINE_LOCATION},
                                          LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void incrementTimesAskedForPermission()
    {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(activity).edit();
        editor.putLong(SHARED_PREF_LOCATION_ASKS_COUNTER, getTimesAskedForPermission() + 1);
        editor.putLong(SHARED_PREF_LAST_TIME_ASKED_LOCATION, System.currentTimeMillis());
        editor.apply();
    }

    private boolean shouldAskForPermissions()
    {
        long timesAskedForPermission = getTimesAskedForPermission();
        long timedPassedSinceLastPermissionAsk = getTimedPassedSinceLastPermissionAsk();

        if (timesAskedForPermission == 0)
            return true;
        else if (timesAskedForPermission == 1 && timedPassedSinceLastPermissionAsk < DAY)
            return true;
        else if (timesAskedForPermission == 2 && timedPassedSinceLastPermissionAsk < WEEK)
            return true;
        else if (timesAskedForPermission > 2 && timedPassedSinceLastPermissionAsk < MONTH)
            return true;
        return false;
    }

    private long getTimedPassedSinceLastPermissionAsk()
    {
        return PreferenceManager.getDefaultSharedPreferences(activity).getLong(SHARED_PREF_LAST_TIME_ASKED_LOCATION, 0);
    }

    private long getTimesAskedForPermission()
    {
        return PreferenceManager.getDefaultSharedPreferences(activity).getLong(SHARED_PREF_LOCATION_ASKS_COUNTER, 0);
    }
}
