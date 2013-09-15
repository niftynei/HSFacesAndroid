package knaps.hacker.school;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.util.LruCache;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import knaps.hacker.school.R;
import knaps.hacker.school.data.HSDataContract;
import knaps.hacker.school.data.HSDatabaseHelper;
import knaps.hacker.school.data.SQLiteCursorLoader;
import knaps.hacker.school.models.Student;
import knaps.hacker.school.networking.Constants;
import knaps.hacker.school.networking.ImageDownloads;

public class GuessThatHSActivity extends FragmentActivity implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor>, TextView.OnEditorActionListener {

    private ImageView mHsPicture;
    private EditText mEditGuess;
    private TextView mTextScore;
    private Button mGuess;

    private Student mCurrentStudent;
    private static Cursor mStudentCursor;
    private int mCurrentScore;
    private int mCurrentGuesses;

    private LruCache<String, Bitmap> mMemoryCache;
    private RetainFragment mRetainedFragment;

    private static final String SAVED_CURSOR = "saved_cursor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);

        mHsPicture = (ImageView) findViewById(R.id.imageStudent);
        mEditGuess = (EditText) findViewById(R.id.editGuess);
        mEditGuess.setOnEditorActionListener(this);
        mTextScore = (TextView) findViewById(R.id.textScore);
        mGuess = (Button) findViewById(R.id.buttomGuess);
        mGuess.setOnClickListener(this);

        // TODO: save high score to preferences (so you can beat yourself!)
        // TODO: Settings -- save your email and password
        // TODO: randomize how hackerschoolers are selected
        // TODO: check network connectivity!?!?!

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        mRetainedFragment = RetainFragment.findOrCreateRetainFragment(getSupportFragmentManager());
        mMemoryCache = mRetainedFragment.mRetainedCache;
        if (mMemoryCache == null) {
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount() / 1024;
                }
            };
            mRetainedFragment.mRetainedCache = mMemoryCache;
        }

        mStudentCursor = mRetainedFragment.mRetainedCursor;
        if (mStudentCursor == null || mStudentCursor.isClosed()) {
            getSupportLoaderManager().initLoader(0, null, this);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.guess_that_h, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle icicle) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttomGuess:
                final String guess = mEditGuess.getText().toString().toLowerCase().trim();
                if ("".equals(guess)) {
                    Toast.makeText(this, String.format(mHintMessages[mHintCount], mCurrentStudent.mName.charAt(0)), Toast.LENGTH_SHORT).show();
                    incrementHint();
                }
                else if (runGuess(guess)) {
                    showSuccess();
                    mCurrentScore++;
                    mCurrentGuesses++;
                }
                else {
                    showFail();
                    mCurrentGuesses++;
                }
                displayScore();
                break;
            default:
                //no default
        }
    }

    String[] mHintMessages = new String[] { "Give it a try.", "'Dave' is a safe choice.",
            "Hint: Starts with %s" };
    int mHintCount = 0;
    private void incrementHint() {
        mHintCount = Math.min(mHintCount + 1, 2);
    }


    private void displayScore() {
        if (mCurrentGuesses != 0) {
            final float score = (float) (mCurrentScore / (mCurrentGuesses * 1.0) * 100);
            mTextScore.setText(getString(R.string.current_score, score));
        }
    }

    private void showNextStudent(boolean isFirst) {
        if (isFirst) {
            mStudentCursor.moveToFirst();
        }
        else {
            mStudentCursor.moveToNext();
        }

        if (mStudentCursor.isAfterLast()) {
            showEndGame();
        }
        else {
            mCurrentStudent = new Student(mStudentCursor);
            mEditGuess.setText("");
            mHintCount = 0;
            new HSImageDownloadTask(mCurrentStudent.mImageUrl, mHsPicture).execute();
        }
    }

    private void showEndGame() {
        mEditGuess.setVisibility(View.GONE);
        mGuess.setVisibility(View.GONE);
        mHsPicture.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
    }

    private void showFail() {
        Toast.makeText(this, "This is " + mCurrentStudent.mName, Toast.LENGTH_SHORT).show();
        mTextScore.postDelayed(new Runnable() {
            @Override
            public void run() {
                showNextStudent(false);
            }
        }, Toast.LENGTH_LONG + 200);
    }

    int mSuccessMessageCount = 0;
    String[] mSuccessMessages = { "010110010110010101110011", "Correct.", "Yes." };
    private void showSuccess() {
        if (!"".equals(mCurrentStudent.mSkills) && mCurrentStudent.mSkills != null && mSuccessMessageCount % 3 == 2) {
            String[] skills = mCurrentStudent.mSkills.split(",");
            String skill = mCurrentStudent.mSkills;
            if (skills.length > 0) {
                skill = skills[0];
            }
            Toast.makeText(this, "You got it. " + mCurrentStudent.mName + " is a " + skill + " ninja.",
                    Toast.LENGTH_SHORT).show();
        }
        else if (!"".equals(mCurrentStudent.mJob) && mCurrentStudent.mJob != null && mSuccessMessageCount % 3 == 1) {
            Toast.makeText(this, "Right. " + mCurrentStudent.mName + " works at " + mCurrentStudent.mJob,
                    Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, mSuccessMessages[mSuccessMessageCount], Toast.LENGTH_SHORT).show();
        }
        incrementSuccess();
        mTextScore.postDelayed(new Runnable() {
            @Override
            public void run() {
                showNextStudent(false);
            }
        }, Toast.LENGTH_LONG + 200);
    }

    private void incrementSuccess() {
        mSuccessMessageCount++;
        mSuccessMessageCount = mSuccessMessageCount % 3;
    }

    private boolean runGuess(final String guess) {
        final String first = mCurrentStudent.mName.split(" ")[0];
        if (guess.equals(mCurrentStudent.mName.toLowerCase()) || guess.equals(first.toLowerCase())) {
            return true;
        }
        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        SQLiteCursorLoader loader = new SQLiteCursorLoader(this, new HSDatabaseHelper(this),
                HSDataContract.StudentEntry.TABLE_NAME, HSDataContract.StudentEntry.PROJECTION_ALL,
                HSDataContract.StudentEntry.SORT_DEFAULT);
        if (mStudentCursor != null && !mStudentCursor.isClosed()) {
            loader.deliverResult(mStudentCursor);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> objectLoader, Cursor o) {
        mStudentCursor = o;
        mRetainedFragment.mRetainedCursor = o;
        showNextStudent(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> objectLoader) {
        mStudentCursor = null;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (event != null && event.getKeyCode() == KeyEvent.FLAG_EDITOR_ACTION) {
            mGuess.performClick();
            return true;
        }
        return false;
    }

    class HSImageDownloadTask extends AsyncTask<Void, Void, Bitmap> {

        final private String mUrl;
        final private ImageView mImageView;
        public HSImageDownloadTask(String url, ImageView imageView) {
            mUrl = url;
            mImageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            mImageView.setBackgroundResource(android.R.color.background_dark);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = null;
            bitmap = mMemoryCache.get(mUrl);

            if (bitmap == null) {
                bitmap = ImageDownloads.loadBitmap(Constants.HACKER_SCHOOL_URL + mUrl);
                new ImageDownloads.SaveToCacheTask(mMemoryCache, bitmap, mUrl).execute();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null && mImageView != null) {
                mImageView.setImageBitmap(bitmap);
                mImageView.setBackgroundDrawable(null);
            }
            else if (mImageView != null) {
                mImageView.setBackgroundDrawable(null);
                mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
                Toast.makeText(GuessThatHSActivity.this, "Error loading image.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    static class RetainFragment extends Fragment {
        private static final String TAG = "RetainFragment";
        public LruCache<String, Bitmap> mRetainedCache;
        public Cursor mRetainedCursor;

        public RetainFragment() {}

        public static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
            RetainFragment fragment = (RetainFragment) fm.findFragmentByTag(TAG);
            if (fragment == null) {
                fragment = new RetainFragment();
            }
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }
    }
}
