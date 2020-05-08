package com.ipant.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDexApplication;
import android.util.Log;


/**
 * Created by motionmagik on 1/14/17.
 */

public class iPantApp extends MultiDexApplication {
    private static Context mContext;
    public static LocaleManager localeManager;
    private final String TAG = "App";


    @Override
    protected void attachBaseContext(Context base) {
//super.attachBaseContext(base);
        localeManager = new LocaleManager(base);
        super.attachBaseContext(localeManager.setLocale(base));
        Log.d(TAG, "attachBaseContext");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        localeManager = new LocaleManager(this);
        localeManager.setLocale(this);
    }

    public static boolean checkNetworkConnectivity() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        return isConnected;
    }


}

