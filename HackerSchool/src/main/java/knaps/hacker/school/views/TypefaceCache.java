package knaps.hacker.school.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by lisaneigut on 15 Sep 2013.
 */
public class TypefaceCache {
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
                    Typeface t = Typeface.createFromAsset(context.getAssets(),
                            file);
                    mCache.put(file, t);
                } catch (Exception e) {
                    Log.e("Error", "Could not get typeface '" + file
                            + "' because " + e.getMessage());
                    return null;
                }
            }
            return mCache.get(file);
        }
    }
}
