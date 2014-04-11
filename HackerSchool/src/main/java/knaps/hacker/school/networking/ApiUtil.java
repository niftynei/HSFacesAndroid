package knaps.hacker.school.networking;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import knaps.hacker.school.data.HSDatabaseUtil;
import knaps.hacker.school.models.Batch;
import knaps.hacker.school.models.Student;

/**
 * Created by lisaneigut on 11 Apr 2014.
 */
public class ApiUtil {

    // TODO: i need a content provider's auto observer

    /**
     * Download the most recent batches and update the database
     */
    public static void updateBatches(final Context context) {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                List<Batch> batches = RequestManager.getService().getBatches();
                HSDatabaseUtil.writeBatchesToDatabase(context, batches);
                return null;
            }
        };

        task.execute();
    }

    public static void updateStudents(final Context context) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                // for each batch in batches...
                long[] batchIds = HSDatabaseUtil.getBatchIds(context);

                for (long batchId : batchIds) {
                    List<Student> students = RequestManager.getService().getPeopleInBatch(batchId);
                    HSDatabaseUtil.writeStudentsToDatabase(context, students);
                }

                return null;
            }
        };

        task.execute();

    }
}
