package knaps.hacker.school.data;

import android.provider.BaseColumns;

/**
 * Created by lisaneigut on 14 Sep 2013.
 */
public final class HSDataContract {

    public HSDataContract() {}

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String PRIMARY_KEY_TYPE = INT_TYPE + " PRIMARY KEY";
    private static final String COMMA_SEP = ", ";
    private static final String STMT_CREATE_TABLE = "CREATE TABLE ";
    private static final String STMT_DROP_TABLE = "DROP TABLE IF EXISTS ";


    public static abstract class StudentEntry implements BaseColumns {
        public static final String TABLE_NAME = "students";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_FULL_NAME = "fullName";
        public static final String COLUMN_NAME_IMAGE_URL = "imageUrl";
        public static final String COLUMN_NAME_JOB = "job";
        public static final String COLUMN_NAME_JOB_URL = "jobUrl";
        public static final String COLUMN_NAME_SKILLS = "skills";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_TWITTER = "twitterUrl";
        public static final String COLUMN_NAME_GITHUB = "githubUrl";
        public static final String COLUMN_NAME_BATCH = "batchName";
        public static final String COLUMN_NAME_BATCH_ID = "batchId";

        public static final String SQL_CREATE =
                STMT_CREATE_TABLE + TABLE_NAME + " (" +
                _ID + PRIMARY_KEY_TYPE + COMMA_SEP +
                COLUMN_NAME_ID + INT_TYPE + COMMA_SEP +
                COLUMN_NAME_FULL_NAME + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_IMAGE_URL + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_JOB + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_JOB_URL + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_SKILLS + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_EMAIL + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_GITHUB + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_TWITTER + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_BATCH_ID + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_BATCH + TEXT_TYPE +
                " )";

        public static final String SQL_DELETE =
                STMT_DROP_TABLE + TABLE_NAME;

        public static final String[] PROJECTION_ALL = {
                _ID,
                COLUMN_NAME_ID,
                COLUMN_NAME_FULL_NAME,
                COLUMN_NAME_IMAGE_URL,
                COLUMN_NAME_JOB,
                COLUMN_NAME_JOB_URL,
                COLUMN_NAME_SKILLS,
                COLUMN_NAME_EMAIL,
                COLUMN_NAME_GITHUB,
                COLUMN_NAME_TWITTER,
                COLUMN_NAME_BATCH_ID,
                COLUMN_NAME_BATCH
        };

        public static final String SORT_DEFAULT =
                COLUMN_NAME_BATCH_ID + " DESC" + COMMA_SEP +
                COLUMN_NAME_FULL_NAME + " ASC";
    }
}
