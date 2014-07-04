package knaps.hacker.school.data;

import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.List;

import knaps.hacker.school.models.Batch;
import knaps.hacker.school.models.Student;
import knaps.hacker.school.networking.RequestManager;
import retrofit.RetrofitError;

/**
 * Created by lisaneigut on 12 Apr 2014.
 */
public class UpdateManager {

    private static final long UPDATE_THRESHOLD = 1000 * 60 * 60 * 24 * 7; // a week
    private final Context mContext;
    private HSDatabaseHelper mDbHelper;

    public UpdateManager(Context context, HSDatabaseHelper dbHelper) {
        mContext = context;
        mDbHelper = dbHelper;
    }

    public void checkAndUpdateDataIfNeeded() {
        // problem ==> i could have a zillion of these running
        Runnable runner = new Runnable() {
            @Override
            public void run() {
                try {
                    if (batchesEmpty() || batchesNeedUpdate() || studentsEmpty()) {
                        updateBatches();
                        updateStudents();
                    }
                    else {
                        // return a count of all the student records
                        long count = DatabaseUtils.longForQuery(mDbHelper.getReadableDatabase(), HSData.HSer.SQL_RECORD_COUNT, null);
                        Log.d("TAG", "Count of all student records " + count);
                    }
                }
                catch (RetrofitError error) {
                    if (error != null) Log.e("ERRORS RETRO", error.getResponse().getReason(), error);
                    else Log.e("ERRORS RETRO!!", "Dunno what happened. Error came back nullio");
                }
                catch (Exception e) {
                    // catch all the exceptions
                    Log.e("ERRORS", "all the errors", e);
                }
            }
        };
        runner.run();
    }

    private boolean batchesEmpty() {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        long count = DatabaseUtils.longForQuery(db, HSData.Batch.SQL_RECORD_COUNT, null);
        Log.d("COUNT", "batches database count " + count);
        return count <= 0;
    }

    private boolean studentsEmpty() {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        long count = DatabaseUtils.longForQuery(db, HSData.HSer.SQL_RECORD_COUNT, null);
        Log.d("COUNT", "student database count " + count);
        return count <= 0;
    }

    public boolean batchesNeedUpdate() {
        return batchNeedsUpdate(-1);
    }

    public boolean batchNeedsUpdate(long id) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        try {
            long lastUpdateDate = DatabaseUtils.longForQuery(db, HSData.Batch.SQL_LAST_BATCH_UPDATE_TIME,
                    new String[] {id < 0 ? "%" : String.valueOf(id)});
            Log.d("COUNT", "database last updated " + DateUtils.formatDateRange(mContext, lastUpdateDate, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME));
            return lastUpdateDate + UPDATE_THRESHOLD < System.currentTimeMillis();
        }
        catch (SQLiteDoneException ex) {
            // no value returned
            Log.d("COUNT", "no value returned for batch need update query", ex);
        }
        return true;
    }

    /**
     * Download and update a single batch
     *
     * @param id
     */
    public void updateBatch(long id) {
        Batch batch = RequestManager.getService().getBatch(id);
        HSDatabaseUtil.writeBatchToDatabase(mDbHelper, batch);
        mContext.getContentResolver().notifyChange(HackerSchoolContentProvider.Uris.BATCH.getUri(id), null);
    }

    /**
     * Download the most recent batches and update the database
     */
    public void updateBatches() {
        List<Batch> batches = RequestManager.getService().getBatches();

        if (batches.size() > 0) {
            HSDatabaseUtil.deleteAllBatches(mDbHelper);
            HSDatabaseUtil.writeBatchesToDatabase(mDbHelper, batches);
            mContext.getContentResolver().notifyChange(HackerSchoolContentProvider.Uris.BATCHES.getUri(), null);
        }
    }

    /**
     * Download the most recent student data, by batch
     */
    public void updateStudents() {
        long[] batchIds = HSDatabaseUtil.getBatchIds(mDbHelper);

        // delete all the students
        HSDatabaseUtil.deleteAllStudentRecords(mDbHelper);

        for (long batchId : batchIds) {
            List<Student> students = RequestManager.getService().getPeopleInBatch(batchId);
            HSDatabaseUtil.writeStudentsToDatabase(mDbHelper, students);
            mContext.getContentResolver().notifyChange(HackerSchoolContentProvider.Uris.STUDENTS.getUri(), null);
        }
    }

    /**
     * Download the most recent student data
     *
     * @param id
     */
    public void updateStudent(long id) {
        Student student = RequestManager.getService().getStudent(id);
        HSDatabaseUtil.writeStudentToDatabase(mDbHelper, student);
        mContext.getContentResolver().notifyChange(HackerSchoolContentProvider.Uris.STUDENT.getUri(id), null);
    }
}
