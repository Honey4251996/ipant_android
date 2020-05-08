package com.ipant.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class App_Preferences {

    public static final String PREFS_NAME = "iPant";


    public App_Preferences() {
        super();
    }


    public static void saveStringPref(Context context, String key, String value) {
        SharedPreferences settings;
        Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(key, value);
        editor.commit();

    }


    public static void saveLongPref(Context context, String key, long value) {
        SharedPreferences settings;
        Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putLong(key, value);
        editor.commit();

    }


    public static void saveBooleanPref(Context context, String key, boolean value) {
        SharedPreferences settings;
        Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();

    }



    public static String loadStringPref(Context context, String key) {
        SharedPreferences settings;
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        String status = settings.getString(key, "");
        return status;

    }

    public static boolean loadBooleanPref(Context context, String key) {
        SharedPreferences settings;
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        boolean status = settings.getBoolean(key, false);
        return status;

    }


    public static void saveUserData(Context context) {

        SharedPreferences settings;
        Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.commit();

    }


    public static void clearAllPreferenceData(Context context) {

        SharedPreferences settings;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);


        Editor editor = settings.edit();
        editor.clear();
        editor.commit();
    }


}