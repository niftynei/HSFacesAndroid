package knaps.hacker.school;

import android.app.Application;

import knaps.hacker.school.networking.RequestManager;

/**
 * Created by lisaneigut on 6 Apr 2014.
 */
public class HSFacesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RequestManager.init();
    }
}
