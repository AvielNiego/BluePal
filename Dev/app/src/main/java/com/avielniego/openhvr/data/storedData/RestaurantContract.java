package com.avielniego.openhvr.data.storedData;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class RestaurantContract
{
    public static final String CONTENT_AUTHORITY = "com.avielniego.restaurant";
    public static final Uri    BASE_CONTENT_URI  = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_RESTAURANT = "restaurant";

    public static final class RestaurantEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "restaurant";

        public static final String COLUMN_RESTAURANT_IMAGE             = "image";
        public static final String COLUMN_RESTAURANT_NAME              = "name";
        public static final String COLUMN_RESTAURANT_DESC              = "desc";
        public static final String COLUMN_RESTAURANT_AREA              = "area";
        public static final String COLUMN_RESTAURANT_CITY              = "city";
        public static final String COLUMN_RESTAURANT_ADDRESS           = "address";
        public static final String COLUMN_RESTAURANT_PHONE             = "phone";
        public static final String COLUMN_RESTAURANT_CATEGORY          = "category";
        public static final String COLUMN_RESTAURANT_TYPE              = "type";
        public static final String COLUMN_RESTAURANT_WEEK_OPEN_HOURS   = "week_hours";
        public static final String COLUMN_RESTAURANT_FRIDAY_OPEN_HOURS = "friday_hours";
        public static final String COLUMN_RESTAURANT_SAT_OPEN_HOURS    = "sat_hours";
        public static final String COLUMN_RESTAURANT_IS_KOSHER         = "is_kosher";
        public static final String COLUMN_RESTAURANT_HANDICAP          = "handicap";
        public static final String COLUMN_RESTAURANT_WEBSITE           = "website";
        public static final String COLUMN_RESTAURANT_LATITUDE          = "latitude";
        public static final String COLUMN_RESTAURANT_LONGITUDE         = "longitude";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_RESTAURANT).build();

        public static final String CONTENT_TYPE      = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RESTAURANT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_RESTAURANT;

        public static Uri buildRestaurantUri(long id)
        {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
