package knaps.hacker.school.data;

import android.provider.BaseColumns;

import static knaps.hacker.school.data.DbKeywords.*;
import static knaps.hacker.school.data.DbKeywords.AND;
import static knaps.hacker.school.data.DbKeywords.ASC;
import static knaps.hacker.school.data.DbKeywords.COMMA_SEP;
import static knaps.hacker.school.data.DbKeywords.CREATE_INDEX;
import static knaps.hacker.school.data.DbKeywords.CREATE_TABLE;
import static knaps.hacker.school.data.DbKeywords.DESC;
import static knaps.hacker.school.data.DbKeywords.DOT;
import static knaps.hacker.school.data.DbKeywords.DROP_TABLE;
import static knaps.hacker.school.data.DbKeywords.EQUALS;
import static knaps.hacker.school.data.DbKeywords.EQUALS_Q;
import static knaps.hacker.school.data.DbKeywords.FROM;
import static knaps.hacker.school.data.DbKeywords.GROUP_BY;
import static knaps.hacker.school.data.DbKeywords.IF_NOT_EXISTS;
import static knaps.hacker.school.data.DbKeywords.INSERT_OR_REPLACE;
import static knaps.hacker.school.data.DbKeywords.INT_TYPE;
import static knaps.hacker.school.data.DbKeywords.IS_NOT_NULL;
import static knaps.hacker.school.data.DbKeywords.LIKE_Q;
import static knaps.hacker.school.data.DbKeywords.LIMIT;
import static knaps.hacker.school.data.DbKeywords.NOT_LIKE_Q;
import static knaps.hacker.school.data.DbKeywords.ON;
import static knaps.hacker.school.data.DbKeywords.OR;
import static knaps.hacker.school.data.DbKeywords.ORDER_BY;
import static knaps.hacker.school.data.DbKeywords.PARENS_CLOSE;
import static knaps.hacker.school.data.DbKeywords.PARENS_OPEN;
import static knaps.hacker.school.data.DbKeywords.PRIMARY_KEY_TYPE;
import static knaps.hacker.school.data.DbKeywords.Q;
import static knaps.hacker.school.data.DbKeywords.SELECT;
import static knaps.hacker.school.data.DbKeywords.TEXT_TYPE;
import static knaps.hacker.school.data.DbKeywords.VALUES;
import static knaps.hacker.school.data.DbKeywords.WHERE;

/**
 * Created by lisaneigut on 14 Sep 2013.
 */
public final class HSData {


    public HSData() {}

    public static abstract class Batch implements BaseColumns {

        public static final String TABLE_NAME = "batch";
        public static final String COLUMN_NAME_ID = "id"; // ?? Avoid collision when doing joins with HSer
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
                        COLUMN_NAME_ID + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_START_DATE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_END_DATE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_LAST_UPDATED + TEXT_TYPE + COMMA_SEP +
                        UNIQUE + PARENS_OPEN + COLUMN_NAME_ID + PARENS_CLOSE + ON + CONFLICT_REPLACE +
                        PARENS_CLOSE;

        public static final String SQL_DELETE =
                DROP_TABLE + TABLE_NAME;

        public static final String SQL_CREATE_ID_INDEX =
                CREATE_INDEX + IF_NOT_EXISTS + IDX_ID + ON + TABLE_NAME +
                        PARENS_OPEN + COLUMN_NAME_ID + PARENS_CLOSE;

        public static final String SQL_UPSERT_ALL =
                INSERT_OR_REPLACE + TABLE_NAME + PARENS_OPEN +
                        COLUMN_NAME_ID + COMMA_SEP +
                        COLUMN_NAME_NAME + COMMA_SEP +
                        COLUMN_NAME_START_DATE + COMMA_SEP +
                        COLUMN_NAME_END_DATE + COMMA_SEP +
                        COLUMN_NAME_LAST_UPDATED +
                        PARENS_CLOSE + VALUES + PARENS_OPEN +
                        Q + COMMA_SEP +
                        Q + COMMA_SEP +
                        Q + COMMA_SEP +
                        Q + COMMA_SEP +
                        Q + PARENS_CLOSE;

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

        public static final String WHERE_HAS_ID =
                COLUMN_NAME_ID + EQUALS_Q;

        public static final String SQL_SINGLE_LINE_RESULT =
                SELECT + "1 AS batches, group_concat(" + COLUMN_NAME_ID + ", ':')" +
                        FROM + TABLE_NAME + GROUP_BY + "batches";

        public static final String SQL_LAST_BATCH_UPDATE_TIME =
                SELECT + COLUMN_NAME_LAST_UPDATED + FROM + TABLE_NAME +
                        WHERE + COLUMN_NAME_ID + LIKE_Q +
                        ORDER_BY + COLUMN_NAME_LAST_UPDATED + ASC +
                        LIMIT + "1";
        public static final String SQL_RECORD_COUNT =
                SELECT + "COUNT(1)" + FROM + TABLE_NAME;
    }

    public static abstract class HSer implements BaseColumns {

        // TABLE NAME
        public static final String TABLE_NAME = "students";

        // COLUMNS
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_FIRST_NAME = "firstName";
        public static final String COLUMN_NAME_LAST_NAME = "lastName";
        public static final String COLUMN_NAME_IMAGE_URL = "imageUrl";
        public static final String COLUMN_NAME_IMAGE_FILENAME = "imageFilename";
        public static final String COLUMN_NAME_JOB = "job";
        public static final String COLUMN_NAME_SKILLS = "skills";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_TWITTER = "twitterAccount";
        public static final String COLUMN_NAME_GITHUB = "githubAccount";
        public static final String COLUMN_NAME_BATCH_ID = "batch_id";
        public static final String COLUMN_NAME_LAST_UPDATED = "updated";
        public static final String IDX_ID = "index_hackerschool_id";
        public static final String COLUMN_NAME_PHONE_NUMBER = "phone";
        public static final String COLUMN_NAME_HAS_PHOTO = "hasPhoto";
        public static final String COLUMN_NAME_IS_HACKER_SCHOOLER = "isHSer";
        public static final String COLUMN_NAME_IS_FACULTY = "isFaculty";

        private static final String _ALL =
                TABLE_NAME + DOT + COLUMN_NAME_ID + COMMA_SEP +
                        TABLE_NAME + DOT + _ID + COMMA_SEP +
                        TABLE_NAME + DOT + COLUMN_NAME_FIRST_NAME + COMMA_SEP +
                        TABLE_NAME + DOT + COLUMN_NAME_LAST_NAME + COMMA_SEP +
                        TABLE_NAME + DOT + COLUMN_NAME_HAS_PHOTO + COMMA_SEP +
                        TABLE_NAME + DOT + COLUMN_NAME_IMAGE_URL + COMMA_SEP +
                        TABLE_NAME + DOT + COLUMN_NAME_IMAGE_FILENAME + COMMA_SEP +
                        TABLE_NAME + DOT + COLUMN_NAME_JOB + COMMA_SEP +
                        TABLE_NAME + DOT + COLUMN_NAME_PHONE_NUMBER + COMMA_SEP +
                        TABLE_NAME + DOT + COLUMN_NAME_SKILLS + COMMA_SEP +
                        TABLE_NAME + DOT + COLUMN_NAME_EMAIL + COMMA_SEP +
                        TABLE_NAME + DOT + COLUMN_NAME_GITHUB + COMMA_SEP +
                        TABLE_NAME + DOT + COLUMN_NAME_TWITTER + COMMA_SEP +
                        TABLE_NAME + DOT + COLUMN_NAME_BATCH_ID + COMMA_SEP +
                        TABLE_NAME + DOT + COLUMN_NAME_IS_FACULTY + COMMA_SEP +
                        TABLE_NAME + DOT + COLUMN_NAME_IS_HACKER_SCHOOLER + COMMA_SEP +
                        TABLE_NAME + DOT + COLUMN_NAME_LAST_UPDATED;

        public static final String SELECTION_NAME_SKILLS_COMPANY =
                COLUMN_NAME_FIRST_NAME + LIKE_Q + OR +
                        COLUMN_NAME_LAST_NAME + LIKE_Q + OR +
                        COLUMN_NAME_SKILLS + LIKE_Q + OR +
                        COLUMN_NAME_JOB + LIKE_Q;


        private static String _ALL_WITH_BATCH =
                _ALL + COMMA_SEP +
                        Batch.TABLE_NAME + DOT + Batch.COLUMN_NAME_NAME + COMMA_SEP +
                        Batch.TABLE_NAME + DOT + Batch.COLUMN_NAME_START_DATE + COMMA_SEP +
                        Batch.TABLE_NAME + DOT + Batch.COLUMN_NAME_END_DATE;

        public static final String SQL_CREATE =
                CREATE_TABLE + TABLE_NAME + PARENS_OPEN +
                        _ID + PRIMARY_KEY_TYPE + COMMA_SEP +
                        COLUMN_NAME_ID + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_FIRST_NAME + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_LAST_NAME + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_HAS_PHOTO + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_IMAGE_URL + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_IMAGE_FILENAME + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_JOB + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_SKILLS + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_EMAIL + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PHONE_NUMBER + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_GITHUB + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_TWITTER + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_BATCH_ID + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_IS_HACKER_SCHOOLER + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_IS_FACULTY + INT_TYPE + COMMA_SEP +
                        COLUMN_NAME_LAST_UPDATED + TEXT_TYPE + COMMA_SEP +
                        UNIQUE + PARENS_OPEN + COLUMN_NAME_ID + PARENS_CLOSE + ON + CONFLICT_REPLACE +
                        PARENS_CLOSE;

        public static final String SQL_CREATE_ID_INDEX =
                CREATE_INDEX + IF_NOT_EXISTS + IDX_ID + ON + TABLE_NAME +
                        PARENS_OPEN + COLUMN_NAME_ID + PARENS_CLOSE;

        public static final String SQL_DELETE =
                DROP_TABLE + TABLE_NAME;

        public static final String SQL_UPSERT_ALL =
                INSERT_OR_REPLACE + TABLE_NAME + PARENS_OPEN +
                        COLUMN_NAME_ID + COMMA_SEP +
                        COLUMN_NAME_FIRST_NAME + COMMA_SEP +
                        COLUMN_NAME_LAST_NAME + COMMA_SEP +
                        COLUMN_NAME_HAS_PHOTO + COMMA_SEP +
                        COLUMN_NAME_IMAGE_URL + COMMA_SEP +
                        COLUMN_NAME_JOB + COMMA_SEP +
                        COLUMN_NAME_SKILLS + COMMA_SEP +
                        COLUMN_NAME_EMAIL + COMMA_SEP +
                        COLUMN_NAME_GITHUB + COMMA_SEP +
                        COLUMN_NAME_TWITTER + COMMA_SEP +
                        COLUMN_NAME_PHONE_NUMBER + COMMA_SEP +
                        COLUMN_NAME_IS_FACULTY + COMMA_SEP +
                        COLUMN_NAME_IS_HACKER_SCHOOLER + COMMA_SEP +
                        COLUMN_NAME_BATCH_ID + COMMA_SEP +
                        COLUMN_NAME_LAST_UPDATED +
                        PARENS_CLOSE + VALUES + PARENS_OPEN +
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
                        Q + COMMA_SEP +
                        Q + COMMA_SEP +
                        Q + COMMA_SEP +
                        Q + PARENS_CLOSE;

        public static final String JOIN_BATCH =
                " LEFT OUTER JOIN " + Batch.TABLE_NAME + ON +
                        TABLE_NAME + DOT + COLUMN_NAME_BATCH_ID + EQUALS + Batch.TABLE_NAME + DOT + Batch.COLUMN_NAME_ID + " ";

        public static final String SQL_GET_ALL =
                SELECT + _ALL_WITH_BATCH + FROM
                        + TABLE_NAME + JOIN_BATCH;

        public static final String SQL_RECORD_COUNT =
                SELECT + "COUNT(1)" + FROM + TABLE_NAME;

        public static final String GET_ALL_FILTERED =
                SELECT + _ALL_WITH_BATCH +
                        FROM + TABLE_NAME + JOIN_BATCH +
                        WHERE + COLUMN_NAME_FIRST_NAME + LIKE_Q +
                        OR + COLUMN_NAME_SKILLS + LIKE_Q;

        public static final String WHERE_HAS_ID =
                TABLE_NAME + DOT + COLUMN_NAME_ID + EQUALS_Q;

        public static final String WHERE_HAS_SQL_ID =
                TABLE_NAME + DOT + _ID + EQUALS_Q;

        public static final String SQL_GET_FILENAME =
                SELECT + COLUMN_NAME_IMAGE_FILENAME +
                        FROM + TABLE_NAME +
                        WHERE + COLUMN_NAME_IMAGE_URL + LIKE_Q;

        public static final String SQL_GET_ALL_SAVED_TO_DISK =
                SELECT + _ALL_WITH_BATCH +
                        FROM + TABLE_NAME + JOIN_BATCH +
                        WHERE + COLUMN_NAME_IMAGE_FILENAME + IS_NOT_NULL;


        public static final String SQL_NOT_LIKE_YOU =
                TABLE_NAME + DOT + COLUMN_NAME_EMAIL + NOT_LIKE_Q;


        public static final String[] PROJECTION_ALL_BATCH = {
                TABLE_NAME + DOT + _ID,
                TABLE_NAME + DOT + COLUMN_NAME_ID,
                TABLE_NAME + DOT + COLUMN_NAME_FIRST_NAME,
                TABLE_NAME + DOT + COLUMN_NAME_LAST_NAME,
                TABLE_NAME + DOT + COLUMN_NAME_HAS_PHOTO,
                TABLE_NAME + DOT + COLUMN_NAME_IMAGE_URL,
                TABLE_NAME + DOT + COLUMN_NAME_IMAGE_FILENAME,
                TABLE_NAME + DOT + COLUMN_NAME_JOB,
                TABLE_NAME + DOT + COLUMN_NAME_SKILLS,
                TABLE_NAME + DOT + COLUMN_NAME_EMAIL,
                TABLE_NAME + DOT + COLUMN_NAME_GITHUB,
                TABLE_NAME + DOT + COLUMN_NAME_TWITTER,
                TABLE_NAME + DOT + COLUMN_NAME_PHONE_NUMBER,
                TABLE_NAME + DOT + COLUMN_NAME_IS_HACKER_SCHOOLER,
                TABLE_NAME + DOT + COLUMN_NAME_IS_FACULTY,
                TABLE_NAME + DOT + COLUMN_NAME_BATCH_ID,
                TABLE_NAME + DOT + COLUMN_NAME_LAST_UPDATED,
                Batch.TABLE_NAME + DOT + Batch.COLUMN_NAME_NAME,
                Batch.TABLE_NAME + DOT + Batch.COLUMN_NAME_START_DATE,
                Batch.TABLE_NAME + DOT + Batch.COLUMN_NAME_END_DATE,
        };

        public static final String SORT_DEFAULT =
                Batch.TABLE_NAME + DOT + Batch.COLUMN_NAME_START_DATE + ASC + COMMA_SEP +
                        TABLE_NAME + DOT + COLUMN_NAME_FIRST_NAME + ASC;
    }

}
