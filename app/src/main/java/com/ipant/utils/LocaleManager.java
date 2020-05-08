package com.ipant.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LocaleManager {
    private static String LANGUAGE_ENGLISH = "sw";
    private static final String LANGUAGE_KEY = "language_key";

    private final SharedPreferences prefs;

    public LocaleManager(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

/*public static Locale getLocale(Resources res) {
Configuration config = res.getConfiguration();
return Utils.isAtLeastVersion(N) ? config.getLocales().get(0) : config.locale;
}*/

    public Context setLocale(Context c) {
        return updateResources(c, getLanguage());
    }

    public void setNewLocale(Context context, String language) {
        persistLanguage(language);
        try {
            Locale myLocale = new Locale(language);
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLanguage() {
        if(Locale.getDefault().getDisplayLanguage().equalsIgnoreCase("English")) {
            LANGUAGE_ENGLISH = "en";
        }
        else {
            LANGUAGE_ENGLISH = "sw";
        }
        return prefs.getString(LANGUAGE_KEY, LANGUAGE_ENGLISH);
    }

    @SuppressLint("ApplySharedPref")
    private void persistLanguage(String language) {
        prefs.edit().putString(LANGUAGE_KEY, language).commit();
    }

    private Context updateResources(Context context, String language) {
        try {
            Locale myLocale = new Locale(language);
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return context;
    }

}
