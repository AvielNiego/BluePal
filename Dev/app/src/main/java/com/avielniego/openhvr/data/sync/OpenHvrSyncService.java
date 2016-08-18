package com.avielniego.openhvr.data.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class OpenHvrSyncService extends Service
{
    private static final Object             sSyncAdapterLock    = new Object();
    private static       OpenHvrSyncAdapter sOpenHvrSyncAdapter = null;

    @Override
    public void onCreate()
    {
        synchronized (sSyncAdapterLock)
        {
            if (sOpenHvrSyncAdapter == null)
            {
                sOpenHvrSyncAdapter = new OpenHvrSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sOpenHvrSyncAdapter.getSyncAdapterBinder();
    }

}
