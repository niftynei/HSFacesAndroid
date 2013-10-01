package knaps.hacker.school.networking;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import knaps.hacker.school.utils.AppUtil;
import knaps.hacker.school.utils.Constants;

/**
 * Created by lisaneigut on 15 Sep 2013.
 */
public class ImageDownloads {

    private static final int IO_BUFFER_SIZE = 4 * 1024;
    private static LruCache<String, Bitmap> sMemoryCache;

    public interface ImageDownloadCallback {
        public void onPreImageDownload();

        public void onImageDownloaded(Bitmap bitmap);

        public void onImageFailed();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static Bitmap loadBitmap(String url) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
        }
        catch (MalformedURLException e) {
            Log.e("Error!", "Unable to write to file", e);
        }
        catch (IOException e) {
            Log.e("Error!", "Unable to write to file", e);
        }
        return bitmap;
    }

    public static LruCache<String, Bitmap> getBitmapMemoryCache() {
        if (sMemoryCache == null) {
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            int divisor = !AppUtil.isHoneycomb() ? 16 : 8;
            final int cacheSize = maxMemory / divisor;
            sMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return (int) (getSizeInBytes(bitmap) / 1024);
                }

                private long getSizeInBytes(final Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
            };

        }
        return sMemoryCache;
    }


    public static class SaveToCacheTask extends AsyncTask<Void, Void, Void> {

        private LruCache<String, Bitmap> cache;
        private Bitmap bitmap;
        private String key;

        public SaveToCacheTask(LruCache<String, Bitmap> cache, Bitmap bitmap, String key) {
            this.cache = cache;
            this.bitmap = bitmap;
            this.key = key;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (key != null && bitmap != null) cache.put(key, bitmap);
            return null;
        }
    }

    public static class SaveToDisk extends AsyncTask<Void, Void, Void> {

        private final String mUrl;
        private final Context mContext;
        private final Bitmap mBitmap;

        public SaveToDisk(Context context, String url, Bitmap bitmap) {
            mUrl = url;
            mContext = context;
            mBitmap = bitmap;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ImageSaver saver = new ImageSaver();

            final String filename = saver.getFilenameFromImageUrl(mUrl);
            boolean saved = saver.writeBitmapToFile(mContext, mBitmap, filename);
            if (saved) {
                saver.saveFilenameToDatabase(mContext, filename, mUrl);
            }
            return null;
        }
    }

    public static class RetainFragment extends Fragment {
        private static final String TAG = "RetainFragment";
        public LruCache<String, Bitmap> mRetainedCache;

        public RetainFragment() {}

        public static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
            RetainFragment fragment = (RetainFragment) fm.findFragmentByTag(TAG);
            if (fragment == null) {
                fragment = new RetainFragment();
            }
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }
    }

    public static class HSGetImageTask extends AsyncTask<Void, Void, Bitmap> {

        final private String mUrl;
        final private Activity mContext;
        final private ImageDownloadCallback mCallbacks;

        public HSGetImageTask(String url, Activity context, ImageDownloadCallback callback) {
            mUrl = url;
            mContext = context;
            mCallbacks = callback;
        }

        @Override
        protected void onPreExecute() {
            if (mCallbacks != null) mCallbacks.onPreImageDownload();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap;
            bitmap = getBitmapMemoryCache().get(mUrl);

            if (bitmap == null) {
                final String filename = ImageSaver.databaseHasImage(mContext, mUrl);
                if (filename != null) {
                    final ImageSaver saver = new ImageSaver();
                    bitmap = saver.getBitmapFromFile(mContext, filename);
                }
                if (isOnline(mContext) && bitmap == null) {
                    bitmap = ImageDownloads.loadBitmap(Constants.HACKER_SCHOOL_URL + mUrl);
                    new SaveToDisk(mContext, mUrl, bitmap).execute();
                }

                if (bitmap != null)
                    new SaveToCacheTask(getBitmapMemoryCache(), bitmap, mUrl).execute();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mCallbacks == null) return;

            if (bitmap != null) {
                mCallbacks.onImageDownloaded(bitmap);
            }
            else {
                mCallbacks.onImageFailed();
            }

        }
    }
}
