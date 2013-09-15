package knaps.hacker.school.networking;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by lisaneigut on 15 Sep 2013.
 */
public class ImageDownloads {

    private static final int IO_BUFFER_SIZE = 4 * 1024;
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
            cache.put(key, bitmap);
            return null;
        }
    }
}
