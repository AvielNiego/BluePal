package com.avielniego.openhvr.ui.restaurantListFragment;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avielniego.openhvr.R;
import com.bumptech.glide.Glide;
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
    @Nullable private Context      context;
    @Nullable private Location     location;
    @Nullable private RecyclerView recyclerView;
    private Set<String> selectedTypes = new HashSet<>();

    public RestaurantListAdapter()
    {
    }

    public void setRestaurants(List<RestaurantContent> restaurants)
    {
        this.restaurants = restaurants;
        updateRecyclerView();
    }

    public void setContext(@Nullable Context context)
    {
        this.context = context;
    }

    private void updateRecyclerView()
    {
        if (recyclerView == null)
        {
            return;
        }

        filteredRestaurants();
        sortRestaurants();
        recyclerView.invalidate();
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
        holder.nameTextView.setText(restaurant.name);
        holder.typeTextView.setText(restaurant.type);
        setAddress(holder, restaurant);
        setDistance(holder, restaurant);
        setIsOpenTextViewValue(holder, restaurant);
        Glide.with(context).load(restaurant.image).placeholder(R.mipmap.ic_launcher).into(holder.restaurantImageView);

        holder.view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(context, restaurant.getTodayOpenHours(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAddress(ViewHolder holder, RestaurantContent restaurant)
    {
        if (context != null)
        {
            holder.addressTextView.setText(context.getString(R.string.address_format, restaurant.address, restaurant.city));
        }
    }

    private void setDistance(ViewHolder holder, RestaurantContent restaurant)
    {
        if (location != null && context != null)
        {
            String distance = String.valueOf(Math.floor(restaurant.getDistanceFrom(location) / 10) / 100);
            holder.distanceTextView.setText(context.getString(R.string.distance, distance));
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
        holder.isOpenTextView.setTextAppearance(context, R.style.closedTextStyle);
    }

    private void setOpenedStyle(ViewHolder holder)
    {
        holder.isOpenTextView.setText(R.string.open);
        holder.isOpenTextView.setTextAppearance(context, R.style.openTextStyle);
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
        notifyDataSetChanged();
        updateRecyclerView();
    }

    private void filteredRestaurants()
    {
        if (selectedTypes.isEmpty())
        {
            filteredRestaurants = restaurants;
        }
        else
        {
            filteredRestaurants = getFilteredRestaurants();
        }
    }

    @NonNull
    private ArrayList<RestaurantContent> getFilteredRestaurants()
    {
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
