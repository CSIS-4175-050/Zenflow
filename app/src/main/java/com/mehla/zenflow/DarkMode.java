package com.mehla.zenflow;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class DarkMode {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String DARK_MODE_PREF = "darkMode";

    Context context;

    public DarkMode(Context context) {
        this.context = context;

        // Load the dark mode state
        boolean isDarkMode = loadDarkModeState(context);
        if (isDarkMode) {
            // Enable dark mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            // Disable dark mode
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public boolean loadDarkModeState(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(DARK_MODE_PREF, false);
    }

    public void saveDarkModeState(boolean isDarkMode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(DARK_MODE_PREF, isDarkMode);
        editor.apply();
    }
}
