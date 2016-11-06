package com.avielniego.openhvr.ui.restaurantListFragment;

import android.app.Activity;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avielniego.openhvr.R;
import com.avielniego.openhvr.entities.RestaurantContent;
import com.avielniego.openhvr.ui.GuiUtils;
import com.avielniego.openhvr.ui.analytics.AnalyticsApplication;
import com.avielniego.openhvr.ui.restaurantDetails.RestaurantsDetailsActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.ViewHolder>
{
    private List<RestaurantContent> filteredRestaurants = new ArrayList<>();
    private List<RestaurantContent> restaurants         = new ArrayList<>();
    @Nullable private Activity activity;
    @Nullable private Location     location;
    @Nullable private RecyclerView recyclerView;
    private Set<String> selectedTypes  = new HashSet<>();
    private String      nameSearchText = "";

    public RestaurantListAdapter()
    {
    }

    public void setRestaurants(List<RestaurantContent> restaurants)
    {
        this.restaurants = restaurants;
        updateRecyclerView();
    }

    public void setActivity(@Nullable Activity activity)
    {
        this.activity = activity;
    }

    private void updateRecyclerView()
    {
        if (recyclerView == null)
        {
            return;
        }

        filterRestaurants();
        sortRestaurants();
        recyclerView.invalidate();
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_restaurant_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        final RestaurantContent restaurant = filteredRestaurants.get(position);
        holder.item = restaurant;
        setName(holder, restaurant);
        holder.typeTextView.setText(restaurant.type);
        holder.addressTextView.setText(restaurant.city);
        setDistance(holder, restaurant);
        setIsOpenTextViewValue(holder, restaurant);
        Glide.with(activity).load(restaurant.image).placeholder(R.mipmap.ic_launcher).into(holder.restaurantImageView);

        holder.view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onRestaurantItemClicked(restaurant);
            }
        });
    }

    private void onRestaurantItemClicked(RestaurantContent restaurant)
    {
        if (activity == null)
            return;
        logListItemClicked();
        activity.startActivity(RestaurantsDetailsActivity.getIntent(activity, location, restaurant.id));
    }

    private void logListItemClicked() {
        if (activity == null) {
            return;
        }

        ((AnalyticsApplication) activity.getApplication()).getDefaultTracker().
                send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("RestaurantListItemClicked")
                .build());
    }

    private void setName(ViewHolder holder, RestaurantContent restaurant)
    {
        holder.nameTextView.setText(restaurant.name);
        if (activity != null)
        {
            GuiUtils.highlightTextInTextView(holder.nameTextView,
                                             nameSearchText,
                                             ContextCompat.getColor(activity, R.color.colorAccent));
        }
    }

    private void setDistance(ViewHolder holder, RestaurantContent restaurant)
    {
        if (location != null && activity != null)
        {
            String distance = String.valueOf(Math.floor(restaurant.getDistanceFrom(location) / 10) / 100);
            holder.distanceTextView.setText(activity.getString(R.string.distance, distance));
        }
    }

    private void setIsOpenTextViewValue(ViewHolder holder, RestaurantContent restaurant)
    {
        try
        {
            setIsOpenState(holder, restaurant);
        } catch (RestaurantContent.OpenHoursDoesNotPresented e)
        {
            holder.isOpenTextView.setText("");
        }
    }

    private void setIsOpenState(ViewHolder holder, RestaurantContent restaurant)
    {
        if (restaurant.isOpenNow())
            setOpenedStyle(holder);
        else
            setClosedStyle(holder);
    }

    private void setClosedStyle(ViewHolder holder)
    {
        holder.isOpenTextView.setText(R.string.closed);
        TextViewCompat.setTextAppearance(holder.isOpenTextView, R.style.closedTextStyle);

    }

    private void setOpenedStyle(ViewHolder holder)
    {
        holder.isOpenTextView.setText(R.string.open);
        TextViewCompat.setTextAppearance(holder.isOpenTextView, R.style.openTextStyle);
    }

    @Override
    public int getItemCount()
    {
        return filteredRestaurants.size();
    }

    public void setLocation(@Nullable Location location)
    {
        this.location = location;
        updateRecyclerView();
    }

    private void sortRestaurants()
    {
        if (location == null)
            return;
        Collections.sort(filteredRestaurants, new Comparator<RestaurantContent>()
        {
            @Override
            public int compare(RestaurantContent restaurantContent, RestaurantContent t1)
            {
                return Float.valueOf(restaurantContent.getDistanceFrom(location)).compareTo(t1.getDistanceFrom(location));
            }
        });
    }

    public void setSelectedTypes(final Set<String> selectedTypes)
    {
        this.selectedTypes = selectedTypes;
        updateRecyclerView();
    }

    private void filterRestaurants()
    {
        filteredRestaurants = getFilteredByTypeRestaurants(restaurants);
        filteredRestaurants = getFilteredByRestaurantName(filteredRestaurants);
    }

    private List<RestaurantContent> getFilteredByRestaurantName(List<RestaurantContent> restaurants)
    {
        if (nameSearchText.isEmpty())
            return restaurants;
        return new ArrayList<>(Collections2.filter(restaurants, new Predicate<RestaurantContent>()
        {
            @Override
            public boolean apply(RestaurantContent input)
            {
                return input.name.toLowerCase().contains(nameSearchText.toLowerCase());
            }
        }));
    }

    @NonNull
    private List<RestaurantContent> getFilteredByTypeRestaurants(List<RestaurantContent> restaurants)
    {
        if (selectedTypes.isEmpty())
        {
            return restaurants;
        }

        return new ArrayList<>(Collections2.filter(restaurants, new Predicate<RestaurantContent>()
        {
            @Override
            public boolean apply(RestaurantContent input)
            {
                return Iterables.any(input.getTypes(), new Predicate<String>()
                {
                    @Override
                    public boolean apply(String type)
                    {
                        return selectedTypes.contains(type);
                    }
                });
            }
        }));
    }

    public void setNameSearch(String text)
    {
        this.nameSearchText = text;
        updateRecyclerView();
    }

    public void setLocation(double latitude, double longitude)
    {
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        setLocation(location);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public final View      view;
        public final TextView  nameTextView;
        public final TextView  addressTextView;
        public final TextView  typeTextView;
        public final TextView  distanceTextView;
        public final TextView  isOpenTextView;
        public final ImageView restaurantImageView;

        public RestaurantContent item;

        public ViewHolder(View view)
        {
            super(view);
            this.view = view;
            nameTextView = (TextView) view.findViewById(R.id.restaurant_name);
            addressTextView = (TextView) view.findViewById(R.id.restaurant_address);
            typeTextView = (TextView) view.findViewById(R.id.restaurant_type);
            distanceTextView = (TextView) view.findViewById(R.id.restaurant_distance);
            isOpenTextView = (TextView) view.findViewById(R.id.is_open);
            restaurantImageView = ((ImageView) view.findViewById(R.id.restaurant_image));
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + nameTextView.getText() + "'";
        }
    }
}
