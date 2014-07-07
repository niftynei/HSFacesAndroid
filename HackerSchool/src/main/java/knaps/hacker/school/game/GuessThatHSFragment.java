package knaps.hacker.school.game;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Random;

import knaps.hacker.school.R;
import knaps.hacker.school.data.DbKeywords;
import knaps.hacker.school.data.HSData;
import knaps.hacker.school.data.HackerSchoolContentProvider;
import knaps.hacker.school.models.Student;
import knaps.hacker.school.networking.ImageDownloads;
import knaps.hacker.school.utils.Constants;
import knaps.hacker.school.utils.KeyboardUtil;
import knaps.hacker.school.utils.SharedPrefsUtil;
import knaps.hacker.school.utils.StringUtil;

public class GuessThatHSFragment extends Fragment implements View.OnClickListener,
                                                             LoaderManager.LoaderCallbacks<Cursor>,
                                                             TextView.OnEditorActionListener,
                                                             ImageDownloads.ImageDownloadCallback {

    private static final String GUESS_COUNT = "guess_count";
    private static final String CORRECT_COUNT = "correct_count";
    private static final String SUCCESS_MESSAGE_COUNT = "success_count";
    private static final String HINT_MESSAGE_COUNT = "hint_count";
    private static final String GAME_OVER = "game_over";
    private static final long ALL_THE_BATCHES = -1L;
    private static final String SEED = "seed";

    private View mStartScreen;
    private View mGameScreen;

    private GameTileLayout mGameTileLayout;
    private EditText mEditGuess;
    private Button mGuess;
    private Button mRestartButton;
    private TextView mFlavorText;
    private long mBatchId;
    private TextView mGuessCounter;
    private TextView mBatchText;

    private Student mCurrentStudent;
    private Cursor mStudentCursor;
    private int mCurrentScore;
    private int mCurrentGuesses;
    private int mHintCount = 0;
    private int mGameMax = Integer.MAX_VALUE;
    private int mSuccessMessageCount = 0;
    private static int[] sSuccessMessages = {R.string.success_generic_one, R.string.success_generic_two, R.string.success_generic_three};
    private static String[] sHintMessages = new String[] {"Give it a try.", "Not a guess?", "Hint: Starts with %s"};

    private LruCache<String, Bitmap> mMemoryCache;

    private boolean mIsRestart = false;
    private boolean mGameOver = false;
    private long mSeed;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_guess, container, false);
        mGameTileLayout = (GameTileLayout) view.findViewById(R.id.gameTile);

        mEditGuess = (EditText) view.findViewById(R.id.editGuess);
        mEditGuess.setOnEditorActionListener(this);
        mGuess = (Button) view.findViewById(R.id.buttonGuess);
        mGuess.setOnClickListener(this);
        mGuess.setEnabled(false);
        mRestartButton = (Button) view.findViewById(R.id.buttonRestart);
        mRestartButton.setOnClickListener(this);
        mFlavorText = (TextView) view.findViewById(R.id.text_flavor);

        mGuessCounter = (TextView) view.findViewById(R.id.textGuessCount);
        mBatchText = (TextView) view.findViewById(R.id.textBatchName);
        mStartScreen = view.findViewById(R.id.start_screen);
        mGameScreen = view.findViewById(R.id.game_play);
        view.findViewById(R.id.start_button).setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        ImageDownloads.RetainFragment retainFragment =
                ImageDownloads.RetainFragment.findOrCreateRetainFragment(activity.getFragmentManager());
        mMemoryCache = retainFragment.mRetainedCache;
        if (mMemoryCache == null) {
            mMemoryCache = ImageDownloads.getBitmapMemoryCache();
            retainFragment.mRetainedCache = mMemoryCache;
        }
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mIsRestart = true;
            mCurrentGuesses = savedInstanceState.getInt(GUESS_COUNT);
            mCurrentScore = savedInstanceState.getInt(CORRECT_COUNT);
            mSuccessMessageCount = savedInstanceState.getInt(SUCCESS_MESSAGE_COUNT);
            mHintCount = savedInstanceState.getInt(HINT_MESSAGE_COUNT);
            mGameMax = savedInstanceState.getInt(Constants.GAME_MAX);
            mGameOver = savedInstanceState.getBoolean(GAME_OVER);
            mBatchId = savedInstanceState.getLong(Constants.BATCH_ID);
            mSeed = savedInstanceState.getLong(SEED);
        }

        if (mIsRestart) {
            mStartScreen.setVisibility(View.GONE);
        }
        else {
            mGameScreen.setVisibility(View.GONE);
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private void showGameSettingsDialog() {
        FragmentManager fm = getFragmentManager();
        ChooseGameFragment fragment = new ChooseGameFragment();
        fragment.setOnChooseGameListener(new ChooseGameFragment.OnChooseGameListener() {
            @Override
            public void onChooseGame(long batchId, String batchName, long gameMax) {
                mBatchId = batchId;
                mGameMax = (int) gameMax;
                restartGame();
            }
        });
        fragment.show(fm, "fragment_choose_dialog");
    }

    @Override
    public void onSaveInstanceState(Bundle icicle) {
        icicle.putLong(SEED, mSeed);
        icicle.putInt(GUESS_COUNT, mCurrentGuesses);
        icicle.putInt(CORRECT_COUNT, mCurrentScore);
        icicle.putInt(SUCCESS_MESSAGE_COUNT, mSuccessMessageCount);
        icicle.putInt(HINT_MESSAGE_COUNT, mHintCount);
        icicle.putInt(Constants.GAME_MAX, mGameMax);
        icicle.putBoolean(GAME_OVER, mGameOver);
        icicle.putLong(Constants.BATCH_ID, mBatchId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonGuess:
                mGuess.setClickable(false);
                final String guess = mEditGuess.getText().toString().toLowerCase().trim();
                if ("".equals(guess)) {
                    Toast.makeText(getActivity(), String.format(sHintMessages[mHintCount], mCurrentStudent.firstName.charAt(0)), Toast.LENGTH_SHORT).show();
                    incrementHint();
                    mGuess.setClickable(true);
                }
                else {
                    if (runGuess(guess)) {
                        showSuccess();
                    }
                    else {
                        showFail();
                    }
                    mEditGuess.setEnabled(false);
                }
                break;
            case R.id.buttonRestart:
            case R.id.start_button:
                showGameSettingsDialog();
                break;
            default:
                //no default
        }
    }

    private void incrementHint() {
        mHintCount = Math.min(mHintCount + 1, sHintMessages.length - 1);
    }

    private void displayScore() {
        if (mStudentCursor != null) {
            int count = mGameMax - mCurrentGuesses - 1;
            if (count > -1) mGuessCounter.setText(String.valueOf(count));
        }
    }

    private void showNextStudent() {
        mGameTileLayout.showImage();
        if (mStudentCursor.isLast() || mGameMax - 1 <= mStudentCursor.getPosition()) {
            showEndGame();
        }
        else if (mStudentCursor.getCount() > 0) {
            mStudentCursor.moveToNext();
            showStudent();
            displayScore();
            mGuess.setClickable(true);
            mEditGuess.setEnabled(true);
        }
        else {
            showNoStudentsAndExit();
        }
    }

    private void showNoStudentsAndExit() {
        Toast.makeText(getActivity(), "No valid students found. Try connecting to the internet.", Toast.LENGTH_SHORT).show();
    }

    private void showStudent() {
        mCurrentStudent = new knaps.hacker.school.models.Student(mStudentCursor);
        mEditGuess.setText("");
        mHintCount = 0;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            KeyboardUtil.showKeyboard(getActivity(), mEditGuess);
        }

        new ImageDownloads.HSGetImageTask(mCurrentStudent.image, getActivity(), this).execute();
    }

    private void restartGame() {
        mGameScreen.setVisibility(View.VISIBLE);
        mStartScreen.setVisibility(View.GONE);

        mGuess.setVisibility(View.VISIBLE);
        mEditGuess.setVisibility(View.VISIBLE);
        mGameTileLayout.setVisibility(View.VISIBLE);
        mRestartButton.setVisibility(View.GONE);
        mFlavorText.setVisibility(View.GONE);

        mCurrentScore = 0;
        mCurrentGuesses = 0;
        mSuccessMessageCount = 0;
        mHintCount = 0;
        mGameOver = false;
        mIsRestart = false;
        mSeed = System.nanoTime() % 1000000;

        getLoaderManager().restartLoader(0, null, this);
    }

    private void showEndGame() {
        mGameOver = true;

        mEditGuess.setVisibility(View.GONE);
        mGuess.setVisibility(View.GONE);
        mGameTileLayout.setVisibility(View.GONE);

        mRestartButton.setVisibility(View.VISIBLE);
        mFlavorText.setVisibility(View.VISIBLE);

        mGameTileLayout.setHSPicture(R.drawable.ic_launcher);
        double finalScore = mCurrentScore / (mCurrentGuesses * 1.0) * 100;
        mGuessCounter.setText(String.format("Final Score: %.0f%%", finalScore));

        SharedPrefsUtil.saveUserHighScore(getActivity(), (int) Math.round(finalScore));
        SharedPrefsUtil.saveUserStats(getActivity(), mCurrentScore, mCurrentGuesses);

        showFinalFlavorText(finalScore);
    }

    private void showFinalFlavorText(double finalScore) {
        int message;
        if (finalScore >= 90) {
            message = R.string.score_90_aces;
        }
        else if (finalScore >= 70) {
            message = R.string.score_70_source;
        }
        else {
            message = R.string.score_sub70_hh;
        }
        mFlavorText.setText(message);
    }

    private void showFail() {
        mGameTileLayout.showFail(getString(R.string.fail_message, mCurrentStudent.firstName), mGameTileCallback);
    }

    private void showSuccess() {
        mGameTileLayout.showSuccess(getSuccessMessage(), mGameTileCallback);
        incrementSuccess();
    }

    private GameTileCallback mGameTileCallback = new GameTileCallback() {
        @Override
        public void onAnimationEnd() {
            mGuessCounter.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showNextStudent();
                }
            }, 1700);
        }
    };

    private String getSuccessMessage() {
        if (!"".equals(
                mCurrentStudent.mSkills) && mCurrentStudent.mSkills != null && mSuccessMessageCount % 3 == 2) {
            String[] skills = mCurrentStudent.mSkills.split(",");
            String skill = mCurrentStudent.mSkills;
            if (skills.length > 0) {
                skill = skills[0];
            }
            return getString(R.string.success_skill, mCurrentStudent.firstName, skill);
        }
        else if (!""
                .equals(mCurrentStudent.mJob) && mCurrentStudent.mJob != null && mSuccessMessageCount % 3 == 1) {
            return getString(R.string.success_skill, mCurrentStudent.firstName, mCurrentStudent.mJob);
        }
        else {
            return getString(sSuccessMessages[mSuccessMessageCount], mCurrentStudent.firstName);
        }
    }

    private void incrementSuccess() {
        mSuccessMessageCount++;
        mSuccessMessageCount = mSuccessMessageCount % 3;
    }

    private boolean runGuess(String guess) {
        boolean returnValue = false;
        guess = StringUtil.removeAccents(guess).toLowerCase();
        final String name = StringUtil.removeAccents(mCurrentStudent.firstName).toLowerCase();
        final String[] names = name.split(" ");
        if (guess.equals(name)
                || guess.equals(names[0])
                || guess.equals(names[names.length - 1])) {
            mCurrentScore++;
            returnValue = true;
        }
        mCurrentGuesses++;
        return returnValue;
    }

    private void initGame() {
        if (mGameMax == Constants.INVALID_MIN) {
            mGameMax = mStudentCursor.getCount();
        }

        if (mGameOver) {
            showEndGame();
        }
        else {
            if (!mIsRestart) {
                showNextStudent();
            }
            else {
                if (mCurrentGuesses <= mStudentCursor.getCount()) mStudentCursor.moveToPosition(mCurrentGuesses);
                showStudent();
            }
            mGuess.setEnabled(true);
            displayScore();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String limit = null;
        StringBuilder selection = new StringBuilder();
        String[] selectionArgs = new String[0];
        if (!ImageDownloads.isOnline(getActivity())) {
            if (selection.length() > 0) selection.append(DbKeywords.AND);
            selection.append(HSData.HSer.COLUMN_NAME_IMAGE_FILENAME).append(DbKeywords.IS_NOT_NULL);
        }
        if (mBatchId > ALL_THE_BATCHES) {
            if (selection.length() > 0) selection.append(DbKeywords.AND);
            selection.append(HSData.HSer.COLUMN_NAME_BATCH_ID).append(DbKeywords.EQUALS_Q);

            if (selection.length() > 0) selection.append(DbKeywords.AND);
            selection.append(HSData.HSer.COLUMN_NAME_IMAGE_URL).append(DbKeywords.NOT_LIKE_Q);
            selectionArgs = new String[] {String.valueOf(mBatchId), "%no_photo%"};
        }
        if (!TextUtils.isEmpty(SharedPrefsUtil.getUserEmail(getActivity()))) {
            if (selection.length() > 0) selection.append(DbKeywords.AND);
            selection.append(HSData.HSer.SQL_NOT_LIKE_YOU);
            selectionArgs = Arrays.copyOf(selectionArgs, selectionArgs.length + 1);
            selectionArgs[selectionArgs.length - 1] = "%" + SharedPrefsUtil.getUserEmail(getActivity()) + "%";
        }
        return new CursorLoader(getActivity(),
                HackerSchoolContentProvider.Uris.STUDENTS.getUri()
                                                         .buildUpon().appendQueryParameter(DbKeywords.LIMIT, limit).build(),
                HSData.HSer.PROJECTION_ALL_BATCH,
                selection.toString(),
                selectionArgs,
                HSData.HSer.SORT_DEFAULT
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> objectLoader, Cursor o) {
        mStudentCursor = new HSRandomCursorWrapper(o, mSeed);
        if (o != null && o.getCount() > 0 && getActivity() != null) {
            initGame();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> objectLoader) {
        mStudentCursor = null;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if ((event != null && event.getKeyCode() == KeyEvent.FLAG_EDITOR_ACTION) ||
                (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO)) {
            mGuess.performClick();
            final InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditGuess.getWindowToken(), 0);
            return true;
        }
        return false;
    }

    @Override
    public void onPreImageDownload() {
        mGameTileLayout.clearHSPicture();
        int drawable = Constants.sLoadingIcons[Math.abs(new Random().nextInt() % Constants.sLoadingIcons.length)];
        mGameTileLayout.setHSPicture(getResources().getDrawable(drawable));
    }

    @Override
    public void onImageDownloaded(Bitmap bitmap) {
        mGameTileLayout.setHSPicture(bitmap);
    }

    @Override
    public void onImageFailed(boolean wasNetworkError) {
        if (getActivity() != null) {
            mGameTileLayout.setHSPicture(R.drawable.ic_launcher);

            if (!wasNetworkError) {
                Toast.makeText(getActivity(), "Error loading image.", Toast.LENGTH_SHORT).show();
            }
            showNextStudent();
        }
    }
}
