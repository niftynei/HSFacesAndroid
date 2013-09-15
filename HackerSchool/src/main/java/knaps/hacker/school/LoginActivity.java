package knaps.hacker.school;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import knaps.hacker.school.data.HSDataContract;
import knaps.hacker.school.data.HSDatabaseHelper;
import knaps.hacker.school.data.HSParser;
import knaps.hacker.school.models.Student;
import knaps.hacker.school.networking.Constants;

public class LoginActivity extends Activity implements View.OnClickListener {


    View mLoadingView;
    EditText mEmailView;
    EditText mPasswordView;
    boolean mDestroyed;
    boolean mHasData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Button loginButton = (Button) findViewById(R.id.button);
        loginButton.setOnClickListener(this);

        mLoadingView = findViewById(R.id.loading_view);
        mEmailView = (EditText) findViewById(R.id.editEmail);
        mPasswordView = (EditText) findViewById(R.id.editPassword);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEmailView, InputMethodManager.SHOW_IMPLICIT);

        HSDatabaseHelper dbHelper = new HSDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        long results = DatabaseUtils.queryNumEntries(db, HSDataContract.StudentEntry.TABLE_NAME);
        if (results > 0) {
            mHasData = true;
            Button goToGameButton = (Button) findViewById(R.id.buttonGame);
            goToGameButton.setVisibility(View.VISIBLE);
            goToGameButton.setOnClickListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        mDestroyed = true;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                if (!"".equals(mEmailView.getText().toString()) && !"".equals(mPasswordView.getText().toString())) {
                    new LoginAsyncTask(mEmailView.getText().toString(), mPasswordView.getText().toString()).execute();
                }
                break;
            case R.id.buttonGame:
                final Intent intent = new Intent(this, GuessThatHSActivity.class);
                startActivity(intent);
                this.finish();
                break;
            default:
                // no default
        }
    }

    class LoginAsyncTask extends AsyncTask<Void, Void, String> {

        final String mPassword;
        final String mEmail;

        public LoginAsyncTask(final String email, final String password) {
            mPassword = password;
            mEmail = email;
        }

        @Override
        protected void onPreExecute() {
            mLoadingView.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            // run a network request to log me into hacker school.
            try {
                final DefaultHttpClient httpClient = new DefaultHttpClient();
                final HttpPost httpPost = new HttpPost(Constants.HACKER_SCHOOL_URL + Constants.LOGIN_PAGE);
                final List<NameValuePair> formData = new ArrayList<NameValuePair>(2);
                formData.add(new BasicNameValuePair("email", mEmail));
                formData.add(new BasicNameValuePair("password", mPassword));
                httpPost.setEntity(new UrlEncodedFormEntity(formData));

                final HttpResponse response = httpClient.execute(httpPost);
                final HttpEntity entity = response.getEntity();

                // TODO: save session cookie??
//                for (final Header h : response.getAllHeaders()) {
//                    Log.d("XML HEADERS", h.toString());
//                }

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 302 && statusCode != 200) {
                    return "Request failed. Error:" + statusCode + " Check username and password.";
                }

                final HashSet<String> existingBatches = getExistingBatches();
                final ArrayList<Student> students = HSParser.parseBatches(entity.getContent(), existingBatches);
                if (students.size() > 0) {
                    HSParser.writeStudentsToDatabase(students, LoginActivity.this);
                    return null;
                }
                else if (existingBatches.size() > 0 && mHasData) {
                    return null;
                }
                else {
                    return "No results returned. Check username and password.";
                }

            } catch (UnsupportedEncodingException e) {
                Log.e("Error", "error!!", e);
                return "Error code 100";
            } catch (ClientProtocolException e) {
                Log.e("Error", "error!!", e);
                return "Error code 200";
            } catch (IOException e) {
                Log.e("Error", "error!!", e);
                return "Error code 300";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (mDestroyed) return;

            mLoadingView.setVisibility(View.GONE);
            if (result == null) {
                // navigate to the game page
                Intent intent = new Intent(LoginActivity.this, HSListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, "Login error. " + result, Toast.LENGTH_LONG).show();
            }
        }
    }

    private HashSet<String> getExistingBatches() {
        final HashSet<String> batches = new HashSet<String>();
        // sql statement for distinct batch names
        final SQLiteDatabase db = new HSDatabaseHelper(this).getReadableDatabase();
        final Cursor cursor = db.query(true, HSDataContract.StudentEntry.TABLE_NAME, new String[]{HSDataContract.StudentEntry.COLUMN_NAME_BATCH_ID}, null,
                null, HSDataContract.StudentEntry.COLUMN_NAME_BATCH_ID, null, null, null);

        Log.d("XML -- cursor for batch", DatabaseUtils.dumpCursorToString(cursor));
        if (cursor != null) {
            while (cursor.moveToNext()) {
                batches.add(cursor.getString(
                        cursor.getColumnIndex(HSDataContract.StudentEntry.COLUMN_NAME_BATCH_ID)).trim().toLowerCase());
            }
        }
        cursor.close();
        return batches;
    }
}
