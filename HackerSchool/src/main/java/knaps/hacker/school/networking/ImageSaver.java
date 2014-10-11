package knaps.hacker.school.networking;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.*;

import knaps.hacker.school.data.HSData;
import knaps.hacker.school.data.HSDatabaseHelper;
import knaps.hacker.school.utils.DebugUtil;

/**
 * Created by lisaneigut on 16 Sep 2013.
 */
public class ImageSaver {


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state) ||
                Environment.MEDIA_MOUNTED.equals(state));
    }

    // get a file handle for writing to cache stuff
    public File getPrivatePicturesExternalStorage(final Context context, final String filename) {
        final File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename);
        if (!file.mkdirs()) {
            DebugUtil.e("Error", "Directory not created!");
        }
        return file;
    }

    public File getPicturesInternalStorage(final Context context, final String filename) {
        final File file = new File(context.getFilesDir(), filename);
        if (!file.getParentFile().mkdirs()) {
            DebugUtil.e("Error!", "Directory not created!!");
        }
        return file;
    }

    public String getFilenameInternalStorage(final Context context, final String filename) {
        return context.getFilesDir() + File.separator + filename;
    }

    public boolean writeBitmapToFile(final Context context, final Bitmap bitmap, final String filename) {
        if (context == null || bitmap == null || filename == null) return false;
        // let's just use internal storage. Makes life easier.
        //        if (isExternalStorageWritable()) {
        //           file = getPrivatePicturesExternalStorage(context, filename);
        //        } else {
        final File file = getPicturesInternalStorage(context, filename);
        //        }

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
        final byte[] bitmapdata = bos.toByteArray();

        final InputStream is = new ByteArrayInputStream(bitmapdata);
        return writeStreamToDisk(file, is);
    }

    public boolean writeStreamToDisk(File file, InputStream is) {
        final byte[] buffer = new byte[8 * 1024];
        boolean success = false;
        OutputStream output = null;
        try {
            int bytesRead;
            output = new FileOutputStream(file);
            while ((bytesRead = is.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            success = true;
        }
        catch (IOException e) {
            DebugUtil.e("Error!", "Unable to write to file", e);
        }
        finally {
            try {
                if (output != null) {
                    output.close();
                }
            }
            catch (IOException e) {
                DebugUtil.e("Error!", "Unable to write to file", e);
            }
            try {
                is.close();
            }
            catch (IOException e) {
                DebugUtil.e("Error!", "Unable to write to file", e);
            }
        }
        return success;
    }

    public Bitmap getBitmapFromFile(final Context context, final String filename) {
        String fullFilename = context.getFilesDir() + File.separator + filename;
        return BitmapFactory.decodeFile(fullFilename);
    }

    public static String getFilenameFromImageUrl(String imageUrl) {
        final String search = "150-";
        if (imageUrl.lastIndexOf(".jpg") == -1) {
            imageUrl = imageUrl + ".jpg";
        }
        final int index = imageUrl.indexOf(search);
        return imageUrl.substring(index + search.length());
    }

    public boolean saveFilenameToDatabase(Context context, String filename, String url) {
        final SQLiteDatabase db = new HSDatabaseHelper(context).getWritableDatabase();

        final ContentValues values = new ContentValues(1);
        values.put(HSData.HSer.COLUMN_NAME_IMAGE_FILENAME, filename);

        int rowsAffected = db.update(HSData.HSer.TABLE_NAME, // table name
                values, // values to update
                HSData.HSer.COLUMN_NAME_IMAGE_URL + " = ?", // where
                new String[] {url}
        );

        db.close();
        return rowsAffected != 0;
    }

    public static String databaseHasImage(Context context, String url) {
        final SQLiteDatabase db = new HSDatabaseHelper(context).getWritableDatabase();
        final String response = DatabaseUtils.stringForQuery(db, HSData.HSer.SQL_GET_FILENAME,
                new String[] { "%" + url + "%"});
        db.close();
        return response;
    }
}
