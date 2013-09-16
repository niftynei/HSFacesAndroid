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
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import knaps.hacker.school.R;

/**
 * Created by lisaneigut on 15 Sep 2013.
 */
public class ImageDownloads {

    private static final int IO_BUFFER_SIZE = 4 * 1024;
    private static LruCache<String, Bitmap> sMemoryCache;

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
            bitmap = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static LruCache<String, Bitmap> getBitmapMemoryCache() {
        if (sMemoryCache == null) {
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            final int cacheSize = maxMemory / 8;
            sMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount() / 1024;
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

    public static class HSImageDownloadTask extends AsyncTask<Void, Void, Bitmap> {

        final private String mUrl;
        final private ImageView mImageView;
        final private Activity mContext;

        public HSImageDownloadTask(String url, ImageView imageView, Activity context) {
            mUrl = url;
            mImageView = imageView;
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            mImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher));
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = null;
            bitmap = getBitmapMemoryCache().get(mUrl);

            if (bitmap == null) {
                if (isOnline(mContext)) {
                    bitmap = ImageDownloads.loadBitmap(Constants.HACKER_SCHOOL_URL + mUrl);
                    new ImageDownloads.SaveToCacheTask(getBitmapMemoryCache(), bitmap, mUrl).execute();
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mContext != null) {

                if (bitmap != null && mImageView != null) {
                    mImageView.setImageBitmap(bitmap);
                }
                else if (mImageView != null) {
                    mImageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher));
                    Toast.makeText(mContext, "Error loading image.", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
}
