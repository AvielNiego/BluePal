package com.avielniego.openhvr.data.loadData;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.avielniego.openhvr.alerts.NewRestaurantAlert;
import com.avielniego.openhvr.data.storedData.RestaurantContract.RestaurantEntry;
import com.avielniego.openhvr.entities.RestaurantContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RestaurantDataLoader
{
    private static final String LOG_TAG = RestaurantDataLoader.class.getSimpleName();

    private static final String HVR_IMAGE_PATH = "https://www.hvr.co.il/img_hvr/Gift_card_teamim/";

    private Context context;
    private JSONObject data;

    public RestaurantDataLoader(Context context, JSONObject data)
    {
        this.context = context;
        this.data = data;
    }

    public void loadToDB()
    {
        ContentValues[] restaurantsContentValues = parseJson();
        new NewRestaurantAlert(context, getOldRestaurants(), restaurantsContentValues).alert();
        context.getContentResolver().delete(RestaurantEntry.CONTENT_URI, null, null);
        context.getContentResolver().bulkInsert(RestaurantEntry.CONTENT_URI, restaurantsContentValues);
    }

    private List<RestaurantContent> getOldRestaurants() {
        Cursor query = context.getContentResolver().query(RestaurantEntry.CONTENT_URI, RestaurantCursorParser.PROJECTION, null, null, null);
        List<RestaurantContent> restaurantContents = new RestaurantCursorParser().parseMany(query);
        if (query != null) {
            query.close();
        }
        return restaurantContents;
    }

    @NonNull
    private ContentValues[] parseJson() {
        JSONArray moviesJsonArray = getJsonArray(data, "branch");
        ContentValues[] movieContentValues = new ContentValues[moviesJsonArray.length()];
        for (int i = 0; i < moviesJsonArray.length(); i++)
        {
            movieContentValues[i] = restaurantJsonToContentValue(getJsonObjectAt(moviesJsonArray, i));
        }
        return movieContentValues;
    }

    private ContentValues restaurantJsonToContentValue(JSONObject restaurantJsonObject)
    {
        try
        {
            return tryParingJsonRestaurantToContentValue(restaurantJsonObject);
        } catch (JSONException e)
        {
            jsonParsingError(e, "Error parsing json: " + restaurantJsonObject.toString());
            return new ContentValues();
        }
    }

    private ContentValues tryParingJsonRestaurantToContentValue(JSONObject restaurantJsonObject) throws JSONException
    {
        ContentValues movieContentValues = new ContentValues();

        movieContentValues.put(RestaurantEntry.COLUMN_RESTAURANT_IMAGE, HVR_IMAGE_PATH + restaurantJsonObject.getString("img"));
        movieContentValues.put(RestaurantEntry.COLUMN_RESTAURANT_NAME, restaurantJsonObject.getString("name"));
        movieContentValues.put(RestaurantEntry.COLUMN_RESTAURANT_DESC, restaurantJsonObject.getString("desc"));
        movieContentValues.put(RestaurantEntry.COLUMN_RESTAURANT_AREA, restaurantJsonObject.getString("area"));
        movieContentValues.put(RestaurantEntry.COLUMN_RESTAURANT_CITY, restaurantJsonObject.getString("city"));
        movieContentValues.put(RestaurantEntry.COLUMN_RESTAURANT_ADDRESS, restaurantJsonObject.getString("address"));
        movieContentValues.put(RestaurantEntry.COLUMN_RESTAURANT_PHONE, restaurantJsonObject.getString("phone"));
        movieContentValues.put(RestaurantEntry.COLUMN_RESTAURANT_CATEGORY, restaurantJsonObject.getString("category"));
        movieContentValues.put(RestaurantEntry.COLUMN_RESTAURANT_TYPE, restaurantJsonObject.getString("type"));
        movieContentValues.put(RestaurantEntry.COLUMN_RESTAURANT_WEEK_OPEN_HOURS, restaurantJsonObject.getString("hours15"));
        movieContentValues.put(RestaurantEntry.COLUMN_RESTAURANT_FRIDAY_OPEN_HOURS, restaurantJsonObject.getString("hours6"));
        movieContentValues.put(RestaurantEntry.COLUMN_RESTAURANT_SAT_OPEN_HOURS, restaurantJsonObject.getString("hours7"));
        movieContentValues.put(RestaurantEntry.COLUMN_RESTAURANT_IS_KOSHER, restaurantJsonObject.getString("kosher"));
        movieContentValues.put(RestaurantEntry.COLUMN_RESTAURANT_HANDICAP, restaurantJsonObject.getString("handicap"));
        movieContentValues.put(RestaurantEntry.COLUMN_RESTAURANT_WEBSITE, restaurantJsonObject.getString("website"));
        movieContentValues.put(RestaurantEntry.COLUMN_RESTAURANT_LATITUDE, restaurantJsonObject.getDouble("latitude"));
        movieContentValues.put(RestaurantEntry.COLUMN_RESTAURANT_LONGITUDE, restaurantJsonObject.getDouble("longitude"));

        return movieContentValues;
    }

    private JSONArray getJsonArray(JSONObject jsonObject, String name)
    {
        try
        {
            return jsonObject.getJSONArray(name);
        } catch (JSONException e)
        {
            jsonParsingError(e, "Error parsing json. Expected json array named: " + name);
            return new JSONArray();
        }
    }

    private void jsonParsingError(JSONException e, String errorMessage)
    {
        Log.e(LOG_TAG, errorMessage, e);
        RestaurantDataDownloader.setServerStatus(context, ServerStatus.SERVER_STATUS_SERVER_INVALID);
    }

    private JSONObject getJsonObjectAt(JSONArray jsonArray, int index)
    {
        try
        {
            return jsonArray.getJSONObject(index);
        } catch (JSONException e)
        {
            jsonParsingError(e, "Error parsing json. Expected json object on index: " + index);
            return new JSONObject();
        }
    }
}
