package knaps.hacker.school.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.text.TextUtils;

public class HackerSchoolContentProvider extends ContentProvider {

    // Authority
    public static final String PROVIDER_NAME = "knaps.hacker.school.content";
    private static final String URI = "content://" + PROVIDER_NAME;
    public static final Uri CONTENT_URI = Uri.parse(URI);

    private static final String SINGLE_RECORD_TYPE = "vnd.android.cursor.item/vnd." + PROVIDER_NAME;
    private static final String MULTIPLE_RECORD_TYPE = "vnd.android.cursor.dir/vnd." + PROVIDER_NAME;

    private static final int STUDENTS = 1;
    private static final int STUDENT = 2;
    private static final int BATCHES = 3;
    private static final int BATCH = 4;

    // Matcher
    private static UriMatcher sMatcher;

    static {
        sMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sMatcher.addURI(PROVIDER_NAME, Uris.STUDENTS.path(), STUDENTS);
        sMatcher.addURI(PROVIDER_NAME, Uris.STUDENT.path(), STUDENT);
        sMatcher.addURI(PROVIDER_NAME, Uris.BATCHES.path(), BATCH);
        sMatcher.addURI(PROVIDER_NAME, Uris.BATCH.path(), BATCHES);
    }

    private HSDatabaseHelper mDbHelper;
    private UpdateManager mUpdateManager;

    public enum Uris {
        STUDENT("students/#"),
        STUDENTS("students"),
        BATCH("batches/#"),
        BATCHES("batches");

        private String mPath;

        private Uris(String path) {
            mPath = path;
        }

        public String path() {return mPath;}

        public Uri getUri() {
            return CONTENT_URI.buildUpon().appendPath(mPath).build();
        }

        public Uri getUri(long id) {
            return CONTENT_URI.buildUpon().appendPath(mPath.replace("#", String.valueOf(id))).build();
        }
    }

    @Override
    public boolean onCreate() {

        mDbHelper = new HSDatabaseHelper(getContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        mUpdateManager = new UpdateManager(getContext(), mDbHelper);
        return db != null;
    }

    @Override
    public String getType(Uri uri) {
        switch (sMatcher.match(uri)) {
            case STUDENTS:
            case BATCHES:
                return MULTIPLE_RECORD_TYPE;
            case STUDENT:
            case BATCH:
                return SINGLE_RECORD_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    /**
     * On Query, if the data is out of date, we go ahead and launch an update here.
     * Magically behind the scenes updating data!
     *
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        Cursor cursor;
        switch (sMatcher.match(uri)) {
            case STUDENT:
                qb.appendWhere(HSData.HSer.WHERE_HAS_ID);
                // falls through
            case STUDENTS:
                qb.setTables(HSData.HSer.TABLE_NAME + ", " + HSData.Batch.TABLE_NAME);
                if (!TextUtils.isEmpty(sortOrder)) {
                    sortOrder = HSData.HSer.SORT_DEFAULT;
                }
                break;
            case BATCH:
                qb.appendWhere(HSData.Batch.WHERE_HAS_ID);
                // falls through
            case BATCHES:
                qb.setTables(HSData.Batch.TABLE_NAME);
                if (!TextUtils.isEmpty(sortOrder)) {
                    sortOrder = HSData.Batch.SORT_DEFAULT;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // check if data is outdated
        mUpdateManager.checkAndUpdateDataIfNeeded();
        cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder, getLimit(uri));

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    private String getLimit(final Uri uri) {
        try {
            // we want to guarantee this is an integer before we send it back
            return String.valueOf(Integer.parseInt(uri.getQueryParameter(DbKeywords.LIMIT)));
        }
        catch (NullPointerException npe) {
            // there's nothing by that name here
        }
        catch (NumberFormatException ex) {
            // whoops, bad parameter given
        }
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
