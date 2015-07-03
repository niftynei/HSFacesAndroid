package knaps.hacker.school;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import knaps.hacker.school.networking.HSOAuthService;
import knaps.hacker.school.networking.RequestManager;

/**
 * Application entry point
 * Created by lisaneigut on 6 Apr 2014.
 */
public class HSFacesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());
        RequestManager.init();
        HSOAuthService.init(this);
    }
}
