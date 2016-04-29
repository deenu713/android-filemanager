package com.bresan.learning.filemanager.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rodrigobresan on 4/27/16.
 */

public class SharedPreferencesUtils {

    private static final String PREFERENCES_FILE = "file_manager_preferences_file";
    private static final String KEY_IS_FIRST_RUN = "is_first_run";
    private static final String KEY_DEFAULT_FOLDER = "default_folder";

    /**
     * This method is used to check if the user has already done the app first run.
     *
     * @param context the context of the application
     * @return a boolean if it's the user first run
     */
    public static boolean isFirstRun(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_IS_FIRST_RUN, true);
    }

    /**
     * Set the value of the first run key on shared preferences. It's used way after user has executed
     * the app for the first time
     *
     * @param context the context of the application
     */
    public static void setFirstRunDone(Context context) {
        SharedPreferences.Editor preferencesEditor = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        preferencesEditor.putBoolean(KEY_IS_FIRST_RUN, false);
        preferencesEditor.apply();
    }

    public static void setDefaultFolder(Context context, String folderPath) {
        SharedPreferences.Editor preferencesEditor = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        preferencesEditor.putString(KEY_DEFAULT_FOLDER, folderPath);
        preferencesEditor.apply();
    }

    public static String getDefaultFolder(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return preferences.getString(KEY_DEFAULT_FOLDER, "/");
    }
}
