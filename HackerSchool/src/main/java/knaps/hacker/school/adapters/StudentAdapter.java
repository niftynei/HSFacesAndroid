package knaps.hacker.school.adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.Arrays;

import knaps.hacker.school.R;
import knaps.hacker.school.data.HSData;
import knaps.hacker.school.models.Student;

/**
 * Created by lisaneigut on 9 Oct 2013.
 */
public class StudentAdapter extends CursorAdapter {

    // TODO: Figure out a way to group by batch when moved to a grid view!!
    private static final int STATE_UNKNOWN = 0;
    private static final int STATE_SECTIONED_CELL = 1;
    private static final int STATE_REGULAR_CELL = 2;
    private int[] mCellStates;

    public StudentAdapter(final Context context, final Cursor c, final int flags) {
        super(context, c, flags);
        mCellStates = c == null ? new int[0] : new int[c.getCount()];
    }

    @Override
    public Student getItem(final int position) {
        Cursor cursor = (Cursor) super.getItem(position);
        return new Student(cursor);
    }

    @Override
    public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.student_list_item, parent, false);

        final StudentViewHolder holder = new StudentViewHolder();
        holder.separatorBatchName = (TextView) view.findViewById(R.id.separator);
        holder.studentName = (TextView) view.findViewById(R.id.studentNameText);

        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        final StudentViewHolder holder = (StudentViewHolder) view.getTag();

        boolean needsSeparator = false;

        final int position = cursor.getPosition();

        switch (mCellStates[position]) {
            case STATE_SECTIONED_CELL:
                needsSeparator = true;
                break;
            case STATE_REGULAR_CELL:
                needsSeparator = false;
                break;
            case STATE_UNKNOWN:
            default:
                if (position == 0) {
                    needsSeparator = true;
                }
                else {
                    int batchId = cursor.getInt(HSData.HSer.COL_BATCH_ID);
                    cursor.moveToPosition(position - 1);
                    int nextBatchId = cursor.getInt(HSData.HSer.COL_BATCH_ID);

                    if (batchId != nextBatchId) {
                        needsSeparator = true;
                    }

                    cursor.moveToPosition(position);
                }
                mCellStates[position] = needsSeparator ? STATE_SECTIONED_CELL : STATE_REGULAR_CELL;
                break;
        }

        if (needsSeparator) {
            cursor.copyStringToBuffer(HSData.HSer.COL_BATCH_NAME, holder.batchNameBuffer);
            holder.separatorBatchName.setText(holder.batchNameBuffer.data, 0, holder.batchNameBuffer.sizeCopied);
            holder.separatorBatchName.setVisibility(View.VISIBLE);
        }
        else {
            holder.separatorBatchName.setVisibility(View.GONE);
        }

        /*  NAME */
        String firstName = cursor.getString(HSData.HSer.COL_FIRST_NAME);
        String lastName = cursor.getString(HSData.HSer.COL_LAST_NAME);
        holder.studentName.setText(firstName + " " + lastName);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public Cursor swapCursor(final Cursor cursor) {
        mCellStates = cursor == null ? new int[0] : new int[cursor.getCount()];
        return super.swapCursor(cursor);
    }

    @Override
    public void changeCursor(final Cursor cursor) {
        super.changeCursor(cursor);
        mCellStates = cursor == null ? new int[0] : new int[cursor.getCount()];
    }

    private static class StudentViewHolder {

        public TextView separatorBatchName;
        public CharArrayBuffer batchNameBuffer = new CharArrayBuffer(128);
        public TextView studentName;
    }
}
