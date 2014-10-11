package knaps.hacker.school.networking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import knaps.hacker.school.HSActivity;
import knaps.hacker.school.models.Student;
import knaps.hacker.school.utils.Constants;
import knaps.hacker.school.utils.SharedPrefsUtil;
import retrofit.RetrofitError;

/**
 * Created by lisaneigut on 24 Mar 2014.
 */
public class HSOAuthService {

    private static final String API_PREFS = "game_state";
    private static final String API_TOKEN = "game_hash";
    private static final String API_EXPIRES_AT = "game_start";
    private static final String REFRESH_TOKEN = "game_key";

    private static HSOAuthService mService = new HSOAuthService();
    private String TAG = "HSOAuthService";
    private String mAccessToken;
    private OAuthClient mClient;
    private Context mContext;
    private long mExpiresAt;

    public interface RequestCallback {
        public void onSuccess();
        public void onFailure();
    }

    private HSOAuthService() {}

    public static void init(Context context) {
        mService.mContext = context;
        mService.getAccessTokenFromPrefs();
    }

    public static HSOAuthService getService() { return mService; }

    public boolean isAuthorized() {
        return mAccessToken != null ||
                (mAccessToken = getAccessTokenFromPrefs()) != null &&
                    !shouldRefresh();
    }

    public String getAccessToken() {
        if (mAccessToken != null) {
            return mAccessToken;
        }
        else if ((mAccessToken = getAccessTokenFromPrefs()) != null) {
            return mAccessToken;
        }

        throw new IllegalStateException("no access token is available yet");
    }

    public String getAuthUrl() {

        OAuthClientRequest request = null;
        try {
            request = OAuthClientRequest
                    .authorizationLocation(Constants.OAUTH_AUTHORIZE)
                    .setClientId(Constants.CLIENT_ID)
                    .setResponseType("code")
                    .setRedirectURI(Constants.REDIRECT_URI)
                    .buildQueryMessage();
        }
        catch (OAuthSystemException e) {
            Log.e(TAG, "exception loading oauth ", e);
        }

        return request.getLocationUri();
    }

    public void getAccessToken(final String accessCode, RequestCallback callback) {

        try {
            OAuthClientRequest request = OAuthClientRequest
                    .tokenLocation(Constants.OAUTH_TOKEN)
                    .setClientId(Constants.CLIENT_ID)
                    .setClientSecret(Constants.CLIENT_SECRET)
                    .setRedirectURI(Constants.REDIRECT_URI)
                    .setGrantType(GrantType.AUTHORIZATION_CODE)
                    .setCode(accessCode)
                    .buildBodyMessage();
            new GetToken(request, callback).execute();
        }
        catch (OAuthSystemException e) {
            Log.e(TAG, "exception loading oauth ", e);
        }
    }


    /**
     * Using documentation from Google OAuth2 docs
     * https://developers.google.com/accounts/docs/OAuth2WebServer#refresh
     */
    public void refreshAccessToken(RequestCallback callback) {
        // first, delete out the old access token
        mAccessToken = null;
        mExpiresAt = 0;
        saveAccessToken(null, 0);

        try {
            OAuthClientRequest request = OAuthClientRequest
                    .tokenLocation(Constants.OAUTH_TOKEN)
                    .setClientId(Constants.CLIENT_ID)
                    .setClientSecret(Constants.CLIENT_SECRET)
                    .setRefreshToken(getRefreshTokenFromPrefs())
                    .setGrantType(GrantType.REFRESH_TOKEN)
                    .buildBodyMessage();
            new GetToken(request, callback).execute();
        }
        catch (OAuthSystemException e) {
            Log.e(TAG, "exception loading oauth ", e);
        }
    }

    public void refreshAccessTokenSynchronously() {
        // first, delete out the old access token
        mAccessToken = null;
        mExpiresAt = 0;
        saveAccessToken(null, 0);

        try {
            OAuthClientRequest request = OAuthClientRequest
                    .tokenLocation(Constants.OAUTH_TOKEN)
                    .setClientId(Constants.CLIENT_ID)
                    .setClientSecret(Constants.CLIENT_SECRET)
                    .setRefreshToken(getRefreshTokenFromPrefs())
                    .setGrantType(GrantType.REFRESH_TOKEN)
                    .buildBodyMessage();
            makeOauthRequest(request);
        }
        catch (OAuthSystemException e) {
            Log.e(TAG, "exception loading oauth ", e);
        }
    }

    private class GetToken extends AsyncTask<Void, Void, Boolean> {

        private final RequestCallback mCallback;
        private final OAuthClientRequest mRequest;

        public GetToken(final OAuthClientRequest request, final RequestCallback callback) {
            mRequest = request;
            mCallback = callback;
        }

        @Override
        protected Boolean doInBackground(final Void... params) {
            return makeOauthRequest(mRequest);
        }

        @Override
        protected void onPostExecute(final Boolean wasSuccess) {
            if (mCallback != null) {
                if (wasSuccess) {
                    mCallback.onSuccess();
                }
                else {
                    mCallback.onFailure();
                }
            }
        }
    }

    private boolean makeOauthRequest(OAuthClientRequest request) {
        mClient = new OAuthClient(new URLConnectionClient());

        try {
            OAuthJSONAccessTokenResponse response = mClient.accessToken(request);

            mAccessToken = response.getAccessToken();
            String refreshToken = response.getRefreshToken();
            long expriesAt = response.getExpiresIn() * DateUtils.SECOND_IN_MILLIS;

            saveAccessToken(mAccessToken, expriesAt);

            if (!TextUtils.isEmpty(refreshToken)) {
                saveRefreshToken(refreshToken);
            }
            return true;
        }
        catch (OAuthSystemException e) {
            Log.e(TAG, "exception loading oauth ", e);
        }
        catch (OAuthProblemException e) {
            if ("invalid_request".equals(e.getError())) {
                // something went horribly wrong. probably revoked auth access on the server.
                clearAllTokens();
                forceLogout();
            }
            Log.e(TAG, "exception loading oauth ", e);
        }

        return false;
    }

    private void forceLogout() {
        Intent intent = new Intent(mContext, HSActivity.class);
        // clear the backstack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public boolean shouldRefresh() {
        if (mExpiresAt == 0) {
            SharedPreferences prefs = mContext.getSharedPreferences(API_PREFS, Context.MODE_PRIVATE);
            if (prefs.contains(API_EXPIRES_AT)) {
                mExpiresAt = prefs.getLong(API_EXPIRES_AT, System.currentTimeMillis());
            }
        }
        return mExpiresAt <= System.currentTimeMillis();
    }

    private String getRefreshTokenFromPrefs() {
        SharedPreferences prefs = mContext.getSharedPreferences(API_PREFS, Context.MODE_PRIVATE);
        if (prefs.contains(REFRESH_TOKEN)) {
            return prefs.getString(REFRESH_TOKEN, "");
        }
        return "";
    }

    private void saveRefreshToken(String refreshToken) {
        SharedPreferences prefs = mContext.getSharedPreferences(API_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    private void saveAccessToken(String accessToken, long expiresAt) {
        SharedPreferences prefs = mContext.getSharedPreferences(API_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(API_EXPIRES_AT, System.currentTimeMillis() + expiresAt);
        editor.putString(API_TOKEN, accessToken)
                .apply();
    }

    private String getAccessTokenFromPrefs() {
        SharedPreferences prefs = mContext.getSharedPreferences(API_PREFS, Context.MODE_PRIVATE);
        if (prefs.contains(API_TOKEN)) {
            mService.mAccessToken = prefs.getString(API_TOKEN, null);
            mExpiresAt = prefs.getLong(API_EXPIRES_AT, System.currentTimeMillis());
        }
        return mAccessToken;
    }

    private void clearAllTokens() {
        SharedPreferences prefs = mContext.getSharedPreferences(API_PREFS, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}
