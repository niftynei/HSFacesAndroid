package knaps.hacker.school.networking;

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

import knaps.hacker.school.utils.Constants;

/**
 * Created by lisaneigut on 24 Mar 2014.
 */
public class HSOAuthService {

    private static HSOAuthService mService = new HSOAuthService();
    private String TAG = "HSOAuthService";
    private String mAccessToken;
    private OAuthClient mClient;

    private HSOAuthService() {}

    public static HSOAuthService getService() { return mService; }

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

    public void getAccessToken(final String accessCode) {

        try {
            OAuthClientRequest request = OAuthClientRequest
                    .tokenLocation(Constants.OAUTH_TOKEN)
                    .setClientId(Constants.CLIENT_ID)
                    .setClientSecret(Constants.CLIENT_SECRET)
                    .setRedirectURI(Constants.REDIRECT_URI)
                    .setGrantType(GrantType.AUTHORIZATION_CODE)
                    .setCode(accessCode)
                    .buildBodyMessage();
            new GetToken(request).execute();
        }
        catch (OAuthSystemException e) {
            Log.e(TAG, "exception loading oauth ", e);
        }
    }

    public void makeARequest() throws OAuthSystemException, OAuthProblemException {
        OAuthClientRequest bearerRequest = new OAuthBearerClientRequest(Constants.ME_ENDPOINT)
                .setAccessToken(mAccessToken)
                .buildQueryMessage();

        OAuthResourceResponse response = mClient.resource(bearerRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);

        response.getBody();
        response.getResponseCode();
    }

    private class GetToken extends AsyncTask<Void, Void, Void> {

        private OAuthClientRequest mRequest;

        public GetToken(final OAuthClientRequest request) {
            mRequest = request;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            mClient = new OAuthClient(new URLConnectionClient());

            try {
                OAuthJSONAccessTokenResponse response = mClient.accessToken(mRequest);
                // todo: save to shared prefs
                mAccessToken = response.getAccessToken();
                long expiresIn = response.getExpiresIn();
                makeARequest();
            }
            catch (OAuthSystemException e) {
                Log.e(TAG, "exception loading oauth ", e);
            }
            catch (OAuthProblemException e) {
                Log.e(TAG, "exception loading oauth ", e);
            }

            return null;
        }
    }

}
