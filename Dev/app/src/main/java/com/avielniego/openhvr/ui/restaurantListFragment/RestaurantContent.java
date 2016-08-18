package com.avielniego.openhvr.ui.restaurantListFragment;

import android.location.Location;
import android.support.annotation.NonNull;

import java.util.Calendar;

public class RestaurantContent
{
    public long id;
    public String image           = "";
    public String name            = "";
    public String desc            = "";
    public String area            = "";
    public String city            = "";
    public String address         = "";
    public String phone           = "";
    public String category        = "";
    public String type            = "";
    public String weekOpenHours   = "";
    public String fridayOpenHours = "";
    public String satOpenHours    = "";
    public String isKosher        = "";
    public String handicap        = "";
    public String website         = "";
    public double latitude;
    public double longitude;

    public boolean isOpenNow()
    {
        try
        {
            return tryIsOpenNow();
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw  new OpenHoursDoesNotPresented();
        }
    }

    private boolean tryIsOpenNow()
    {
        String todayOpenTime = getTodayOpenHours();

        String openTime = todayOpenTime.split("-")[0];
        int openHour = Integer.valueOf(openTime.split(":")[0]);
        int openMinuet = Integer.valueOf(openTime.split(":")[1]);

        String closeTime = todayOpenTime.split("-")[1];
        int closeHour = Integer.valueOf(closeTime.split(":")[0]);
        int closeMinuet = Integer.valueOf(closeTime.split(":")[1]);

        int nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int nowMinuet = Calendar.getInstance().get(Calendar.MINUTE);

        return (openHour < closeHour && openHour < nowHour && nowHour < closeHour) ||
                (openHour < closeHour && openHour == nowHour && openMinuet < nowMinuet) ||
                (openHour < closeHour && closeHour == nowHour && closeMinuet > nowMinuet) ||
                (openHour > closeHour && closeHour > nowHour) ||
                (openHour > closeHour && closeHour == nowHour && closeMinuet > nowMinuet) ||
                (openHour > closeHour && openHour < nowHour) ||
                (openHour > closeHour && openHour == nowHour && openMinuet < nowMinuet);
    }

    public String getTodayOpenHours()
    {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.FRIDAY)
            return fridayOpenHours;
        if (day == Calendar.SATURDAY)
            return satOpenHours;
        return weekOpenHours;
    }

    public float getDistanceFrom(Location location)
    {
        return getLocation().distanceTo(location);
    }

    @NonNull
    public Location getLocation()
    {
        Location l = new Location("");
        l.setLatitude(latitude);
        l.setLongitude(longitude);
        return l;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        RestaurantContent that = (RestaurantContent) o;

        if (Double.compare(that.latitude, latitude) != 0)
            return false;
        if (Double.compare(that.longitude, longitude) != 0)
            return false;
        return name.equals(that.name);

    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        result = name.hashCode();
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public static class OpenHoursDoesNotPresented extends RuntimeException{}
}
