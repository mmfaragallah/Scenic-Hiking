package com.mfarag.scenichiking;

import android.app.Application;

import com.mapbox.mapboxsdk.Mapbox;

public class ScenicHikingApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Mapbox.getInstance(this, getString(R.string.access_token));
//        Mapbox.getInstance(this, "pk.eyJ1IjoibW1mYXJhZyIsImEiOiJjanp6Y2l6Z2QwcDlpM21xdGd2bDh4cmIzIn0.did4Hlgdtl0hMLdRsHOprA");
    }
}
