package knaps.hacker.school.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashSet;

/**
 * Created by lisaneigut on 14 Sep 2013.
 */
public class HSDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "HackerSchool.db";

    public HSDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HSData.HSer.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Since we're just storing data from online, delete old table
        db.execSQL(HSData.HSer.SQL_DELETE);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public HashSet<String> getExistingBatches() {
        final HashSet<String> batches = new HashSet<String>();
        // sql statement for distinct batch names
        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.query(true, HSData.HSer.TABLE_NAME, new String[]{HSData.HSer.COLUMN_NAME_BATCH_ID}, null,
                null, HSData.HSer.COLUMN_NAME_BATCH_ID, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                batches.add(cursor.getString(
                        cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_BATCH_ID)).trim().toLowerCase());
            }
        }
        cursor.close();
        return batches;
    }

    public HashSet<String> getExistingBatchesByName() {
        final HashSet<String> batches = new HashSet<String>();
        // sql statement for distinct batch names
        final SQLiteDatabase db = getReadableDatabase();
        final Cursor cursor = db.query(true, HSData.HSer.TABLE_NAME, new String[]{HSData.HSer.COLUMN_NAME_BATCH}, null,
                null, HSData.HSer.COLUMN_NAME_BATCH, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                batches.add(cursor.getString(
                        cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_BATCH)));
            }
        }
        cursor.close();
        return batches;
    }
}
