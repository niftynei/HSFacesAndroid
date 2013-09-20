package knaps.hacker.school.data;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by lisaneigut on 15 Sep 2013.
 */
public class HSRandomCursorWrapper  extends CursorWrapper{

    /**
     * An array list, of randomized positions to call
     */
    final ArrayList<Integer> randomizedOrder;
    int mCurrentIndex;

    /**
     * Creates a cursor wrapper.
     * This cursor wrapper randomizes the order of the underlying cursor
     *
     * @param cursor The underlying cursor to wrap.
     */
    public HSRandomCursorWrapper(Cursor cursor) {
        super(cursor);

        randomizedOrder = new ArrayList<Integer>(cursor.getCount());

        for (int i = 0; i < cursor.getCount(); i++) {
            randomizedOrder.add(i);
        }
        long seed = System.nanoTime() % 1000000;
        Collections.shuffle(randomizedOrder, new Random(seed));
        mCurrentIndex = 0;
    }

    @Override
    public int getPosition() {
        return mCurrentIndex;
    }

    @Override
    public boolean move(int offset) {
        mCurrentIndex = mCurrentIndex + offset;
        if (mCurrentIndex < 0 || mCurrentIndex > randomizedOrder.size()) {
            throw new IndexOutOfBoundsException("Offset was " + offset + " size is " + randomizedOrder.size());
        }
        return getWrappedCursor().moveToPosition(randomizedOrder.get(mCurrentIndex));
    }

    @Override
    public boolean moveToFirst() {
        mCurrentIndex = 0;
        return getWrappedCursor().moveToPosition(randomizedOrder.get(mCurrentIndex));
    }

    @Override
    public boolean moveToLast() {
        mCurrentIndex = randomizedOrder.size() - 1;
        return getWrappedCursor().moveToPosition(randomizedOrder.get(mCurrentIndex));
    }

    @Override
    public boolean moveToNext() {
        mCurrentIndex++;
        if (mCurrentIndex < 0 || mCurrentIndex > randomizedOrder.size()) {
            throw new IndexOutOfBoundsException("Current index is " + mCurrentIndex + " size is " + randomizedOrder.size());
        }
        return getWrappedCursor().moveToPosition(randomizedOrder.get(mCurrentIndex));
    }

    @Override
    public boolean moveToPrevious() {
        mCurrentIndex--;
        if (mCurrentIndex < 0 || mCurrentIndex > randomizedOrder.size()) {
            throw new IndexOutOfBoundsException("Current index is " + mCurrentIndex + " size is " + randomizedOrder.size());
        }
        return getWrappedCursor().moveToPosition(randomizedOrder.get(mCurrentIndex));
    }

    @Override
    public boolean moveToPosition(int position) {
        mCurrentIndex = position;
        if (mCurrentIndex < 0 || mCurrentIndex > randomizedOrder.size()) {
            throw new IndexOutOfBoundsException("Current index is " + mCurrentIndex + " size is " + randomizedOrder.size());
        }
        return getWrappedCursor().moveToPosition(randomizedOrder.get(mCurrentIndex));
    }

    @Override
    public boolean isLast() {
        return mCurrentIndex == randomizedOrder.size() - 1;
    }

    @Override
    public boolean isFirst() {
        return mCurrentIndex == 0;
    }

    @Override
    public boolean isAfterLast() {
        return mCurrentIndex >= randomizedOrder.size();
    }

    @Override
    public boolean isBeforeFirst() {
        return mCurrentIndex < 0;
    }

}
