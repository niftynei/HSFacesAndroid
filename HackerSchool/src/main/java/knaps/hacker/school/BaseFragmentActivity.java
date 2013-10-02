package knaps.hacker.school;

import android.support.v4.app.FragmentActivity;

import com.google.analytics.tracking.android.EasyTracker;

/**
 * Created by lisaneigut on 20 Sep 2013.
 */
public class BaseFragmentActivity extends FragmentActivity {

    @Override
    protected void onStart() {
        super.onStart();
        if (!BuildConfig.DEBUG) EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!BuildConfig.DEBUG) EasyTracker.getInstance(this).activityStop(this);
    }
}
