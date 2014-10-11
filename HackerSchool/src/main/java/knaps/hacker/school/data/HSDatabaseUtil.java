package knaps.hacker.school.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.text.SimpleDateFormat;
import java.util.List;

import knaps.hacker.school.models.Batch;
import knaps.hacker.school.models.Student;
import knaps.hacker.school.utils.DebugUtil;
import knaps.hacker.school.utils.StringUtil;

/**
 * Created by lisaneigut on 14 Sep 2013.
 */
public class HSDatabaseUtil {

    public static void deleteAllBatches(HSDatabaseHelper dbHelper) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(HSData.Batch.TABLE_NAME, null, null);
    }

    /**
     * Must be run on background thread
     */
    protected static void writeBatchesToDatabase(HSDatabaseHelper dbHelper, final List<Batch> batches) {
        long startTime = System.currentTimeMillis();
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        final SQLiteStatement stmt = db.compileStatement(HSData.Batch.SQL_UPSERT_ALL);
        final SimpleDateFormat formatter = StringUtil.getSimpleDateFormatter();

        for (Batch batch : batches) {
            bindBatch(stmt, batch, formatter, startTime);
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        DebugUtil.d("DB _ timing", "Total time for writing to db: " + (System.currentTimeMillis() - startTime) + "ms");
    }

    public static void writeBatchToDatabase(final HSDatabaseHelper dbHelper, final Batch batch) {
        long startTime = System.currentTimeMillis();
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        final SQLiteStatement stmt = db.compileStatement(HSData.Batch.SQL_UPSERT_ALL);
        final SimpleDateFormat formatter = StringUtil.getSimpleDateFormatter();

        bindBatch(stmt, batch, formatter, startTime);

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private static void bindBatch(SQLiteStatement stmt, final Batch batch, SimpleDateFormat formatter, final long startTime) {
        stmt.bindLong(1, batch.id);
        stmt.bindString(2, batch.name);
        stmt.bindString(3, batch.startDate);
        stmt.bindString(4, batch.endDate);
        stmt.bindLong(5, startTime);
        stmt.execute();
        stmt.clearBindings();

    }

    public static void deleteAllStudentRecords(HSDatabaseHelper dbHelper) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(HSData.HSer.TABLE_NAME, null, null);
    }

    protected static void writeStudentsToDatabase(final HSDatabaseHelper dbHelper, final List<Student> students) {
        long startTime = System.currentTimeMillis();
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        final SQLiteStatement stmt = db.compileStatement(HSData.HSer.SQL_UPSERT_ALL);

        for (Student student : students) {
            bindStudent(stmt, student, startTime);
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        DebugUtil.d("DB _ timing", "Total time for writing to db: " + (System.currentTimeMillis() - startTime) + "ms");
    }

    protected static void writeStudentToDatabase(final HSDatabaseHelper dbHelper, final Student student) {
        long startTime = System.currentTimeMillis();
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        final SQLiteStatement stmt = db.compileStatement(HSData.HSer.SQL_UPSERT_ALL);

        bindStudent(stmt, student, startTime);

        db.setTransactionSuccessful();
        db.endTransaction();
        DebugUtil.d("DB _ timing", "Total time for writing to db: " + (System.currentTimeMillis() - startTime) + "ms");

    }

    private static void bindStudent(final SQLiteStatement stmt, final Student student, final long startTime) {
        stmt.bindLong(1, student.id);
        stmt.bindString(2, student.getFirstName());
        stmt.bindString(3, student.getLastName());
        stmt.bindLong(4, student.hasPhoto() ? 1 : 0);
        stmt.bindString(5, student.getImage());
        stmt.bindString(6, student.getJob());

        String[] skills = student.getSkills();
        StringBuilder builder = new StringBuilder();
        for (String skill : skills) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(skill);
        }

        stmt.bindString(7, builder.toString());
        stmt.bindString(8, student.getEmail());
        stmt.bindString(9, student.getGithub());
        stmt.bindString(10, student.getTwitter());
        stmt.bindString(11, student.getPhoneNumber());
        stmt.bindLong(12, student.isFaculty() ? 1 : 0);
        stmt.bindLong(13, student.isHackerSchooler() ? 1 : 0);
        stmt.bindLong(14, student.batchId);
        stmt.bindLong(15, startTime);
        stmt.execute();
        stmt.clearBindings();
    }

    protected static long[] getBatchIds(HSDatabaseHelper dbHelper) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.rawQuery(HSData.Batch.SQL_SINGLE_LINE_RESULT, null);

        String string = "";
        if (c.moveToFirst()) {
            string = c.getString(1);
        }

        String[] idStrings = string.split(":");
        long[] ids = new long[idStrings.length];
        for (int i = 0; i < idStrings.length; i++) {
            try {
                ids[i] = Long.parseLong(idStrings[i].trim());
            }
            catch (NumberFormatException e) {
                // problem parsing that string
            }
        }

        return ids;
    }

}
