package knaps.hacker.school.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;

/*
 * Adapted from:
 *
 * Copyright (c) 2011-2012 CommonsWare, LLC
 * portions Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * By Lisa Neigut
 */

public class SQLiteCursorLoader extends AsyncTaskLoader<Cursor> {

    private static final int UPDATE = 0;
    private static final int INSERT = 1;
    private static final int REPLACE = 2;
    private static final int DELETE = 3;
    private static final int EXEC = 4;

    Cursor mLastCursor = null;

    private String mTableName;
    private String[] mProjection;
    private String mRawQuery = null;
    private String[] mSelectionArgs = null;
    private String mLimit = null;
    private String mOrderBy = null;
    private String mHaving = null;
    private String mGroupBy = null;
    private String mSelection = null;

    public SQLiteCursorLoader(Context context,
                              String tableName, String[] projection, String sortOrder) {
        super(context);
        mTableName = tableName;
        mProjection = projection;
        mOrderBy = sortOrder;
    }

    public SQLiteCursorLoader(Context context, String rawQuery, String[] selectionArgs) {
        super(context);
        mRawQuery = rawQuery;
        mSelectionArgs = selectionArgs;
    }

    public SQLiteCursorLoader(Context context, String table, String[] columns,
                              String selection, String[] selectionArgs, String groupBy,
                              String having, String orderBy, String limit) {
        super(context);
        mTableName = table;
        mProjection = columns;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mGroupBy = groupBy;
        mHaving = having;
        mOrderBy = orderBy;
        mLimit = limit;
    }

    public static class SQLiteCursorBuilder {

        private final Context mContext;
        private final String mTable;
        private String mLimit = null;
        private String[] mColumns = null;
        private String mSelection = null;
        private String[] mSelectionArgs = null;
        private String mGroupBy = null;
        private String mHaving = null;
        private String mOrderBy = null;

        public SQLiteCursorBuilder(Context context, String table) {
            mContext = context;
            mTable = table;
        }

        public SQLiteCursorBuilder columns(String[] cols) {
            mColumns = cols;
            return this;
        }

        public SQLiteCursorBuilder selection(String selection) {
            mSelection = selection;
            return this;
        }

        public SQLiteCursorBuilder selectionArgs(String[] selectionArgs) {
            mSelectionArgs = selectionArgs;
            return this;
        }

        public SQLiteCursorBuilder groupBy(String groupBy) {
            mGroupBy = groupBy;
            return this;
        }

        public SQLiteCursorBuilder having(String having) {
            mHaving = having;
            return this;
        }

        public SQLiteCursorBuilder orderBy(String orderBy) {
            mOrderBy = orderBy;
            return this;
        }

        public SQLiteCursorBuilder limit(String limit) {
            mLimit = limit;
            return this;
        }

        public SQLiteCursorLoader build() {
            return new SQLiteCursorLoader(mContext, mTable, mColumns, mSelection, mSelectionArgs,
                    mGroupBy, mHaving, mOrderBy, mLimit);
        }

    }

    @Override
    public Cursor loadInBackground() {
        final SQLiteDatabase db = new HSDatabaseHelper(getContext()).getReadableDatabase();
        final Cursor cursor;
        if (mRawQuery != null) {
            cursor = db.rawQuery(mRawQuery, mSelectionArgs);
        }
        else {
            cursor = db.query(
                    mTableName,
                    mProjection,
                    mSelection,
                    mSelectionArgs,
                    mGroupBy,
                    mHaving,
                    mOrderBy,
                    mLimit
            );
        }

        if (cursor != null) {
            cursor.getCount();
        }

        return new HSRandomCursorWrapper(cursor);
    }

    @Override
    public void deliverResult(Cursor cursor) {
        if (isReset()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        Cursor oldCursor = mLastCursor;
        mLastCursor = cursor;

        if (isStarted()) {
            super.deliverResult(cursor);
        }

        if (oldCursor != null && oldCursor != cursor && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }

    @Override
    protected void onStartLoading() {
        if (mLastCursor != null) {
            deliverResult(mLastCursor);
        }
        // register observer
        if (mLastCursor == null || takeContentChanged()) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor cursor) {
        super.onCanceled(cursor);

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    public void onReset() {
        onStopLoading();

        if (mLastCursor != null && !mLastCursor.isClosed()) {
            mLastCursor.close();
        }
        mLastCursor = null;

        // unregister observer
    }

    @Override
    public void dump(String prefix, FileDescriptor fd,
                     PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        writer.print(prefix);
        writer.print("args=");
        writer.println(Arrays.toString(args));
    }

    public void insert(String table, String nullColumn, ContentValues values) {
        final DbTransportObject trans = new DbTransportObject();
        trans.table = table;
        trans.nullColumnHack = nullColumn;
        trans.values = values;
        new DBAsyncTask(this, INSERT).execute(trans);
    }

    public void update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        final DbTransportObject trans = new DbTransportObject();
        trans.table = table;
        trans.whereArgs = whereArgs;
        trans.whereClause = whereClause;
        trans.values = values;
        new DBAsyncTask(this, UPDATE).execute(trans);
    }

    public void replace(String table, String nullColumn, ContentValues values) {
        final DbTransportObject trans = new DbTransportObject();
        trans.table = table;
        trans.nullColumnHack = nullColumn;
        trans.values = values;
        new DBAsyncTask(this, REPLACE).execute(trans);
    }

    public void delete(String table, String whereClause, String[] whereArgs) {
        final DbTransportObject trans = new DbTransportObject();
        trans.table = table;
        trans.whereArgs = whereArgs;
        trans.whereClause = whereClause;
        new DBAsyncTask(this, DELETE).execute(trans);
    }

    public void execSQL(String sql, Object[] bindArgs) {
        final DbTransportObject trans = new DbTransportObject();
        trans.sql = sql;
        trans.bindParams = bindArgs;
        new DBAsyncTask(this, EXEC).execute(trans);

    }

    private class DbTransportObject {
        public String table;
        public String nullColumnHack;
        public ContentValues values;
        public String whereClause;
        public String[] whereArgs;
        public String sql;
        public Object[] bindParams;
    }


    private class DBAsyncTask extends AsyncTask<DbTransportObject, Void, Void> {

        private Loader<?> mLoader = null;
        private int mCommand;

        public DBAsyncTask(Loader<?> loader, int command) {
            mLoader = loader;
            mCommand = command;
        }

        @Override
        protected Void doInBackground(DbTransportObject... params) {
            DbTransportObject dbStuffs = params[0];
            final SQLiteDatabase db = new HSDatabaseHelper(getContext()).getWritableDatabase();
            switch (mCommand) {
                case INSERT:
                    db.insert(dbStuffs.table, dbStuffs.nullColumnHack, dbStuffs.values);
                    break;
                case REPLACE:
                    db.replace(dbStuffs.table, dbStuffs.nullColumnHack, dbStuffs.values);
                    break;
                case UPDATE:
                    db.update(dbStuffs.table, dbStuffs.values, dbStuffs.whereClause,
                            dbStuffs.whereArgs);
                    break;
                case DELETE:
                    db.delete(dbStuffs.table, dbStuffs.whereClause, dbStuffs.whereArgs);
                    break;
                case EXEC:
                    db.execSQL(dbStuffs.sql, dbStuffs.bindParams);
                default:
                    // do nothing
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mLoader.onContentChanged();
        }
    }
}
