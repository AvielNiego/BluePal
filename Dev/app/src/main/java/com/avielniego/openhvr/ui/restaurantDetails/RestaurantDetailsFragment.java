package com.avielniego.openhvr.ui.restaurantDetails;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avielniego.openhvr.R;
import com.avielniego.openhvr.entities.RestaurantContent;
import com.avielniego.openhvr.location.LocationUtils;
import com.avielniego.openhvr.ui.analytics.AnalyticsApplication;
import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;

public class RestaurantDetailsFragment extends Fragment
{
    private static final int DETAIL_LOADER_ID = 0;

    private static final String RESTAURANT_ID_ARG = "RESTAURANT_ID_ARG";
    private static final String LOCATION_ARG      = "LOCATION_ARG";

    private Uri        restaurantUri;
    private ViewHolder viewHolder;
    @Nullable private Location   location;

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
        RestaurantLoader loader = new RestaurantLoader(restaurantUri,
                                                       getContext(),
                                                       new RestaurantLoader.RestaurantLoadListener()
                                                       {
                                                           @Override
                                                           public void onRestaurantLoaded(RestaurantContent restaurant)
                                                           {
                                                               RestaurantDetailsFragment.this.onRestaurantLoaded(restaurant);
                                                           }
                                                       });
        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, loader);
    }

    private void onRestaurantLoaded(final RestaurantContent restaurant)
    {
        viewHolder.item = restaurant;
        setName(restaurant);
        viewHolder.descriptionTextView.setText(restaurant.desc);
        viewHolder.typeTextView.setText(restaurant.type);
        setPhone(restaurant);
        setWebsite(restaurant);
        viewHolder.weekOpenHours.setText(restaurant.weekOpenHours);
        viewHolder.fridayOpenHours.setText(restaurant.fridayOpenHours);
        viewHolder.saturdayOpenHours.setText(restaurant.satOpenHours);
        setKosher(restaurant);
        viewHolder.handicap.setText(restaurant.getHandicapString(getContext()));
        setAddress(viewHolder, restaurant);
        setDistance(viewHolder, restaurant);
        Glide.with(getContext()).load(restaurant.image).placeholder(R.mipmap.ic_launcher)
                .into(viewHolder.restaurantImageView);
    }

    private void setKosher(RestaurantContent restaurant)
    {
        viewHolder.kosher.setText(restaurant.getKosherString(getContext()));
    }

    private void setWebsite(final RestaurantContent restaurant)
    {
        viewHolder.goToWebsiteAction.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onWebsiteActionClicked(restaurant);
            }
        });
    }

    private void onWebsiteActionClicked(RestaurantContent restaurant)
    {
        logAction("GoToWebsiteAction");
        sendToWebsite(restaurant);
    }

    private void logAction(String actionName) {
        ((AnalyticsApplication) getActivity().getApplication()).getDefaultTracker()
                .send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction(actionName)
                        .build());
    }

    private void sendToWebsite(RestaurantContent restaurant) {
        String url = restaurant.website;
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "http://" + url;
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    private void setPhone(final RestaurantContent restaurant)
    {
        viewHolder.phoneTextView.setText(restaurant.phone);
        viewHolder.callAction.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onCallActionClicked(restaurant);
            }
        });
    }

    private void onCallActionClicked(RestaurantContent restaurant) {
        logAction("CallToRestaurantAction");
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + restaurant.phone)));
    }

    private void setName(RestaurantContent restaurant)
    {
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)
        {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(restaurant.name);
        }
    }

    private void setAddress(ViewHolder holder, final RestaurantContent restaurant)
    {
        holder.addressTextView.setText(getContext().getString(R.string.address_format, restaurant.address, restaurant.city));

        viewHolder.navigateAction.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onNavigationActionClicked(restaurant);
            }
        });
    }

    private void onNavigationActionClicked(RestaurantContent restaurant) {
        logAction("NavigationAction");
        LocationUtils.startNavigationActivity(getContext(),
                                              restaurant.address + ", " + restaurant.city,
                                              restaurant.getLocation());
    }

    private void setDistance(ViewHolder holder, RestaurantContent restaurant)
    {
        if (location != null)
        {
            String distance = String.valueOf(Math.floor(restaurant.getDistanceFrom(location) / 10) / 100);
            holder.distanceTextView.setText(getContext().getString(R.string.distance, distance));
        }
    }

    public class ViewHolder
    {
        public final View      view;
        public final View      goToWebsiteAction;
        public final View      callAction;
        public final View      navigateAction;
        public final TextView  nameTextView;
        public final TextView  addressTextView;
        public final TextView  typeTextView;
        public final TextView  descriptionTextView;
        public final TextView  distanceTextView;
        public final TextView  phoneTextView;
        public final TextView  weekOpenHours;
        public final TextView  fridayOpenHours;
        public final TextView  saturdayOpenHours;
        public final TextView  kosher;
        public final TextView  handicap;
        public final ImageView restaurantImageView;

        public RestaurantContent item;

        public ViewHolder(View view)
        {
            this.view = view;
            nameTextView = (TextView) view.findViewById(R.id.restaurant_name);
            addressTextView = (TextView) view.findViewById(R.id.restaurant_address);
            typeTextView = (TextView) view.findViewById(R.id.restaurant_type);
            descriptionTextView = (TextView) view.findViewById(R.id.restaurant_descr);
            distanceTextView = (TextView) view.findViewById(R.id.restaurant_distance);
            phoneTextView = (TextView) view.findViewById(R.id.phone_number_text_view);
            weekOpenHours = (TextView) view.findViewById(R.id.week_open_hours);
            fridayOpenHours = (TextView) view.findViewById(R.id.friday_open_hours);
            saturdayOpenHours = (TextView) view.findViewById(R.id.saturday_open_hours);
            handicap = (TextView) view.findViewById(R.id.handicap);
            kosher = (TextView) view.findViewById(R.id.kosher);
            goToWebsiteAction = view.findViewById(R.id.website_container);
            callAction = view.findViewById(R.id.phone_number_container);
            navigateAction = view.findViewById(R.id.navigate_container);
            restaurantImageView = ((ImageView) view.findViewById(R.id.restaurant_image));
        }
    }
}
