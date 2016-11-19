package com.avielniego.openhvr.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.avielniego.openhvr.R;
import com.avielniego.openhvr.ui.analytics.AnalyticsApplication;
import com.avielniego.openhvr.ui.analytics.AnalyticsLogger;
import com.avielniego.openhvr.ui.restaurantDetails.RestaurantsDetailsActivity;

public class AboutActivity extends AppCompatActivity {

    public static final String AVIEL_LINKEDIN_URL = "https://www.linkedin.com/in/aviel-niego-93072433?trk=hp-identity-photo";
    public static final String RAVID_HOME_PAGE = "http://ravidgal.com/";

    private AnalyticsLogger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setToolbar();
        logger = new AnalyticsLogger(((AnalyticsApplication) getApplication()));
        addLinksFromNames();
    }

    private void addLinksFromNames() {
        findViewById(R.id.by_avielniego).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onByTextViewClicked();
            }
        });
        findViewById(R.id.designed_by).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDesignedByViewClicked();
            }
        });
    }

    private void onDesignedByViewClicked() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(RAVID_HOME_PAGE)));
        logger.logAction(R.id.designed_by);
    }

    private void onByTextViewClicked() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AVIEL_LINKEDIN_URL)));
        logger.logAction(R.id.by_avielniego);
    }


    private void setToolbar() {
        if (getSupportActionBar() == null) {
            return;
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.about);
    }

    @Override
    protected void onResume() {
        super.onResume();
        logger.logScreen(AboutActivity.class.getSimpleName());
    }
}
