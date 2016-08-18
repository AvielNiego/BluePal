package com.avielniego.openhvr.data.loadData;

import android.app.IntentService;
import android.content.Intent;

public class RestaurantDataDownloadService extends IntentService
{
    public RestaurantDataDownloadService()
    {
        super(RestaurantDataDownloadService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        new RestaurantDataDownloader().downloadData(this);
    }

}
