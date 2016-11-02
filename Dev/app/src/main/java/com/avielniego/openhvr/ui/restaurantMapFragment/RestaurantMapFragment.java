package com.avielniego.openhvr.ui.restaurantMapFragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avielniego.openhvr.R;
import com.avielniego.openhvr.entities.RestaurantContent;
import com.avielniego.openhvr.data.storedData.RestaurantsLoader;
import com.avielniego.openhvr.ui.restaurantDetails.RestaurantsDetailsActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class RestaurantMapFragment extends Fragment {
    public static final LatLng ISRAEL_LAT_LNG = new LatLng(32.004436, 34.787704);
    private Float hueColor;

    public RestaurantMapFragment() {
    }

    public static RestaurantMapFragment newInstance() {
        RestaurantMapFragment fragment = new RestaurantMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable private Location location;
    private MapView mMapView;
    private GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_restaurant_map, container, false);
        createMap(savedInstanceState, rootView);

        return rootView;
    }

    private void createMap(Bundle savedInstanceState, View rootView) {
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately
        initMap();

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                moveCamera();
                launchRestaurantLoader();
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        onInfoWindowClicked(marker);
                    }
                });
            }
        });
    }

    private void onInfoWindowClicked(Marker marker) {
        RestaurantContent restaurant = (RestaurantContent) marker.getTag();
        getContext().startActivity(RestaurantsDetailsActivity.getIntent(getContext(), location, restaurant.id));
    }

    private void moveCamera() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            moveCameraToCurrentLocation();
        }
        else
        {
            movieCameraToDefaultLocation();
        }
    }

    private void movieCameraToDefaultLocation() {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(ISRAEL_LAT_LNG).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @SuppressWarnings("MissingPermission")
    private void moveCameraToCurrentLocation() {
        googleMap.setMyLocationEnabled(true);

        if (location == null)
        {
            movieCameraToDefaultLocation();
        }
        else {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void initMap() {
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void onLocationReceived(Location location) {
        this.location = location;
    }

    private void launchRestaurantLoader() {
        RestaurantsLoader restaurantsLoader = new RestaurantsLoader(getContext(), new RestaurantsLoader.RestaurantLoadListener() {
            @Override
            public void onRestaurantLoaded(List<RestaurantContent> restaurants) {
                RestaurantMapFragment.this.onRestaurantLoaded(restaurants);
            }
        });
        getLoaderManager().initLoader(RestaurantsLoader.RESTAURANT_LOADER_ID, null, restaurantsLoader);
    }

    private void onRestaurantLoaded(List<RestaurantContent> restaurants) {
        for (RestaurantContent restaurant : restaurants) {
            addMarkerToMap(restaurant);
        }
    }

    private void addMarkerToMap(RestaurantContent restaurant) {
        LatLng position = new LatLng(restaurant.getLocation().getLatitude(), restaurant.getLocation().getLongitude());
        googleMap.addMarker(new MarkerOptions().position(position)
                .title(restaurant.name)
                .snippet(restaurant.type)
                .icon(BitmapDescriptorFactory.defaultMarker(getHueMarkerColorLazy())))
                .setTag(restaurant);
    }

    private float getHueMarkerColorLazy() {
        if (hueColor == null) {
            hueColor = getHueMarkerColor();
        }
        return hueColor;
    }

    private float getHueMarkerColor() {
        float[] hsv = new float[3];
        Color.colorToHSV(ContextCompat.getColor(getContext(), R.color.colorAccent), hsv);
        return hsv[0];
    }
}
