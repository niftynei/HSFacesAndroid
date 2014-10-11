package knaps.hacker.school.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import knaps.hacker.school.models.Batch;
import knaps.hacker.school.models.Student;
import knaps.hacker.school.utils.SharedPrefsUtil;

/**
 * Created by lisaneigut on 14 Sep 2013.
 */
public class HSDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 10;
    private static final String DATABASE_NAME = "HackerSchool.db";

    public HSDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HSData.HSer.SQL_CREATE);
        db.execSQL(HSData.HSer.SQL_CREATE_ID_INDEX);
        db.execSQL(HSData.Batch.SQL_CREATE);
        db.execSQL(HSData.Batch.SQL_CREATE_ID_INDEX);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < DATABASE_VERSION) {
            db.execSQL(HSData.HSer.SQL_DELETE);
            db.execSQL(HSData.Batch.SQL_DELETE);
            onCreate(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public ArrayList<Batch> getExistingBatches() {
        final ArrayList<Batch> batches = new ArrayList<Batch>();
        // sql statement for batches
        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.query(HSData.Batch.TABLE_NAME,
                HSData.Batch.PROJECTION_ALL, null, null, null, null, HSData.Batch.SORT_DEFAULT);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                batches.add(new Batch(cursor));
            }
        }
        cursor.close();
        db.close();
        return batches;
    }

    public Student getLoggedInStudent(Context context) {
        final String userEmail = SharedPrefsUtil.getUserEmail(context);
        final SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        Student student = null;
        try {
            cursor = db.query(HSData.HSer.TABLE_NAME, HSData.HSer.PROJECTION_ALL_BATCH,
                    HSData.HSer.COLUMN_NAME_EMAIL + DbKeywords.LIKE_Q, new String[] {userEmail},
                    null, null, null, null);
            cursor.moveToFirst();
            student = new Student(cursor);
        }
        finally {
            try {
                if (cursor != null) cursor.close();
            }
            finally {
                db.close();
            }
        }
        return student;
    }
}
