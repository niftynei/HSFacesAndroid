package knaps.hacker.school.models;

import android.database.Cursor;

import java.text.ParseException;
import java.util.Date;

import knaps.hacker.school.data.HSData;
import knaps.hacker.school.utils.StringUtil;

/**
 * Created by lisaneigut on 6 Apr 2014.
 */
public class Batch {
    public long id;
    public String name;
    public Date startDate;
    public Date endDate;

    public Batch() {}

    public Batch(final Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(HSData.Batch.COLUMN_NAME_ID));
        name = cursor.getString(cursor.getColumnIndex(HSData.Batch.COLUMN_NAME_NAME));

        // need to parse these out specially.  Save them in the same format that they're downloaded from the internet
        try {
            startDate = StringUtil.getSimpleDateFormatter().parse(cursor.getString(
                    cursor.getColumnIndex(HSData.Batch.COLUMN_NAME_START_DATE)));
            endDate = StringUtil.getSimpleDateFormatter().parse(cursor.getString(
                    cursor.getColumnIndex(HSData.Batch.COLUMN_NAME_END_DATE)));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
