package knaps.hacker.school.views;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import java.util.HashMap;

import knaps.hacker.school.utils.DebugUtil;

/**
 * Created by lisaneigut on 15 Sep 2013.
 */
public class TypefaceCache {

    public static final String NEW_RELIC = "new-relic.ttf";
    private final HashMap<String, Typeface> mCache;

    private TypefaceCache() {
        mCache = new HashMap<String, Typeface>();
    }

    private static TypefaceCache mInstance = new TypefaceCache();

    public static TypefaceCache getInstance() {
        return mInstance;
    }

    public Typeface getTypeface(Context context, String file) {
        synchronized (mCache) {
            if (!mCache.containsKey(file)) {
                try {
                    Typeface t = Typeface.createFromAsset(context.getAssets(), file);
                    mCache.put(file, t);
                }
                catch (Exception e) {
                    DebugUtil.e("Error", "Could not get typeface '" + file + "' because " + e.getMessage());
                    return null;
                }
            }
            return mCache.get(file);
        }
    }

    public SpannableString getTitleBarText(Context context, String title) {
        SpannableString string = new SpannableString(title);
        string.setSpan(new TypefaceSpan(getTypeface(context, NEW_RELIC)), 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return string;
    }

    private static class TypefaceSpan extends MetricAffectingSpan {

        private Typeface mTypeface;
        public TypefaceSpan(Typeface typeface) {
            mTypeface = typeface;
        }

        @Override
        public void updateMeasureState(final TextPaint p) {
            p.setTypeface(mTypeface);
            p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }

        @Override
        public void updateDrawState(final TextPaint p) {
            p.setTypeface(mTypeface);
            p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }
    }
}
