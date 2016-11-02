package com.avielniego.openhvr.ui.restaurantListFragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avielniego.openhvr.R;
import com.avielniego.openhvr.entities.RestaurantContent;
import com.avielniego.openhvr.data.storedData.RestaurantsLoader;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RestaurantListFragment extends Fragment
{
    private static final String LOG_TAG = RestaurantListFragment.class.getSimpleName();

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 0;

    private RestaurantListAdapter adapter = new RestaurantListAdapter();

    private List<String> restaurantTypes = new ArrayList<>();
    private Set<String>  selectedTypes   = new HashSet<>();
    private Location currentLocation;
    private TextView locationFilterView;
    @Nullable private Place chosenPlace;
    private View cancelLocationFilter;

    public static RestaurantListFragment newInstance()
    {
        RestaurantListFragment fragment = new RestaurantListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        launchRestaurantLoader();
        adapter.setContext(getContext());
    }

    private void launchRestaurantLoader()
    {
        RestaurantsLoader restaurantsLoader = new RestaurantsLoader(getContext(), new RestaurantsLoader.RestaurantLoadListener()
        {
            @Override
            public void onRestaurantLoaded(List<RestaurantContent> restaurants)
            {
                RestaurantListFragment.this.onRestaurantLoaded(restaurants);
            }
        });
        getLoaderManager().initLoader(RestaurantsLoader.RESTAURANT_LOADER_ID, null, restaurantsLoader);
    }

    private void onRestaurantLoaded(List<RestaurantContent> restaurants)
    {
        adapter.setRestaurants(restaurants);
        setRestaurantTypes(restaurants);
    }

    private void setRestaurantTypes(List<RestaurantContent> restaurants)
    {
        Set<String> types = new HashSet<>();
        for (RestaurantContent restaurant : restaurants)
        {
            types.addAll(restaurant.getTypes());
        }
        restaurantTypes = new ArrayList<>(types);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.restaurant_list_menu, menu);
        initSearchView(menu);
    }

    private void initSearchView(Menu menu)
    {
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText)
            {
                adapter.setNameSearch(newText);
                return false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);
        initRecyclerView(view);
        initCategoryFilter(view);
        initLocationFilter(view);
        initCancelCustomLocationFilter(view);
        return view;
    }

    private void initCancelCustomLocationFilter(View view)
    {
        cancelLocationFilter = view.findViewById(R.id.cancel_location_filter);
        cancelLocationFilter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onCancelCustomLocationFilter();
            }
        });
    }

    private void onCancelCustomLocationFilter()
    {
        chosenPlace = null;
        onLocationReceived(currentLocation);
    }

    private void initLocationFilter(View view)
    {

        locationFilterView = ((TextView) view.findViewById(R.id.location_filter));
        enableLocationFilter();
    }

    private void enableLocationFilter()
    {
        locationFilterView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                tryLaunchingPlaceAutocomplete();
            }
        });
    }

    private void disableLocationFilter()
    {
        locationFilterView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
            }
        });
    }

    private void tryLaunchingPlaceAutocomplete()
    {
        try
        {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(getActivity());
            disableLocationFilter();
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e)
        {
            // TODO: Handle the error.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE)
        {
            onPlaceAutocompleteReturned(resultCode, data);
        }
    }

    private void onPlaceAutocompleteReturned(int resultCode, Intent data)
    {
        enableLocationFilter();
        if (resultCode == Activity.RESULT_OK)
        {
            onPlaceAutocompleteResultOk(data);
        }
    }

    private void onPlaceAutocompleteResultOk(Intent data)
    {
        chosenPlace = PlaceAutocomplete.getPlace(getContext(), data);
        adapter.setLocation(chosenPlace.getLatLng().latitude, chosenPlace.getLatLng().longitude);
        locationFilterView.setText(chosenPlace.getName());
        cancelLocationFilter.setVisibility(View.VISIBLE);
    }

    private void initRecyclerView(View view)
    {
        RecyclerView recyclerView = ((RecyclerView) view.findViewById(R.id.list));
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void initCategoryFilter(View view)
    {
        view.findViewById(R.id.category_filter_combo_box).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                openFilterTypesDialog();
            }
        });
    }

    private void openFilterTypesDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(restaurantTypes.toArray(new String[restaurantTypes.size()]),
                                    getCurrentSelectedTypes(),
                                    new DialogInterface.OnMultiChoiceClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i, boolean b)
                                        {
                                            onTypeSelected(i, b);
                                        }
                                    }).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                filterRestaurantByType();
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialogInterface)
            {
                filterRestaurantByType();
            }
        }).create().show();
    }

    private boolean[] getCurrentSelectedTypes()
    {
        boolean[] selectedTypes = new boolean[restaurantTypes.size()];
        for (int i = 0; i < restaurantTypes.size(); i++)
        {
            selectedTypes[i] = this.selectedTypes.contains(restaurantTypes.get(i));
        }
        return selectedTypes;
    }

    private void onTypeSelected(int typeIndex, boolean isSelected)
    {
        if (isSelected)
        {
            selectedTypes.add(restaurantTypes.get(typeIndex));
        }
        else
        {
            selectedTypes.remove(restaurantTypes.get(typeIndex));
        }
    }

    private void filterRestaurantByType()
    {
        adapter.setSelectedTypes(selectedTypes);
    }

    public void onLocationReceived(Location location)
    {
        this.currentLocation = location;

        if (chosenPlace != null)
            return;
        adapter.setLocation(location);
        if (locationFilterView != null)
            locationFilterView.setText(R.string.current_location);
        if (cancelLocationFilter != null)
            cancelLocationFilter.setVisibility(View.GONE);
    }
}
