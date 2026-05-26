package com.example.globatalk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.Calendar;

public class StreakManager {
    private static final String PREF_STREAK = "streak_count";
    private static final String PREF_LAST_DATE = "last_active_date";

    public static void updateStreak(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int streak = prefs.getInt(PREF_STREAK, 0);
        long lastDateMillis = prefs.getLong(PREF_LAST_DATE, 0);

        Calendar lastDate = Calendar.getInstance();
        lastDate.setTimeInMillis(lastDateMillis);

        Calendar today = Calendar.getInstance();

        if (isSameDay(lastDate, today)) {
            return; // Already active today
        }

        if (isYesterday(lastDate, today)) {
            streak++;
        } else {
            streak = 1; // Streak broken
        }

        prefs.edit()
                .putInt(PREF_STREAK, streak)
                .putLong(PREF_LAST_DATE, today.getTimeInMillis())
                .apply();
    }

    public static int getStreak(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_STREAK, 0);
    }

    private static boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private static boolean isYesterday(Calendar cal1, Calendar cal2) {
        Calendar yesterday = (Calendar) cal2.clone();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        return isSameDay(cal1, yesterday);
    }
}
