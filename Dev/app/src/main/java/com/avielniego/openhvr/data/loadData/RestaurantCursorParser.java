package com.avielniego.openhvr.data.loadData;

import android.database.Cursor;

import com.avielniego.openhvr.data.storedData.RestaurantContract;
import com.avielniego.openhvr.entities.RestaurantContent;

import java.util.ArrayList;
import java.util.List;

public class RestaurantCursorParser {

    public static final String[] PROJECTION = new String[]{
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry._ID,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_IMAGE,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_NAME,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_DESC,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_AREA,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_CITY,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_ADDRESS,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_PHONE,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_CATEGORY,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_TYPE,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_WEEK_OPEN_HOURS,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_FRIDAY_OPEN_HOURS,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_SAT_OPEN_HOURS,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_IS_KOSHER,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_HANDICAP,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_WEBSITE,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_LATITUDE,
            RestaurantContract.RestaurantEntry.TABLE_NAME + "." + RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_LONGITUDE};

    private static final int RESTAURANT_ID_COLUMN_INDEX          = 0;
    private static final int COLUMN_RESTAURANT_IMAGE             = 1;
    private static final int RESTAURANT_NAME_COLUMN_INDEX        = 2;
    private static final int COLUMN_RESTAURANT_DESC              = 3;
    private static final int COLUMN_RESTAURANT_AREA              = 4;
    private static final int COLUMN_RESTAURANT_CITY              = 5;
    private static final int COLUMN_RESTAURANT_ADDRESS           = 6;
    private static final int COLUMN_RESTAURANT_PHONE             = 7;
    private static final int COLUMN_RESTAURANT_CATEGORY          = 8;
    private static final int COLUMN_RESTAURANT_TYPE              = 9;
    private static final int COLUMN_RESTAURANT_WEEK_OPEN_HOURS   = 10;
    private static final int COLUMN_RESTAURANT_FRIDAY_OPEN_HOURS = 11;
    private static final int COLUMN_RESTAURANT_SAT_OPEN_HOURS    = 12;
    private static final int COLUMN_RESTAURANT_IS_KOSHER         = 13;
    private static final int COLUMN_RESTAURANT_HANDICAP          = 14;
    private static final int COLUMN_RESTAURANT_WEBSITE           = 15;
    private static final int COLUMN_RESTAURANT_LATITUDE          = 16;
    private static final int COLUMN_RESTAURANT_LONGITUDE         = 17;

    public List<RestaurantContent> parseMany(Cursor data)
    {
        List<RestaurantContent> restaurantsContent = new ArrayList<>();
        data.moveToFirst();
        while (!data.isAfterLast())
        {
            restaurantsContent.add(parse(data));
            data.moveToNext();
        }
        return restaurantsContent;
    }

    public RestaurantContent parse(Cursor data)
    {
        RestaurantContent content = new RestaurantContent();
        content.id = data.getLong(RESTAURANT_ID_COLUMN_INDEX);
        content.image = data.getString(COLUMN_RESTAURANT_IMAGE);
        content.name = data.getString(RESTAURANT_NAME_COLUMN_INDEX);
        content.desc = data.getString(COLUMN_RESTAURANT_DESC);
        content.area = data.getString(COLUMN_RESTAURANT_AREA);
        content.city = data.getString(COLUMN_RESTAURANT_CITY);
        content.address = data.getString(COLUMN_RESTAURANT_ADDRESS);
        content.phone = data.getString(COLUMN_RESTAURANT_PHONE);
        content.category = data.getString(COLUMN_RESTAURANT_CATEGORY);
        content.type = data.getString(COLUMN_RESTAURANT_TYPE);
        content.weekOpenHours = data.getString(COLUMN_RESTAURANT_WEEK_OPEN_HOURS);
        content.fridayOpenHours = data.getString(COLUMN_RESTAURANT_FRIDAY_OPEN_HOURS);
        content.satOpenHours = data.getString(COLUMN_RESTAURANT_SAT_OPEN_HOURS);
        content.kosher = data.getString(COLUMN_RESTAURANT_IS_KOSHER);
        content.handicap = data.getString(COLUMN_RESTAURANT_HANDICAP);
        content.website = data.getString(COLUMN_RESTAURANT_WEBSITE);
        content.latitude = data.getDouble(COLUMN_RESTAURANT_LATITUDE);
        content.longitude = data.getDouble(COLUMN_RESTAURANT_LONGITUDE);
        return content;
    }
}
