package knaps.hacker.school.networking;

import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.apache.oltu.oauth2.common.OAuth;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import knaps.hacker.school.utils.Constants;
import knaps.hacker.school.utils.StringUtil;
import retrofit.ErrorHandler;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by lisaneigut on 6 Apr 2014.
 */
public class RequestManager {


    private static HSApiService mService;

    public static void init() {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(Constants.HACKER_SCHOOL_URL)
                .setRequestInterceptor(new AuthInterceptor())
                .setErrorHandler(new UnauthorizedErrorHandler())
                .build();

        mService = restAdapter.create(HSApiService.class);
    }

    public static HSApiService getService() {
        if (mService == null) throw new Error("API Service not instantiated!!");
        return mService;
    }

    /**
     * This adds the OAuth token to all API requests
     */
    public static class AuthInterceptor implements RequestInterceptor {

        @Override
        public void intercept(final RequestFacade request) {
            // TODO: look at marrying OLTU with Retrofit more closely?

            // If the current access token is expired, be proactive in updating it
            if (HSOAuthService.getService().shouldRefresh()) {
                HSOAuthService.getService().refreshAccessTokenSynchronously();
            }

            if (HSOAuthService.getService().isAuthorized()) {
                final String accessToken = HSOAuthService.getService().getAccessToken();
                String bearerString = OAuth.OAUTH_HEADER_NAME + " " + accessToken;
                request.addHeader(OAuth.HeaderType.AUTHORIZATION, bearerString);

                Log.d("REtrofit", "Bearer string added" + bearerString);
            }
            else {
                Log.d("REtrofit", "Auth token not available");
            }
        }
    }

    /**
     * This is a custom Date Serializer that takes the format from the API
     * of yyyy-MM-dd and translates this automatically to an instance of the
     * Java Date type
     */
    private static class DateDeserializer implements JsonDeserializer<Date> {

        @Override
        public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            String dateString = json.getAsJsonPrimitive().getAsString();
            try {
                return StringUtil.getSimpleDateFormatter().parse(dateString);
            }
            catch (ParseException e) {
                Log.d("JSON", "Error parsing date string");
            }
            return null;
        }
    }

    /**
     * Catch all unauthorized calls. Check to see if this is because our token has been revoked.
     * If so, go and get a new token!
     */
    private static class UnauthorizedErrorHandler implements ErrorHandler {

        @Override
        public Throwable handleError(RetrofitError cause) {
            Response r = cause.getResponse();
            if (r != null & r.getStatus() == 401) {
                List<Header> headers = r.getHeaders();
                if (headers != null) {
                    for (Header header : headers) {
                        if ("Www-Authenticate".equals(header.getName()) && header.getValue().contains("invalid_token")) {
                            // go and fetch a new access token!
                            HSOAuthService.getService().refreshAccessToken(null);
                        }
                    }
                }
            }

            return cause;
        }
    }
}
