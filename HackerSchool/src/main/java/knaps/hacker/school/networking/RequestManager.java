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
import org.apache.oltu.oauth2.common.utils.OAuthUtils;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import knaps.hacker.school.utils.Constants;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
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
                .setEndpoint(Constants.HACKER_SCHOOL_URL)
                .setRequestInterceptor(new AuthInterceptor())
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
            final String accessToken = HSOAuthService.getService().getAccessToken();
            String bearerString = OAuthUtils.encodeAuthorizationBearerHeader(new HashMap<String, Object>() {{
                put("access_token", accessToken);
            }});
            request.addHeader(OAuth.OAUTH_HEADER_NAME, bearerString);
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
            SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            try {
                return dateParser.parse(dateString);
            }
            catch (ParseException e) {
                Log.d("JSON", "Error parsing date string");
            }
            return null;
        }
    }
}
