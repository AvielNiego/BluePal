package com.avielniego.openhvr.ui.restaurantDetails;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avielniego.openhvr.R;
import com.avielniego.openhvr.ui.restaurantListFragment.RestaurantContent;
import com.bumptech.glide.Glide;

public class RestaurantDetailsFragment extends Fragment
{
    private static final int DETAIL_LOADER_ID = 0;

    private static final String RESTAURANT_ID_ARG = "RESTAURANT_ID_ARG";
    private static final String LOCATION_ARG      = "LOCATION_ARG";

    private Uri restaurantUri;
    private ViewHolder viewHolder;
    private Location location;

    public static RestaurantDetailsFragment newInstance(Uri restaurantUri, Location location)
    {
        RestaurantDetailsFragment fragment = new RestaurantDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(RESTAURANT_ID_ARG, restaurantUri);
        args.putParcelable(LOCATION_ARG, location);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        loadArgs();
    }

    private void loadArgs()
    {
        if (getArguments() != null)
        {
            this.restaurantUri = getArguments().getParcelable(RESTAURANT_ID_ARG);
            this.location = getArguments().getParcelable(LOCATION_ARG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_restaurant_details, container, false);
        viewHolder = new ViewHolder(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        RestaurantLoader loader = new RestaurantLoader(restaurantUri, getContext(), new RestaurantLoader.RestaurantLoadListener()
        {
            @Override
            public void onRestaurantLoaded(RestaurantContent restaurant)
            {
                RestaurantDetailsFragment.this.onRestaurantLoaded(restaurant);
            }
        });
        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, loader);
    }

    private void onRestaurantLoaded(RestaurantContent restaurant)
    {
        viewHolder.item = restaurant;
        viewHolder.nameTextView.setText(restaurant.name);
        viewHolder.typeTextView.setText(restaurant.type);
        setAddress(viewHolder, restaurant);
        setDistance(viewHolder, restaurant);
        setIsOpenTextViewValue(viewHolder, restaurant);
        Glide.with(getContext()).load(restaurant.image).placeholder(R.mipmap.ic_launcher).into(viewHolder.restaurantImageView);
    }

    private void setAddress(ViewHolder holder, RestaurantContent restaurant)
    {
        if (getContext() != null)
        {
            holder.addressTextView.setText(getContext().getString(R.string.address_format, restaurant.address, restaurant.city));
        }
    }

    private void setDistance(ViewHolder holder, RestaurantContent restaurant)
    {
        if (location != null)
        {
            String distance = String.valueOf(Math.floor(restaurant.getDistanceFrom(location) / 10) / 100);
            holder.distanceTextView.setText(getContext().getString(R.string.distance, distance));
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
        holder.isOpenTextView.setTextAppearance(getContext(), R.style.closedTextStyle);
    }

    private void setOpenedStyle(ViewHolder holder)
    {
        holder.isOpenTextView.setText(R.string.open);
        holder.isOpenTextView.setTextAppearance(getContext(), R.style.openTextStyle);
    }


    public class ViewHolder
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
            this.view = view;
            nameTextView = (TextView) view.findViewById(R.id.restaurant_name);
            addressTextView = (TextView) view.findViewById(R.id.restaurant_address);
            typeTextView = (TextView) view.findViewById(R.id.restaurant_type);
            distanceTextView = (TextView) view.findViewById(R.id.restaurant_distance);
            isOpenTextView = (TextView) view.findViewById(R.id.is_open);
            restaurantImageView = ((ImageView) view.findViewById(R.id.restaurant_image));
        }
    }
}
