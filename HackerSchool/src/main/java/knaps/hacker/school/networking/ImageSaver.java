package knaps.hacker.school.networking;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import knaps.hacker.school.data.HSDataContract;
import knaps.hacker.school.data.HSDatabaseHelper;

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
        final File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), filename);
        if (!file.mkdirs()) {
            Log.e("Error", "Directory not created!");
        }
        return file;
    }

    public File getPicturesInternalStorage(final Context context, final String filename) {
        final File file = new File(context.getFilesDir(), filename);
        if (!file.getParentFile().mkdirs()) {
            Log.e("Error!", "Directory not created!!");
        }
        return file;
    }

    public String getFilenameInternalStorage(final Context context, final String filename) {
        return context.getFilesDir() + File.separator + filename;
    }

    public boolean writeBitmapToFile(final Context context, final Bitmap bitmap, final String filename) {
        if (context == null || bitmap == null || filename == null) return false;

        boolean success = false;
        // let's just use internal storage. Makes life easier.
//        if (isExternalStorageWritable()) {
//           file = getPrivatePicturesExternalStorage(context, filename);
//        } else {
        final File file = getPicturesInternalStorage(context, filename);
//        }

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        final byte[] bitmapdata = bos.toByteArray();

        final byte[] buffer = new byte[8 * 1024];
        final InputStream is = new ByteArrayInputStream(bitmapdata);
        OutputStream output = null;
        try {
            int bytesRead;
            output = new FileOutputStream(file);
            while((bytesRead = is.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            success = true;
        } catch (IOException e) {
            Log.e("Error!", "Unable to write to file", e);
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                Log.e("Error!", "Unable to write to file", e);
            }
            try {
                is.close();
            } catch (IOException e) {
                Log.e("Error!", "Unable to write to file", e);
            }
        }
        return success;
    }

    public Bitmap getBitmapFromFile(final Context context, final String filename) {
        String fullFilename = context.getFilesDir() + File.separator + filename;
        return BitmapFactory.decodeFile(fullFilename);
    }

    public String getFilenameFromImageUrl(String imageUrl) {
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
        values.put(HSDataContract.StudentEntry.COLUMN_NAME_IMAGE_FILENAME, filename);

        int rowsAffected = db.update(HSDataContract.StudentEntry.TABLE_NAME, // table name
                values, // values to update
                HSDataContract.StudentEntry.COLUMN_NAME_IMAGE_URL + " = ?", // where
                new String[]{url}
        );

        return rowsAffected != 0;
    }

    public static String databaseHasImage(Context context, String url) {
        final SQLiteDatabase db = new HSDatabaseHelper(context).getWritableDatabase();
        return DatabaseUtils.stringForQuery(db, HSDataContract.StudentEntry.SQL_GET_FILENAME,
                new String[]{url});
    }
}
