package knaps.hacker.school;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import knaps.hacker.school.data.HSData;
import knaps.hacker.school.data.HSDatabaseHelper;
import knaps.hacker.school.game.GuessThatHSActivity;
import knaps.hacker.school.models.Student;
import knaps.hacker.school.networking.DownloadTaskFragment;
import knaps.hacker.school.networking.HSOAuthService;
import knaps.hacker.school.networking.ImageDownloads;
import knaps.hacker.school.utils.AppUtil;
import knaps.hacker.school.utils.Constants;
import knaps.hacker.school.utils.SharedPrefsUtil;

public class LoginActivity extends BaseFragmentActivity implements View.OnClickListener {

    boolean mHasData;
    private DownloadTaskFragment mDownloadFragment;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupActionBar();

        mWebView = (WebView) findViewById(R.id.webView);

        if (!HSOAuthService.getService().isAuthorized()) {
            // TODO: what happens after the 2 hour lease has expired??
            setupWebView();
        }
        else {
            // go to the HSListActivity
            Intent listIntent = new Intent(this, HSListActivity.class);
            startActivity(listIntent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        final HSDatabaseHelper dbHelper = new HSDatabaseHelper(this);
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        long numHackerSchoolers = 0;
        try {
            numHackerSchoolers = DatabaseUtils.queryNumEntries(db, HSData.HSer.TABLE_NAME);
        }
        finally {
            db.close();
        }

        if (numHackerSchoolers > 0) {
            mHasData = true;
            new LoadUserData().execute();
        }

    }

    private void setupWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                Log.d("URI", url);
                if (url.startsWith(Constants.REDIRECT_URI)) {
                    Uri uri = Uri.parse(url);
                    if (uri.getQueryParameter("code") != null) {
                        String code = uri.getQueryParameter("code");
                        HSOAuthService.getService().getAccessToken(code);
                        return true;
                    }
                    else if (uri.getQueryParameter("error") != null) {
                        String message = uri.getQueryParameter("error_message");
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                        mWebView.setVisibility(View.GONE);
                        // TODO: error messaging for URL stuffs
                    }
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(final WebView view, final String url, final Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });
        mWebView.loadUrl(HSOAuthService.getService().getAuthUrl());
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (AppUtil.isHoneycomb()) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.action_refresh_data).setVisible(mHasData);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh_data:
                break;
            default:
                return super.onMenuItemSelected(featureId, item);
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                //if (!"".equals(mEmailView.getText().toString()) && !"".equals(mPasswordView.getText().toString())) {
                //    final String email = mEmailView.getText().toString();
                //    final String password = mPasswordView.getText().toString();
                //    mDownloadFragment = new DownloadTaskFragment(email, password, mHasData);
                //    getSupportFragmentManager().beginTransaction().add(mDownloadFragment, "download_task").commit();
                //    SharedPrefsUtil.saveUserEmail(this, email);
                //}
                break;
            case R.id.buttonGame:
                Intent playGame = new Intent(LoginActivity.this, GuessThatHSActivity.class);
                playGame.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(playGame);
                break;
            case R.id.buttonBrowse:
                Intent browse = new Intent(LoginActivity.this, HSListActivity.class);
                browse.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(browse);
                break;
            case R.id.textName:
                // open a dialog to show your highest score :0
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getHighScores().toString());
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();
                break;
            default:
                // no default
        }
    }

    private StringBuilder getHighScores() {
        StringBuilder sb = new StringBuilder("High Score: ");
        sb.append(String.format("%.2f%%", (double) SharedPrefsUtil.getHighScore(this)));
        sb.append("\n");
        sb.append("Lifetime Score: ");
        sb.append(SharedPrefsUtil.getAllTimeScore(this));
        sb.append("\n");
        sb.append("Total Faces Seen: ");
        sb.append(SharedPrefsUtil.getTries(this));
        return sb;
    }

    public void onPostExecute(String result) {
        if (result == null) {
            // navigate to the game page
            Intent intent = new Intent(LoginActivity.this, GuessThatHSActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            mDownloadFragment = null;
        }
        else {
            Toast.makeText(LoginActivity.this, "Login error. " + result, Toast.LENGTH_LONG).show();
        }
    }

    class LoadUserData extends AsyncTask<Void, Void, Student>
            implements ImageDownloads.ImageDownloadCallback {

        @Override
        protected Student doInBackground(Void... params) {
            // get student data
            Student student = new HSDatabaseHelper(LoginActivity.this).getLoggedInStudent(LoginActivity.this);
            // load the image
            new ImageDownloads.HSGetImageTask(student.image, LoginActivity.this, this).execute();
            return student;
        }

        @Override
        protected void onPostExecute(Student student) {
            if (student != null) {
                TextView user = (TextView) findViewById(R.id.textName);
                user.setText(getString(R.string.hi) + " " + student.firstName.split(" ")[0]);
            }
        }

        @Override
        public void onPreImageDownload() {
            // Nothing
        }

        @Override
        public void onImageDownloaded(Bitmap bitmap) {
            final TextView user = (TextView) findViewById(R.id.textName);
            final DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int dimens = Math.round(dm.density * 30);
            final Drawable drawable = new BitmapDrawable(getResources(),
                    Bitmap.createScaledBitmap(bitmap, dimens, dimens, true));
            user.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);

            user.setOnClickListener(LoginActivity.this);
        }

        @Override
        public void onImageFailed(boolean bool) {
            // don't do anything, yo
        }
    }
}
