package com.avielniego.openhvr.ui.restaurantListFragment;

import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.avielniego.openhvr.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RestaurantListFragment extends Fragment
{
    private RestaurantListAdapter adapter = new RestaurantListAdapter();

    private List<String> restaurantTypes = new ArrayList<>();
    private Set<String>  selectedTypes   = new HashSet<>();

    public RestaurantListFragment()
    {
    }

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
        RestaurantLoader restaurantLoader = new RestaurantLoader(getContext(), new RestaurantLoader.RestaurantLoadListener()
        {
            @Override
            public void onRestaurantLoaded(List<RestaurantContent> restaurants)
            {
                RestaurantListFragment.this.onRestaurantLoaded(restaurants);
            }
        });
        getLoaderManager().initLoader(RestaurantLoader.RESTAURANT_LOADER_ID, null, restaurantLoader);
    }

    private void onRestaurantLoaded(List<RestaurantContent> restaurants)
    {
        adapter.setRestaurants(restaurants);
        setRestaurantTypes(restaurants);
    }

    private void setRestaurantTypes(List<RestaurantContent> restaurants)
    {
        restaurantTypes.clear();
        Set<String> types = new HashSet<>();
        for (RestaurantContent restaurant : restaurants)
        {
            types.addAll(Arrays.asList(restaurant.type.split(",")));
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_filter:
                openFilterDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openFilterDialog()
    {
        new AlertDialog.Builder(getContext()).setMultiChoiceItems(restaurantTypes.toArray(new String[restaurantTypes.size()]),
                                                                  getCurrentSelectedTypes(),
                                                                  new DialogInterface.OnMultiChoiceClickListener()
                                                                  {
                                                                      @Override
                                                                      public void onClick(DialogInterface dialogInterface,
                                                                                          int i, boolean b)
                                                                      {
                                                                          onTypeSelected(i, b);
                                                                      }
                                                                  })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        filterRestaurantByType();
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
            }
        }).create().show();
    }

    private void filterRestaurantByType()
    {
        adapter.setSelectedTypes(selectedTypes);
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

    private boolean[] getCurrentSelectedTypes()
    {
        boolean[] selectedTypes = new boolean[restaurantTypes.size()];
        for (int i = 0; i < restaurantTypes.size(); i++)
        {
            selectedTypes[i] = this.selectedTypes.contains(restaurantTypes.get(i));
        }
        return selectedTypes;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);
        initRecyclerView(view);
        return view;
    }

    private void initRecyclerView(View view)
    {
        RecyclerView recyclerView = ((RecyclerView) view.findViewById(R.id.list));
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(adapter);
    }

    public void setLocation(Location location)
    {
        adapter.setLocation(location);
    }
}
