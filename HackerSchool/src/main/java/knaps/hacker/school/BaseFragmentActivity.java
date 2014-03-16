package knaps.hacker.school;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.analytics.tracking.android.EasyTracker;

import knaps.hacker.school.utils.AppUtil;
import knaps.hacker.school.views.TypefaceCache;

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
        if (!BuildConfig.DEBUG) EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!BuildConfig.DEBUG) EasyTracker.getInstance(this).activityStop(this);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setActionBarTitle(String title) {
        if (AppUtil.isHoneycomb()) {
            getActionBar().setTitle(TypefaceCache.getInstance().getTitleBarText(this, title));
        }
    }
}
