package com.trunch.trunch.utilities;

import android.content.SharedPreferences;

/**
 * Created by or on 4/6/2015.
 */
public class SharedPrefUtils {

    public static final String SHARED_PREF_NAME = "com.package.SHARED_PREF_NAME";
    public static final String SHARED_PREF_KEY_LAST_TIME_DOWNLOADED = "com.package.SHARED_PREF_KEY_LAST_TIME_DOWNLOADED";
    public static final String SHARED_PREF_KEY_FOOD_TAGS = "com.package.SHARED_PREF_KEY_FOOD_TAGS";
    public static final String SHARED_PREF_KEY_RESTAURANT = "com.package.SHARED_PREF_KEY_RESTAURANT";
    public static final String SHARED_PREF_USER_ID = "com.package.SHARED_PREF_USER_ID";
    public static final String SHARED_PREF_HAS_TRUNCH = "com.package.SHARED_PREF_HAS_TRUNCH";
    public static final String SHARED_PREF_TRUNCHERS = "com.package.SHARED_PREF_TRUNCHERS";
    public static final String SHARED_PREF_CHOSEN_REST = "com.package.SHARED_CHOSEN_REST";


    public static boolean hasTrunch(SharedPreferences mSharedPreferences) {
        return mSharedPreferences.getBoolean(SHARED_PREF_HAS_TRUNCH, false);
    }

    public static String getTrunchers(SharedPreferences mSharedPreferences) {
        return mSharedPreferences.getString(SHARED_PREF_TRUNCHERS, "No One!!");
    }

    public static String getChosenRest (SharedPreferences mSharedPreferences) {
        return mSharedPreferences.getString(SHARED_PREF_CHOSEN_REST, "No One!!");
    }

    public static String getRests(SharedPreferences mSharedPreferences) {
        return mSharedPreferences.getString(SHARED_PREF_KEY_RESTAURANT, "{'empty' : empty}");
    }

    public static String getFoodTags(SharedPreferences mSharedPreferences) {
        return mSharedPreferences.getString(SHARED_PREF_KEY_FOOD_TAGS, "{'empty' : empty}");
    }

    public static void UpdateTrunchResult(SharedPreferences mSharedPreferences, String trunchers,
                                          String chosenRest,boolean hasTrunch) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(SHARED_PREF_HAS_TRUNCH, hasTrunch);
        if(hasTrunch) {
            edit.putString(SHARED_PREF_CHOSEN_REST, chosenRest);
            edit.putString(SHARED_PREF_TRUNCHERS, trunchers);
        }
        edit.commit();
    }

    public static void saveRestData(SharedPreferences mSharedPreferences, String jsonTags,
                                          String jsonRest) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(SHARED_PREF_KEY_FOOD_TAGS, jsonTags);
        edit.putString(SHARED_PREF_KEY_RESTAURANT, jsonRest);
        edit.putLong(SHARED_PREF_KEY_LAST_TIME_DOWNLOADED, System.currentTimeMillis());
        edit.commit();
    }


    public static void saveUserID(SharedPreferences mSharedPreferences, String userID) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(SHARED_PREF_USER_ID, userID);
        edit.commit();
    }

    public static String getUserId(SharedPreferences mSharedPreferences) {
        return mSharedPreferences.getString(SHARED_PREF_USER_ID, null);
    }

    public static boolean isLoggedIn (SharedPreferences mSharedPreferences) {
        return mSharedPreferences.getString(SHARED_PREF_USER_ID, null) != null;
    }

    public static long lastTimeDownloaded (SharedPreferences mSharedPreferences) {
        return mSharedPreferences.getLong(SHARED_PREF_KEY_LAST_TIME_DOWNLOADED, -1);
    }

    public static void clearTrunched(SharedPreferences mSharedPreferences) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(SHARED_PREF_HAS_TRUNCH, false);
        edit.commit();
    }



}
