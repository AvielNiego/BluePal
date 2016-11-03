package com.avielniego.openhvr.ui.restaurantMapFragment.googleMapsClusterMarker;

import com.avielniego.openhvr.entities.RestaurantContent;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class RestaurantsClusterMarker implements ClusterItem {
    private final LatLng mPosition;
    private RestaurantContent restaurant;

    public RestaurantsClusterMarker(LatLng position, RestaurantContent restaurant) {
        mPosition = position;
        this.restaurant = restaurant;
    }

    public RestaurantContent getRestaurant() {
        return restaurant;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
