package knaps.hacker.school.models;

import android.database.Cursor;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import knaps.hacker.school.data.HSData;
import knaps.hacker.school.utils.StringUtil;

/**
 * Created by lisaneigut on 6 Apr 2014.
 */
public class Batch implements Serializable {

    private static final long serialVersionUID = 939493284502L;

    public long id;
    public String name;
    public String startDate;
    public String endDate;

    public Batch() {}

    public Batch(final Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(HSData.Batch.COLUMN_NAME_ID));
        name = cursor.getString(cursor.getColumnIndex(HSData.Batch.COLUMN_NAME_NAME));
        startDate =  cursor.getString(cursor.getColumnIndex(HSData.Batch.COLUMN_NAME_START_DATE));
        endDate =  cursor.getString(cursor.getColumnIndex(HSData.Batch.COLUMN_NAME_END_DATE));
    }

    public Date getStartDate() {
        try {
            return StringUtil.getSimpleDateFormatter().parse(startDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Date getEndDate() {
        try {
            return StringUtil.getSimpleDateFormatter().parse(endDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
