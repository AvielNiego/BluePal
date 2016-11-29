package com.avielniego.openhvr.ui.restaurantMapFragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avielniego.openhvr.R;
import com.avielniego.openhvr.data.storedData.RestaurantsLoader;
import com.avielniego.openhvr.entities.RestaurantContent;
import com.avielniego.openhvr.ui.analytics.AnalyticsApplication;
import com.avielniego.openhvr.ui.analytics.AnalyticsLogger;
import com.avielniego.openhvr.ui.restaurantDetails.RestaurantsDetailsActivity;
import com.avielniego.openhvr.ui.restaurantMapFragment.googleMapsClusterMarker.RestaurantClusterRenderer;
import com.avielniego.openhvr.ui.restaurantMapFragment.googleMapsClusterMarker.RestaurantsClusterMarker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

public class RestaurantMapFragment extends Fragment {
    public static final LatLng ISRAEL_LAT_LNG = new LatLng(32.004436, 34.787704);

    public static RestaurantMapFragment newInstance() {
        RestaurantMapFragment fragment = new RestaurantMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private ClusterManager<RestaurantsClusterMarker> clusterManager;
    @Nullable
    private Location location;
    private MapView mapView;
    private GoogleMap googleMap;
    private AnalyticsLogger logger;

    public RestaurantMapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_restaurant_map, container, false);
        createMap(savedInstanceState, rootView);
        logger = new AnalyticsLogger((AnalyticsApplication) getActivity().getApplication());
        return rootView;
    }

    private void createMap(Bundle savedInstanceState, View rootView) {
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume(); // needed to get the map to display immediately
        initMap();

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                RestaurantMapFragment.this.onMapReady(mMap);
            }
        });
    }

    private void initMap() {
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onMapReady(GoogleMap mMap) {
        googleMap = mMap;
        initClusterMarkerManager();
        launchRestaurantLoader();
        moveCamera();
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                onInfoWindowClicked(marker);
            }
        });
    }

    private void initClusterMarkerManager() {
        clusterManager = new ClusterManager<>(getContext(), googleMap);
        clusterManager.setRenderer(new RestaurantClusterRenderer(getContext(), googleMap, clusterManager));
        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);
    }

    private void onInfoWindowClicked(Marker marker) {
        RestaurantContent restaurant = (RestaurantContent) marker.getTag();
        logger.logAction("MarkerInfoWindowClick");
        getContext().startActivity(RestaurantsDetailsActivity.getIntent(getContext(), location, restaurant.id));
    }

    private void moveCamera() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            moveCameraToCurrentLocation();
        } else {
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

        if (location == null) {
            movieCameraToDefaultLocation();
        } else {
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
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
        clusterManager.clearItems();
        for (RestaurantContent restaurant : restaurants) {
            addMarkerToMap(restaurant);
        }
    }

    private void addMarkerToMap(RestaurantContent restaurant) {
        LatLng position = new LatLng(restaurant.getLocation().getLatitude(), restaurant.getLocation().getLongitude());
        RestaurantsClusterMarker offsetItem = new RestaurantsClusterMarker(position, restaurant);
        clusterManager.addItem(offsetItem);
    }
}