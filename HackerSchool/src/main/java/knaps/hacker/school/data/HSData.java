package knaps.hacker.school.data;

import android.provider.BaseColumns;

/**
 * Created by lisaneigut on 14 Sep 2013.
 */
public final class HSData {


    public HSData() {}

    public static final String TEXT_TYPE = " TEXT";
    public static final String INT_TYPE = " INTEGER";
    public static final String PRIMARY_KEY_TYPE = INT_TYPE + " PRIMARY KEY";
    public static final String COMMA_SEP = ", ";
    public static final String STMT_CREATE_TABLE = "CREATE TABLE ";
    public static final String STMT_CREATE_INDEX = "CREATE UNIQUE INDEX ";
    public static final String STMT_IF_NOT_EXISTS = " IF NOT EXISTS ";
    public static final String STMT_DROP_TABLE = "DROP TABLE IF EXISTS ";
    public static final String STMT_SELECT = "SELECT ";
    public static final String STMT_FROM = " FROM ";
    public static final String STMT_WHERE = " WHERE ";
    public static final String STMT_INSERT = "INSERT INTO ";
    public static final String STMT_REPLACE = "REPLACE INTO ";
    public static final String STMT_IS_NOT_NULL = " IS NOT NULL ";
    public static final String STMT_LIMIT = " LIMIT ";
    public static final String STMT_AND = " AND ";
    public static final String STMT_OR = " OR ";
    public static final String STMT_EQUALS_Q = " = ?";
    public static final String STMT_LIKE_Q = " LIKE ?";
    public static final String STMT_NOT_LIKE_Q = " NOT LIKE ?";
    public static final String PARENS_OPEN = " (";
    public static final String PARENS_CLOSE = ") ";
    public static final String STMT_ON = " ON ";

    public static abstract class HSer implements BaseColumns {
        public static final String TABLE_NAME = "students";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_FULL_NAME = "fullName";
        public static final String COLUMN_NAME_IMAGE_URL = "imageUrl";
        public static final String COLUMN_NAME_IMAGE_FILENAME = "imageFilename";
        public static final String COLUMN_NAME_JOB = "job";
        public static final String COLUMN_NAME_JOB_URL = "jobUrl";
        public static final String COLUMN_NAME_SKILLS = "skills";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_TWITTER = "twitterUrl";
        public static final String COLUMN_NAME_GITHUB = "githubUrl";
        public static final String COLUMN_NAME_BATCH = "batchName";
        public static final String COLUMN_NAME_BATCH_ID = "batchId";
        public static final String IDX_ID = "index_hackerschool_id";

        public static final String _ALL =
                _ID + COMMA_SEP +
                        COLUMN_NAME_ID + COMMA_SEP +
                        COLUMN_NAME_FULL_NAME + COMMA_SEP +
                        COLUMN_NAME_IMAGE_URL + COMMA_SEP +
                        COLUMN_NAME_IMAGE_FILENAME + COMMA_SEP +
                        COLUMN_NAME_JOB + COMMA_SEP +
                        COLUMN_NAME_JOB_URL + COMMA_SEP +
                        COLUMN_NAME_SKILLS + COMMA_SEP +
                        COLUMN_NAME_EMAIL + COMMA_SEP +
                        COLUMN_NAME_GITHUB + COMMA_SEP +
                        COLUMN_NAME_TWITTER + COMMA_SEP +
                        COLUMN_NAME_BATCH_ID + COMMA_SEP +
                        COLUMN_NAME_BATCH;


        public static final String SQL_CREATE =
                STMT_CREATE_TABLE + TABLE_NAME + " (" +
                        _ID + PRIMARY_KEY_TYPE + COMMA_SEP +
                        COLUMN_NAME_ID + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_FULL_NAME + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_IMAGE_URL + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_IMAGE_FILENAME + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_JOB + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_JOB_URL + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_SKILLS + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_EMAIL + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_GITHUB + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_TWITTER + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_BATCH_ID + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_BATCH + TEXT_TYPE +
                        " )";

        public static final String SQL_CREATE_ID_INDEX =
                STMT_CREATE_INDEX + STMT_IF_NOT_EXISTS + IDX_ID + STMT_ON + TABLE_NAME +
                        PARENS_OPEN + COLUMN_NAME_ID + PARENS_CLOSE;

        public static final String SQL_DELETE =
                STMT_DROP_TABLE + TABLE_NAME;

        public static final String SQL_UPSERT_ALL =
                STMT_REPLACE + TABLE_NAME + "(" +
                        COLUMN_NAME_ID + COMMA_SEP +
                        COLUMN_NAME_FULL_NAME + COMMA_SEP +
                        COLUMN_NAME_IMAGE_URL + COMMA_SEP +
                        COLUMN_NAME_JOB + COMMA_SEP +
                        COLUMN_NAME_JOB_URL + COMMA_SEP +
                        COLUMN_NAME_SKILLS + COMMA_SEP +
                        COLUMN_NAME_EMAIL + COMMA_SEP +
                        COLUMN_NAME_GITHUB + COMMA_SEP +
                        COLUMN_NAME_TWITTER + COMMA_SEP +
                        COLUMN_NAME_BATCH_ID + COMMA_SEP +
                        COLUMN_NAME_BATCH + ")  VALUES (" +
                        "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        public static final String GET_ALL_FILTERED =
                STMT_SELECT + _ALL +
                        STMT_FROM + TABLE_NAME +
                        STMT_WHERE + COLUMN_NAME_FULL_NAME + STMT_LIKE_Q +
                        STMT_OR + COLUMN_NAME_SKILLS + STMT_LIKE_Q;


        public static final String GET_ALL = STMT_SELECT + _ALL + STMT_FROM + TABLE_NAME;

        public static final String SQL_GET_FILENAME =
                STMT_SELECT + COLUMN_NAME_IMAGE_FILENAME +
                        STMT_FROM + TABLE_NAME +
                        STMT_WHERE + COLUMN_NAME_IMAGE_URL + " LIKE ? ";

        public static final String SQL_GET_ALL_SAVED_TO_DISK =
                STMT_SELECT + _ALL +
                        STMT_FROM + TABLE_NAME +
                        STMT_WHERE + COLUMN_NAME_IMAGE_FILENAME + STMT_IS_NOT_NULL;

        public static final String SQL_GET_ALL_DISK_BY_BATCH =
                SQL_GET_ALL_SAVED_TO_DISK +
                        STMT_AND + COLUMN_NAME_BATCH + " = ? ";

        public static final String SQL_GET_ALL_DISK_BATCH_WITH_LIMIT =
                SQL_GET_ALL_DISK_BY_BATCH +
                        STMT_LIMIT + " ? ";

        public static final String SQL_GET_ALL_SAVED_TO_DISK_WITH_LIMIT =
                SQL_GET_ALL_SAVED_TO_DISK +
                        STMT_LIMIT + " ?";

        public static final String SQL_GET_ALL_BATCH =
                STMT_SELECT + _ALL +
                        STMT_FROM + TABLE_NAME +
                        STMT_WHERE + COLUMN_NAME_BATCH + " = ? ";

        public static final String SQL_GET_ALL_LIMIT =
                STMT_SELECT + _ALL +
                        STMT_FROM + TABLE_NAME +
                        STMT_LIMIT + " ?";

        public static final String SQL_GET_ALL_BATCH_LIMIT =
                SQL_GET_ALL_BATCH +
                        STMT_LIMIT + " ?";

        public static final String SQL_NOT_LIKE_YOU =
                COLUMN_NAME_EMAIL + STMT_NOT_LIKE_Q;

        public static final String[] PROJECTION_ALL = {
                _ID,
                COLUMN_NAME_ID,
                COLUMN_NAME_FULL_NAME,
                COLUMN_NAME_IMAGE_URL,
                COLUMN_NAME_IMAGE_FILENAME,
                COLUMN_NAME_JOB,
                COLUMN_NAME_JOB_URL,
                COLUMN_NAME_SKILLS,
                COLUMN_NAME_EMAIL,
                COLUMN_NAME_GITHUB,
                COLUMN_NAME_TWITTER,
                COLUMN_NAME_BATCH_ID,
                COLUMN_NAME_BATCH
        };

        public static final int ID = 1;
        public static final int NAME = 2;
        public static final int IMAGE_URL = 3;
        public static final int IMAGE_FILENAME = 4;
        public static final int JOB = 5;
        public static final int JOB_URL = 6;
        public static final int SKILLS = 7;
        public static final int EMAIL = 8;
        public static final int GITHUB = 9;
        public static final int TWITTER = 10;
        public static final int BATCH_ID = 11;
        public static final int BATCH = 12;

        public static final String SORT_DEFAULT =
                COLUMN_NAME_BATCH_ID + " DESC" + COMMA_SEP +
                        COLUMN_NAME_FULL_NAME + " ASC";

        public static final String SORT_BATCH =
                COLUMN_NAME_BATCH_ID + " ASC";
    }
}
