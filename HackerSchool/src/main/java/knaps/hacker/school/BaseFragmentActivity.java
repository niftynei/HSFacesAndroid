package knaps.hacker.school;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.analytics.GoogleAnalytics;


/**
 * Created by lisaneigut on 20 Sep 2013.
 */
public class BaseFragmentActivity extends FragmentActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!BuildConfig.DEBUG) {
            GoogleAnalytics.getInstance(this).reportActivityStart(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!BuildConfig.DEBUG) {
            GoogleAnalytics.getInstance(this).reportActivityStop(this);
        }
    }

}
