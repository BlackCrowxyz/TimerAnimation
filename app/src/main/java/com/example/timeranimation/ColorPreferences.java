package com.example.timeranimation;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

public final class ColorPreferences {

    public static final String KEY_HOUR_HAND = "pref_hour_hand";
    public static final String KEY_MINUTE_HAND = "pref_minute_hand";
    public static final String KEY_SECOND_HAND = "pref_second_hand";
    public static final String KEY_MILLI_HAND = "pref_milli_hand";
    public static final String KEY_MARKER_COLOR = "pref_marker_color";

    private ColorPreferences() {
    }

    public static boolean isClockColorKey(String key) {
        return KEY_HOUR_HAND.equals(key)
                || KEY_MINUTE_HAND.equals(key)
                || KEY_SECOND_HAND.equals(key)
                || KEY_MILLI_HAND.equals(key)
                || KEY_MARKER_COLOR.equals(key);
    }

    public static int getColor(Context context, SharedPreferences preferences, String key, @ColorRes int fallbackColorRes) {
        String storedValue = preferences.getString(key, null);
        if (storedValue == null || storedValue.isEmpty()) {
            return ContextCompat.getColor(context, fallbackColorRes);
        }
        try {
            return Color.parseColor(storedValue);
        } catch (IllegalArgumentException ex) {
            return ContextCompat.getColor(context, fallbackColorRes);
        }
    }
}

