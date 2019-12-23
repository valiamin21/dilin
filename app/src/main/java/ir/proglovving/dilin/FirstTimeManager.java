package ir.proglovving.dilin;

import android.content.Context;
import android.content.SharedPreferences;

public class FirstTimeManager {
    private static final String PREF_NAME = "first_time_pref";
    private static final String PREF_KEY_IS_FIST_TIME = "is_first_time";

    public static boolean isFirstTime(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(PREF_KEY_IS_FIST_TIME, true);
    }

    // after running this code app recognizes that hasn't to show intro activity to user
    public static void registerAsFirstTime(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        preferences.edit().putBoolean(PREF_KEY_IS_FIST_TIME,false).apply();
    }
}
