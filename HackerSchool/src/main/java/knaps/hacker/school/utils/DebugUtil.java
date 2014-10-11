package knaps.hacker.school.utils;

import android.util.Log;

import knaps.hacker.school.BuildConfig;

/**
 * Created by lisaneigut on 11 Oct 2014.
 */
public class DebugUtil {

    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    public static void d(String tag, String message) {
        if (isDebug()) {
            Log.d(tag, message);
        }
    }

    public static void d(String tag, String message, Exception exception) {
        if (isDebug()) {
            Log.d(tag, message, exception);
        }
    }

    public static void e(String tag, String message) {
        if (isDebug()) {
            Log.e(tag, message);
        }
    }

    public static void e(String tag, String message, Exception exception) {
        if (isDebug()) {
            Log.e(tag, message, exception);
        }
    }
}
