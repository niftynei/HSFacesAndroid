package knaps.hacker.school.networking;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

    private static HSOAuthService mService = new HSOAuthService();
    private String TAG = "HSOAuthService";
    private String mAccessToken;
    private OAuthClient mClient;
    private Context mContext;

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
                (mAccessToken = getAccessTokenFromPrefs()) != null;
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

    private class GetToken extends AsyncTask<Void, Void, Boolean> {

        private final RequestCallback mCallback;
        private final OAuthClientRequest mRequest;

        public GetToken(final OAuthClientRequest request, final RequestCallback callback) {
            mRequest = request;
            mCallback = callback;
        }

        @Override
        protected Boolean doInBackground(final Void... params) {
            mClient = new OAuthClient(new URLConnectionClient());

            try {
                OAuthJSONAccessTokenResponse response = mClient.accessToken(mRequest);
                mAccessToken = response.getAccessToken();
                saveAccessToken(mAccessToken);
                return true;
            }
            catch (OAuthSystemException e) {
                Log.e(TAG, "exception loading oauth ", e);
            }
            catch (OAuthProblemException e) {
                Log.e(TAG, "exception loading oauth ", e);
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean wasSuccess) {
            if (mCallback != null) {
                if (wasSuccess) mCallback.onSuccess();
                else mCallback.onFailure();
            }
        }
    }

    private void saveAccessToken(String accessToken) {
        SharedPreferences prefs = mContext.getSharedPreferences(API_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(API_TOKEN, accessToken).apply();
    }

    private String getAccessTokenFromPrefs() {

        SharedPreferences prefs = mContext.getSharedPreferences(API_PREFS, Context.MODE_PRIVATE);
        if (prefs.contains(API_TOKEN)) {
            mService.mAccessToken = prefs.getString(API_TOKEN, null);
        }
        return mAccessToken;
    }
}
