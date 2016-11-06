package com.avielniego.openhvr.ui.restaurantMapFragment.googleMapsClusterMarker;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.avielniego.openhvr.R;
import com.avielniego.openhvr.entities.RestaurantContent;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class RestaurantClusterRenderer extends DefaultClusterRenderer<RestaurantsClusterMarker> {
    private Float hueColor;

    public RestaurantClusterRenderer(Context context, GoogleMap map, ClusterManager<RestaurantsClusterMarker> clusterManager) {
        super(context, map, clusterManager);
        hueColor = getHueMarkerColor(context);
    }

    @Override
    protected void onBeforeClusterItemRendered(RestaurantsClusterMarker item, MarkerOptions markerOptions) {
        RestaurantContent restaurant = item.getRestaurant();
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(hueColor))
                .title(restaurant.name)
                .snippet(restaurant.type);
    }

    @Override
    protected void onClusterItemRendered(RestaurantsClusterMarker clusterItem, Marker marker) {
        marker.setTag(clusterItem.getRestaurant());
        super.onClusterItemRendered(clusterItem, marker);
    }

    private float getHueMarkerColor(Context context) {
        float[] hsv = new float[3];
        Color.colorToHSV(ContextCompat.getColor(context, R.color.colorAccent), hsv);
        return hsv[0];
    }

}
