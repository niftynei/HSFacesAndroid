package knaps.hacker.school.utils;

import knaps.hacker.school.BuildConfig;
import knaps.hacker.school.R;

/**
 * Created by lisaneigut on 15 Sep 2013.
 */
public final class Constants {

    private Constants() {}

    public static final String ACTION_LOADING_START = "knaps.hacker.school.LOADING_START";
    public static final String ACTION_LOADING_ENDED = "knaps.hacker.school.LOADING_ENDED";

    public final static String HACKER_SCHOOL_URL = "https://www.recurse.com";

    public static final String OAUTH = "/oauth";
    public final static String OAUTH_AUTHORIZE = HACKER_SCHOOL_URL + OAUTH + "/authorize";
    public static final String OAUTH_TOKEN = HACKER_SCHOOL_URL + OAUTH + "/token";
    public static final String REDIRECT_URI = "hsfaces://oauth";
    public static final String CLIENT_SECRET = BuildConfig.API_SECRET;
    public static final String CLIENT_ID = BuildConfig.CLIENT_ID;

    public static final String API_ENDPOINT = "/api/v1";
    public static final String PEOPLE = "/people";
    public static final String ME = "/me";
    public static final String ME_ENDPOINT = HACKER_SCHOOL_URL + API_ENDPOINT + PEOPLE + ME;

    public static final String GITHUB_URL = "http://www.github.com";
    public static final String TWITTER_URL = "http://www.twitter.com";

    public final static String LOGIN_PAGE = "/sessions";
    public static final String STUDENT = "student";
    public static final String GAME_MAX = "game_count";
    public static final String BATCH_ID = "batch_id";
    public static final String RUNTIME_STRING = "Run Time";
    public static final String BATCH_STRING = "Batch";
    public static final int INVALID_MIN = -1;
    public static int[] sLoadingIcons = new int[] {R.drawable.ic_castle, R.drawable.ic_chalice, R.drawable.ic_identicon};
}
