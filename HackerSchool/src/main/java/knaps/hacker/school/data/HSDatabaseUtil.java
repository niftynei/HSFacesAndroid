package knaps.hacker.school.data;

import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.List;

import knaps.hacker.school.models.Batch;
import knaps.hacker.school.models.Student;
import knaps.hacker.school.utils.StringUtil;

/**
 * Created by lisaneigut on 14 Sep 2013.
 */
public class HSDatabaseUtil {

    private static final long UPDATE_THRESHOLD = 1000 * 60 * 60 * 24 * 7;

    /**
     * Must be run on background thread
     */
    public static boolean batchesNeedUpdate(Context context) {
        final HSDatabaseHelper mDbHelper = new HSDatabaseHelper(context);
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long lastUpdateDate = DatabaseUtils.longForQuery(db, "query", new String[] {"selectionargs"});
        return lastUpdateDate + UPDATE_THRESHOLD < System.currentTimeMillis();
    }

    public static void writeBatchesToDatabase(final Context context, final List<Batch> batches) {
        long startTime = System.currentTimeMillis();
        final HSDatabaseHelper mDbHelper = new HSDatabaseHelper(context);
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();

        final SQLiteStatement stmt = db.compileStatement(HSData.Batch.SQL_UPSERT_ALL);
        final SimpleDateFormat formatter = StringUtil.getSimpleDateFormatter();

        for (Batch batch : batches) {
            stmt.bindLong(1, batch.id);
            stmt.bindString(2, batch.name);
            stmt.bindString(3, formatter.format(batch.startDate));
            stmt.bindString(4, formatter.format(batch.endDate));
            stmt.bindLong(5, startTime);
            stmt.execute();
            stmt.clearBindings();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        Log.d("DB _ timing",
                "Total time for writing to db: " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public static void writeStudentsToDatabase(final Context context, final List<Student> students) {
        long startTime = System.currentTimeMillis();
        final HSDatabaseHelper mDbHelper = new HSDatabaseHelper(context);
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        final SQLiteStatement stmt = db.compileStatement(HSData.HSer.SQL_UPSERT_ALL);

        for (Student student : students) {
            stmt.bindLong(1, student.id);
            stmt.bindString(2, student.firstName);
            stmt.bindString(3, student.lastName);
            stmt.bindString(4, student.image);
            stmt.bindString(5, student.mJob);
            stmt.bindString(6, student.mJobUrl);
            stmt.bindString(7, student.mSkills);
            stmt.bindString(8, student.email);
            stmt.bindString(9, student.github);
            stmt.bindString(10, student.twitter);
            stmt.bindLong(11, student.batch.id);
            stmt.bindLong(12, startTime);
            stmt.execute();
            stmt.clearBindings();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        Log.d("DB _ timing",
                "Total time for writing to db: " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public static long[] getBatchIds(final Context context) {
        // get the stuff things
        return new long[0];
    }
}
