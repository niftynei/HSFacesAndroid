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
    public static final String NOT = " NOT ";
    public static final String CREATE_TABLE = "CREATE TABLE ";
    public static final String CREATE_INDEX = "CREATE UNIQUE INDEX ";
    public static final String IF_NOT_EXISTS = " IF NOT EXISTS ";
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS ";
    public static final String SELECT = "SELECT ";
    public static final String FROM = " FROM ";
    public static final String WHERE = " WHERE ";
    public static final String INSERT = "INSERT INTO ";
    public static final String REPLACE = "REPLACE INTO ";
    public static final String IS_NOT_NULL = " IS NOT NULL ";
    public static final String LIMIT = " LIMIT ";
    public static final String AND = " AND ";
    public static final String OR = " OR ";
    public static final String EQUALS = " = ";
    public static final String Q = " ? ";
    public static final String LIKE = " LIKE ";
    public static final String LIKE_Q = LIKE + Q;
    public static final String EQUALS_Q = EQUALS + Q;
    public static final String NOT_LIKE_Q = NOT + LIKE + Q;
    public static final String PARENS_OPEN = " (";
    public static final String PARENS_CLOSE = ") ";
    public static final String ON = " ON ";
    public static final String DOT = ".";
    public static final String AS = " AS ";
    public static final String VALUES = " VALUES ";
    public static final String DESC = " DESC ";
    public static final String ASC = " ASC ";

    public static abstract class SqlTable implements BaseColumns {
        public static String TABLE_NAME;
        public static String IDX_ID;

        public static final String SQL_DELETE =
                DROP_TABLE + TABLE_NAME;
    }


    public static abstract class Batch extends SqlTable {

        public static final String TABLE_NAME = "batches";
        public static final String COLUMN_NAME_ID = "batchId"; // Avoid collision when doing joins with HSer
        public static final String COLUMN_NAME_NAME = "batchName"; // Avoid collision when doing joins with HSer
        public static final String COLUMN_NAME_START_DATE = "startDate";
        public static final String COLUMN_NAME_END_DATE = "endDate";
        public static final String COLUMN_NAME_LAST_UPDATED = "updated";
        public static final String IDX_ID = "index_batch_id";

        public static final String _ALL =
                _ID + COMMA_SEP +
                        COLUMN_NAME_ID + COMMA_SEP +
                        COLUMN_NAME_NAME + COMMA_SEP +
                        COLUMN_NAME_START_DATE + COMMA_SEP +
                        COLUMN_NAME_END_DATE + COMMA_SEP +
                        COLUMN_NAME_LAST_UPDATED;

        public static final String SQL_CREATE =
                CREATE_TABLE + TABLE_NAME + PARENS_OPEN +
                        _ID + PRIMARY_KEY_TYPE + COMMA_SEP +
                        COLUMN_NAME_ID + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_START_DATE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_END_DATE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_LAST_UPDATED + TEXT_TYPE +
                        PARENS_CLOSE;

        public static final String SQL_CREATE_ID_INDEX =
                CREATE_INDEX + IF_NOT_EXISTS + IDX_ID + ON + TABLE_NAME +
                        PARENS_OPEN + COLUMN_NAME_ID + PARENS_CLOSE;

        public static final String SQL_UPSERT_ALL =
                REPLACE + TABLE_NAME + PARENS_OPEN +
                        COLUMN_NAME_ID + COMMA_SEP +
                        COLUMN_NAME_NAME + COMMA_SEP +
                        COLUMN_NAME_START_DATE + COMMA_SEP +
                        COLUMN_NAME_END_DATE +
                        PARENS_CLOSE +  VALUES + PARENS_OPEN +
                        Q + COMMA_SEP +
                        Q + COMMA_SEP +
                        Q + COMMA_SEP +
                        Q + COMMA_SEP +
                        Q + COMMA_SEP +
                        PARENS_CLOSE;

        public static final String GET_ALL = SELECT + _ALL + FROM + TABLE_NAME;

        public static final String GET_LAST_UPDATED =
                SELECT + COLUMN_NAME_ID + COMMA_SEP + COLUMN_NAME_LAST_UPDATED + FROM + TABLE_NAME;

        public static final String[] PROJECTION_ALL = {
                _ID,
                COLUMN_NAME_ID,
                COLUMN_NAME_NAME,
                COLUMN_NAME_START_DATE,
                COLUMN_NAME_END_DATE,
                COLUMN_NAME_LAST_UPDATED
        };

        public static final String SORT_DEFAULT = COLUMN_NAME_ID + DESC;

    }

    public static abstract class HSer extends SqlTable {

        // TABLE NAME
        public static final String TABLE_NAME = "students";

        // COLUMNS
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_FIRST_NAME = "firstName";
        public static final String COLUMN_NAME_LAST_NAME = "lastName";
        public static final String COLUMN_NAME_IMAGE_URL = "imageUrl";
        public static final String COLUMN_NAME_IMAGE_FILENAME = "imageFilename";
        public static final String COLUMN_NAME_JOB = "job";
        public static final String COLUMN_NAME_JOB_URL = "jobUrl";
        public static final String COLUMN_NAME_SKILLS = "skills";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_TWITTER = "twitterAccount";
        public static final String COLUMN_NAME_GITHUB = "githubAccount";
        public static final String COLUMN_NAME_BATCH_ID = "batchId";
        public static final String COLUMN_NAME_LAST_UPDATED = "updated";
        public static final String IDX_ID = "index_hackerschool_id";

        private static final String _ALL =
                _ID + COMMA_SEP +
                        COLUMN_NAME_ID + COMMA_SEP +
                        COLUMN_NAME_FIRST_NAME + COMMA_SEP +
                        COLUMN_NAME_IMAGE_URL + COMMA_SEP +
                        COLUMN_NAME_IMAGE_FILENAME + COMMA_SEP +
                        COLUMN_NAME_JOB + COMMA_SEP +
                        COLUMN_NAME_JOB_URL + COMMA_SEP +
                        COLUMN_NAME_SKILLS + COMMA_SEP +
                        COLUMN_NAME_EMAIL + COMMA_SEP +
                        COLUMN_NAME_GITHUB + COMMA_SEP +
                        COLUMN_NAME_TWITTER + COMMA_SEP +
                        COLUMN_NAME_BATCH_ID + COMMA_SEP +
                        COLUMN_NAME_LAST_UPDATED;

        private static String _ALL_WITH_BATCH =
                _ALL + COMMA_SEP +
                        Batch.TABLE_NAME + DOT + Batch.COLUMN_NAME_NAME + AS + Batch.COLUMN_NAME_NAME + COMMA_SEP +
                        Batch.TABLE_NAME + DOT + Batch.COLUMN_NAME_START_DATE + AS + Batch.COLUMN_NAME_START_DATE + COMMA_SEP +
                        Batch.TABLE_NAME + DOT + Batch.COLUMN_NAME_END_DATE + AS + Batch.COLUMN_NAME_END_DATE;


        public static final String SQL_CREATE =
                CREATE_TABLE + TABLE_NAME + PARENS_OPEN +
                        _ID + PRIMARY_KEY_TYPE + COMMA_SEP +
                        COLUMN_NAME_ID + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_FIRST_NAME + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_IMAGE_URL + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_IMAGE_FILENAME + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_JOB + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_JOB_URL + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_SKILLS + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_EMAIL + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_GITHUB + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_TWITTER + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_BATCH_ID + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_LAST_UPDATED + TEXT_TYPE +
                        PARENS_CLOSE;

        public static final String SQL_CREATE_ID_INDEX =
                CREATE_INDEX + IF_NOT_EXISTS + IDX_ID + ON + TABLE_NAME +
                        PARENS_OPEN + COLUMN_NAME_ID + PARENS_CLOSE;

        public static final String SQL_DELETE =
                DROP_TABLE + TABLE_NAME;

        public static final String SQL_UPSERT_ALL =
                REPLACE + TABLE_NAME + "(" +
                        COLUMN_NAME_ID + COMMA_SEP +
                        COLUMN_NAME_FIRST_NAME + COMMA_SEP +
                        COLUMN_NAME_IMAGE_URL + COMMA_SEP +
                        COLUMN_NAME_JOB + COMMA_SEP +
                        COLUMN_NAME_JOB_URL + COMMA_SEP +
                        COLUMN_NAME_SKILLS + COMMA_SEP +
                        COLUMN_NAME_EMAIL + COMMA_SEP +
                        COLUMN_NAME_GITHUB + COMMA_SEP +
                        COLUMN_NAME_TWITTER + COMMA_SEP +
                        COLUMN_NAME_BATCH_ID + COMMA_SEP +
                        COLUMN_NAME_LAST_UPDATED +
                        PARENS_CLOSE +  VALUES + PARENS_OPEN +
                        Q + COMMA_SEP +
                        Q + COMMA_SEP +
                        Q + COMMA_SEP +
                        Q + COMMA_SEP +
                        Q + COMMA_SEP +
                        Q + COMMA_SEP +
                        Q + COMMA_SEP +
                        Q + COMMA_SEP +
                        Q + COMMA_SEP +
                        Q + COMMA_SEP +
                        Q + COMMA_SEP +
                        PARENS_CLOSE;

        public static final String JOIN_BATCH =
                " LEFT OUTER JOIN " + Batch.TABLE_NAME + ON +
                        TABLE_NAME + DOT + COLUMN_NAME_BATCH_ID + EQUALS + Batch.TABLE_NAME + DOT + Batch.COLUMN_NAME_ID + " ";

        public static final String GET_ALL_FILTERED =
                SELECT + _ALL_WITH_BATCH +
                        FROM + TABLE_NAME + JOIN_BATCH +
                        WHERE + COLUMN_NAME_FIRST_NAME + LIKE_Q +
                        OR + COLUMN_NAME_SKILLS + LIKE_Q;

        public static final String SQL_GET_FILENAME =
                SELECT + COLUMN_NAME_IMAGE_FILENAME +
                        FROM + TABLE_NAME +
                        WHERE + COLUMN_NAME_IMAGE_URL + LIKE_Q;

        public static final String SQL_GET_ALL_SAVED_TO_DISK =
                SELECT + _ALL_WITH_BATCH +
                        FROM + TABLE_NAME + JOIN_BATCH +
                        WHERE + COLUMN_NAME_IMAGE_FILENAME + IS_NOT_NULL;

        public static final String SQL_GET_ALL_DISK_BY_BATCH =
                SQL_GET_ALL_SAVED_TO_DISK +
                        AND + COLUMN_NAME_BATCH_ID + EQUALS_Q;

        public static final String SQL_GET_ALL_DISK_BATCH_WITH_LIMIT =
                SQL_GET_ALL_DISK_BY_BATCH +
                        LIMIT + Q;

        public static final String SQL_GET_ALL_SAVED_TO_DISK_WITH_LIMIT =
                SQL_GET_ALL_SAVED_TO_DISK +
                        LIMIT + Q;

        public static final String SQL_GET_ALL_BATCH =
                SELECT + _ALL +
                        FROM + TABLE_NAME +
                        WHERE + COLUMN_NAME_BATCH_ID + EQUALS_Q;

        public static final String SQL_GET_ALL_LIMIT =
                SELECT + _ALL +
                        FROM + TABLE_NAME +
                        LIMIT + Q;

        public static final String SQL_GET_ALL_BATCH_LIMIT =
                SQL_GET_ALL_BATCH +
                        LIMIT + Q;

        public static final String SQL_NOT_LIKE_YOU =
                COLUMN_NAME_EMAIL + NOT_LIKE_Q;

        public static final String[] PROJECTION_ALL = {
                _ID,
                COLUMN_NAME_ID,
                COLUMN_NAME_FIRST_NAME,
                COLUMN_NAME_IMAGE_URL,
                COLUMN_NAME_IMAGE_FILENAME,
                COLUMN_NAME_JOB,
                COLUMN_NAME_JOB_URL,
                COLUMN_NAME_SKILLS,
                COLUMN_NAME_EMAIL,
                COLUMN_NAME_GITHUB,
                COLUMN_NAME_TWITTER,
                COLUMN_NAME_BATCH_ID,
                COLUMN_NAME_LAST_UPDATED
        };

        public static final String[] PROJECTION_ALL_BATCH = {
                _ID,
                COLUMN_NAME_ID,
                COLUMN_NAME_FIRST_NAME,
                COLUMN_NAME_IMAGE_URL,
                COLUMN_NAME_IMAGE_FILENAME,
                COLUMN_NAME_JOB,
                COLUMN_NAME_JOB_URL,
                COLUMN_NAME_SKILLS,
                COLUMN_NAME_EMAIL,
                COLUMN_NAME_GITHUB,
                COLUMN_NAME_TWITTER,
                COLUMN_NAME_BATCH_ID,
                COLUMN_NAME_LAST_UPDATED,
                Batch.TABLE_NAME + DOT + Batch.COLUMN_NAME_NAME + AS + Batch.COLUMN_NAME_NAME,
                Batch.TABLE_NAME + DOT + Batch.COLUMN_NAME_START_DATE + AS + Batch.COLUMN_NAME_START_DATE,
                Batch.TABLE_NAME + DOT + Batch.COLUMN_NAME_END_DATE + AS + Batch.COLUMN_NAME_END_DATE
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
        public static final int LAST_UPDATED = 12;
        public static final int BATCH_NAME = 13;
        public static final int BATCH_START = 14;
        public static final int BATCH_END = 15;

        public static final String SORT_DEFAULT =
                COLUMN_NAME_BATCH_ID + DESC + COMMA_SEP +
                        COLUMN_NAME_FIRST_NAME + ASC;

        public static final String SORT_BATCH =
                COLUMN_NAME_BATCH_ID + ASC;
    }

}
