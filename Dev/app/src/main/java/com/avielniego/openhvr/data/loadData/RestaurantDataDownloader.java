package com.avielniego.openhvr.data.loadData;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.avielniego.openhvr.R;

import org.json.JSONException;

import java.io.IOException;

public class RestaurantDataDownloader
{
    public static final String LOG_TAG = RestaurantDataDownloader.class.getSimpleName();

    private final static String SERVER_URL        = "https://www.hvr.co.il/";
    private final static String PATH_GET_ALL_DATA = "static/foodcard_branches.json";

    private final static Uri SERVER_URI       = Uri.parse(SERVER_URL);
    private final static Uri GET_ALL_DATA_URI = SERVER_URI.buildUpon().appendPath(PATH_GET_ALL_DATA).build();

    public void downloadData(Context context)
    {
        try
        {
            new RestaurantDataLoader(context, HttpUtils.downloadJson(GET_ALL_DATA_URI.toString())).loadToDB();
        } catch (IOException e)
        {
            setServerStatus(context, ServerStatus.SERVER_STATUS_SERVER_DOWN);
        } catch (JSONException e)
        {
            setServerStatus(context, ServerStatus.SERVER_STATUS_SERVER_INVALID);
        }
    }

    @SuppressLint("CommitPrefEdits") //This method should be called only on background
    public static void setServerStatus(Context context, @ServerStatus int serverStatus)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(context.getString(R.string.pref_location_status_key), serverStatus);
        spe.commit();
    }

    @SuppressWarnings("ResourceType")
    public static @ServerStatus int getServerStatus(Context context)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(context.getString(R.string.pref_location_status_key), ServerStatus.SERVER_STATUS_UNKNOWN);
    }

}
