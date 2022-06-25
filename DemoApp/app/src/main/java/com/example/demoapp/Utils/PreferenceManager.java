package com.example.demoapp.Utils;

import static com.example.demoapp.Utils.Cons.INTENT_PHONE_NUMBER;
import static com.example.demoapp.Utils.Cons.PREF_IS_SIGNED_IN;
import static com.example.demoapp.Utils.Cons.PREF_NEEDS_TO_SETUP_PROFILE;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {

    private final SharedPreferences sharedPreferences;

    /**
     * Default constructor
     */
    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Cons.PREF_GLOBAL_NAME, Context.MODE_PRIVATE);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    /**
     * Puts a boolean into the preferences
     */
    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Gets a boolean from the preferences
     */
    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    /**
     * Puts a String into the preferences
     */
    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Gets a String from the preferences
     */
    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    /**
     * Puts an int into the preferences
     */
    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Gets an int from the preferences
     */
    public int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    /**
     * Clears the desired preferences when the user signs out
     */
    public void clearPrefsForSignOut() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(PREF_IS_SIGNED_IN);
        editor.remove(INTENT_PHONE_NUMBER);
        editor.remove(PREF_NEEDS_TO_SETUP_PROFILE);
        editor.apply();
    }
}

