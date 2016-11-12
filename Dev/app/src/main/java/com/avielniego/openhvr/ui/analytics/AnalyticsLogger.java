package com.avielniego.openhvr.ui.analytics;

import com.avielniego.openhvr.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class AnalyticsLogger {

    private Tracker tracker;
    private AnalyticsApplication application;

    public AnalyticsLogger(AnalyticsApplication application) {
        tracker = application.getDefaultTracker();
        this.application = application;
    }

    public void logScreen(String screenName) {
        tracker.setScreenName("Activity~" + screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void logTab(String tabName) {
        tracker.setScreenName("Tab~" + tabName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void logAction(String actionName) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction(actionName)
                .build());
    }

    public void logAction(int clickedResId) {
        logAction(application.getResources().getResourceEntryName(clickedResId) + application.getString(R.string.clicked));
    }
}
