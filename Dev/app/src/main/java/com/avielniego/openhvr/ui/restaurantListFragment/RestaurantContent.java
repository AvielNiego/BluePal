package com.avielniego.openhvr.ui.restaurantListFragment;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;

import com.avielniego.openhvr.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class RestaurantContent {
    public long id;
    public String image = "";
    public String name = "";
    public String desc = "";
    public String area = "";
    public String city = "";
    public String address = "";
    public String phone = "";
    public String category = "";
    public String type = "";
    public String weekOpenHours = "";
    public String fridayOpenHours = "";
    public String satOpenHours = "";
    public String kosher = "";
    public String handicap = "";
    public String website = "";
    public double latitude;
    public double longitude;

    public boolean isOpenNow() {
        try {
            return tryIsOpenNow();
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new OpenHoursDoesNotPresented();
        }
    }

    private boolean tryIsOpenNow() {
        String todayOpenTime = getTodayOpenHours();

        String openTime = todayOpenTime.split("-")[0];
        int openHour = Integer.valueOf(openTime.split(":")[0].trim());
        int openMinuet = Integer.valueOf(openTime.split(":")[1].trim());

        String closeTime = todayOpenTime.split("-")[1];
        int closeHour = Integer.valueOf(closeTime.split(":")[0].trim());
        int closeMinuet = Integer.valueOf(closeTime.split(":")[1].trim());

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

    public String getTodayOpenHours() {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.FRIDAY)
            return fridayOpenHours;
        if (day == Calendar.SATURDAY)
            return satOpenHours;
        return weekOpenHours;
    }

    public float getDistanceFrom(Location location) {
        return getLocation().distanceTo(location);
    }

    @NonNull
    public Location getLocation() {
        Location l = new Location("");
        l.setLatitude(latitude);
        l.setLongitude(longitude);
        return l;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        RestaurantContent that = (RestaurantContent) o;

        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;
        if (city != null ? !city.equals(that.city) : that.city != null)
            return false;
        return address != null ? address.equals(that.address) : that.address == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }

    public List<String> getTypes() {
        return Arrays.asList(type.split(","));
    }

    public String getKosherString(Context context) {
        return kosher.isEmpty() ? context.getString(R.string.not_kosher) : kosher;
    }

    public String getHandicapString(Context context) {
        return handicap.equals("-") ? context.getString(R.string.no) : handicap;
    }

    public static class OpenHoursDoesNotPresented extends RuntimeException {
    }
}
