package knaps.hacker.school.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lisaneigut on 22 Sep 2013.
 */
public class SharedPrefsUtil {
    static final String PREFS_USER = "user_prefs";
    static final String USER_EMAIL = "user_email";
    private static final String USER_HIGH_SCORE = "user_highscore";

    public static String getUserEmail(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_USER, Context.MODE_PRIVATE);
        return prefs.getString(USER_EMAIL, "");
    }

    public static void saveUserEmail(Context context, String email) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_USER, Context.MODE_PRIVATE);
        prefs.edit().putString(USER_EMAIL, email.toLowerCase().trim()).apply();
    }

    public static int getHighScore(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_USER, Context.MODE_PRIVATE);
        return prefs.getInt(USER_HIGH_SCORE, -1);
    }

    public static void saveUserHighScore(Context context, int highScore) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_USER, Context.MODE_PRIVATE);
        int oldHighScore = prefs.getInt(USER_HIGH_SCORE, -1);

        if (highScore > oldHighScore) {
            prefs.edit().putInt(USER_HIGH_SCORE, highScore).apply();
        }
    }
}
