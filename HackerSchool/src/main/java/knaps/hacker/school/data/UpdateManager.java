package knaps.hacker.school.data;

import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import knaps.hacker.school.models.Batch;
import knaps.hacker.school.models.Student;
import knaps.hacker.school.networking.RequestManager;

/**
 * Created by lisaneigut on 12 Apr 2014.
 */
public class UpdateManager {

    private static final long UPDATE_THRESHOLD = 1000 * 60 * 60 * 24 * 7;
    private final Context mContext;
    private HSDatabaseHelper mDbHelper;

    public UpdateManager(Context context, HSDatabaseHelper dbHelper) {
        mContext = context;
        mDbHelper = dbHelper;
    }

    public void checkAndUpdateDataIfNeeded() {
        // problem == i could have a zillion of these running
        if (batchesNeedUpdate()) {
            updateBatches();
            updateStudents();
        }
    }

    public boolean batchesNeedUpdate() {
        return batchNeedsUpdate(-1);
    }

    public boolean batchNeedsUpdate(long id) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        long lastUpdateDate = DatabaseUtils.longForQuery(db, HSData.Batch.SQL_LAST_BATCH_UPDATE_TIME,
                new String[] {id < 0 ? "%" : String.valueOf(id)});
        return lastUpdateDate + UPDATE_THRESHOLD < System.currentTimeMillis();
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
        HSDatabaseUtil.writeBatchesToDatabase(mDbHelper, batches);
        mContext.getContentResolver().notifyChange(HackerSchoolContentProvider.Uris.BATCHES.getUri(), null);
    }

    /**
     * Download the most recent student data, by batch
     */
    public void updateStudents() {
        long[] batchIds = HSDatabaseUtil.getBatchIds(mDbHelper);

        for (long batchId : batchIds) {
            List<Student> students = RequestManager.getService().getPeopleInBatch(batchId);
            HSDatabaseUtil.writeStudentsToDatabase(mDbHelper, students);
            mContext.getContentResolver().notifyChange(HackerSchoolContentProvider.Uris.STUDENTS.getUri(), null);
        }
    }

    /**
     * Download the most recent student data, by batch
     *
     * @param id
     */
    public void updateStudent(long id) {
        Student student = RequestManager.getService().getStudent(id);
        HSDatabaseUtil.writeStudentToDatabase(mDbHelper, student);
        mContext.getContentResolver().notifyChange(HackerSchoolContentProvider.Uris.STUDENT.getUri(id), null);
    }
}
