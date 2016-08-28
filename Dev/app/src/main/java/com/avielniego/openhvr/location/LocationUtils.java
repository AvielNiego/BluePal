package com.avielniego.openhvr.location;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;

import java.util.Locale;

public class LocationUtils
{
    public static void startNavigationActivity(Context context, String placeLabel, double lat, double lon)
    {
        String uri = "geo:0,0?q=" + android.net.Uri
                .encode(String.format(Locale.ENGLISH, "%s@%f,%f", placeLabel, lat, lon), "UTF-8");
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
    }

    public static void startNavigationActivity(Context context, String placeLabel, Location location)
    {
        startNavigationActivity(context, placeLabel, location.getLatitude(), location.getLongitude());
    }
}
