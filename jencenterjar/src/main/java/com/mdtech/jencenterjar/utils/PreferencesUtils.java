package com.mdtech.jencenterjar.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtils {
    public PreferencesUtils() {
    }

    public static void setPreferences(Context context, String preference, String key, String value) {
        if(context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(preference, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.commit();
        }
    }

    public static String getPreference(Context context, String preference, String key, String defaultValue) {
        if(context == null) {
            return null;
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences(preference, 0);
            return sharedPreferences.getString(key, defaultValue);
        }
    }

    public static void setPreferences(Context context, String preference, String key, int value) {
        if(context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(preference, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(key, value);
            editor.commit();
        }
    }

    public static int getPreference(Context context, String preference, String key, int defaultValue) {
        if(context == null) {
            return -1;
        } else {
            SharedPreferences sharedPreferences = context.getSharedPreferences(preference, 0);
            return sharedPreferences.getInt(key, defaultValue);
        }
    }

}
