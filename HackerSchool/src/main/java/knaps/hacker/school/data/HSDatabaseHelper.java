package knaps.hacker.school.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import knaps.hacker.school.models.Student;
import knaps.hacker.school.utils.SharedPrefsUtil;

/**
 * Created by lisaneigut on 14 Sep 2013.
 */
public class HSDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "HackerSchool.db";

    public HSDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HSData.HSer.SQL_CREATE);
        db.execSQL(HSData.HSer.SQL_CREATE_ID_INDEX);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            db.execSQL(HSData.HSer.SQL_DELETE);
            onCreate(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public ArrayList<String> getExistingBatches() {
        final ArrayList<String> batches = new ArrayList<String>();
        // sql statement for distinct batch names
        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.query(true, HSData.HSer.TABLE_NAME,
                new String[] {HSData.HSer.COLUMN_NAME_BATCH_ID}, null,
                null, HSData.HSer.COLUMN_NAME_BATCH_ID, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                batches.add(cursor.getString(
                        cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_BATCH_ID)).trim()
                                  .toLowerCase());
            }
        }
        cursor.close();
        db.close();
        return batches;
    }

    public ArrayList<String> getExistingBatchesByName() {
        final ArrayList<String> batches = new ArrayList<String>();
        // sql statement for distinct batch names
        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db
                .query(true, HSData.HSer.TABLE_NAME, new String[] {HSData.HSer.COLUMN_NAME_BATCH},
                        null,
                        null, HSData.HSer.COLUMN_NAME_BATCH, null, HSData.HSer.SORT_BATCH, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                batches.add(cursor.getString(
                        cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_BATCH)));
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
            cursor = db.query(HSData.HSer.TABLE_NAME, HSData.HSer.PROJECTION_ALL,
                    HSData.HSer.COLUMN_NAME_EMAIL + HSData.STMT_LIKE_Q, new String[] {userEmail},
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
