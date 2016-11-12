package com.avielniego.openhvr.alerts;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.avielniego.openhvr.R;
import com.avielniego.openhvr.entities.RestaurantContent;
import com.avielniego.openhvr.ui.main.MainActivity;
import com.avielniego.openhvr.ui.newRestaurants.NewRestaurantsActivity;

import java.util.ArrayList;
import java.util.List;

public class NewRestaurantAlert{
    private static final int MAX_STYLE_ROWS = 5;
    private static final String ALLOW_NEW_RESTAURANT_NOTIFICATIONS = "ALLOW_NEW_RESTAURANT_NOTIFICATIONS";
    public static final int NOTIFICATION_ID = 300;

    private Context context;
    private List<RestaurantContent> oldRestaurants;
    private List<RestaurantContent> newRestaurants = new ArrayList<>();

    public static void allowNotifications(Context context)
    {
        getSharedPreferences(context).edit().putBoolean(ALLOW_NEW_RESTAURANT_NOTIFICATIONS, true).apply();
    }

    public static void forbidNotifications(Context context)
    {
        getSharedPreferences(context).edit().putBoolean(ALLOW_NEW_RESTAURANT_NOTIFICATIONS, false).apply();
    }

    public static boolean isNotificationsAllowed(Context context) {
        return getSharedPreferences(context).getBoolean(ALLOW_NEW_RESTAURANT_NOTIFICATIONS, true);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(context.getString(R.string.content_authority), Context.MODE_PRIVATE);
    }

    public NewRestaurantAlert(Context context, List<RestaurantContent> oldRestaurants, List<RestaurantContent> newRestaurantContentValues) {
        this.context = context;
        this.oldRestaurants = oldRestaurants;
        this.newRestaurants = newRestaurantContentValues;
    }

    public void alert() {
        if (oldRestaurants.isEmpty() || !isNotificationsAllowed(context))
            return;

        alertNewRestaurants(getAddedRestaurants());
    }

    private void alertNewRestaurants(List<RestaurantContent> addedRestaurants) {
        if (addedRestaurants.isEmpty())
            return;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(context.getString(R.string.new_restaurants))
                .setContentText(context.getString(R.string.number_new_restaurants, addedRestaurants.size()))
                .setStyle(getInboxStyle(addedRestaurants))
                .setContentIntent(getPendingIntent(addedRestaurants))
                .setNumber(addedRestaurants.size())
                .setTicker(context.getString(R.string.number_new_restaurants, addedRestaurants.size()))
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .addAction(getDisableNotificationsAction())
                .setAutoCancel(true);
        showNotification(builder);
    }

    @NonNull
    private NotificationCompat.Action getDisableNotificationsAction() {
        return new NotificationCompat.Action(R.drawable.ic_disable_notifications,
                context.getString(R.string.disable_notifications),
                PendingIntent.getService(context, 0, new Intent(context, ForbidNotificationsService.class), 0));
    }

    @NonNull
    private NotificationCompat.InboxStyle getInboxStyle(List<RestaurantContent> addedRestaurants) {
        NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
        style.setBigContentTitle(context.getString(R.string.number_new_restaurants, addedRestaurants.size()));
        addLinesToInboxStyle(style, addedRestaurants);
        return style;
    }

    private void showNotification(NotificationCompat.Builder builder) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(NOTIFICATION_ID, builder.build());
    }

    private void addLinesToInboxStyle(NotificationCompat.InboxStyle style, List<RestaurantContent> addedRestaurants) {
        for (int i = 0; i < Math.min(addedRestaurants.size(), MAX_STYLE_ROWS); i++) {
            RestaurantContent restaurantContent = addedRestaurants.get(i);
            style.addLine(restaurantContent.name + ", " + restaurantContent.desc);
        }
    }

    @NonNull
    private List<RestaurantContent> getAddedRestaurants() {
        List<RestaurantContent> addedRestaurants = new ArrayList<>();
        for (RestaurantContent newRestaurant : newRestaurants)
            if(!oldRestaurants.contains(newRestaurant))
                addedRestaurants.add(newRestaurant);
        return addedRestaurants;
    }

    private PendingIntent getPendingIntent(List<RestaurantContent> addedRestaurants) {
        Intent resultIntent = NewRestaurantsActivity.newIntent(context, getAddedRestaurantsIds(addedRestaurants));
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private ArrayList<Integer> getAddedRestaurantsIds(List<RestaurantContent> addedRestaurants) {
        ArrayList<Integer> ids = new ArrayList<>();
        for (RestaurantContent restaurant : addedRestaurants) {
            ids.add(restaurant.id);
        }
        return ids;
    }

    public static class ForbidNotificationsService extends IntentService{
        public ForbidNotificationsService() {
            super(ForbidNotificationsService.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            NewRestaurantAlert.forbidNotifications(this);
            ((NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIFICATION_ID);
        }
    }
}
