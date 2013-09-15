package knaps.hacker.school;

import android.content.Context;
import android.content.Intent;
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

    class LoginAsyncTask extends AsyncTask<Void, Void, Boolean> {

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
        protected Boolean doInBackground(Void... params) {
            // run a network request to log me into hacker school.
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Constants.HACKER_SCHOOL_URL + Constants.LOGIN_PAGE);
//                HttpClientParams.setRedirecting(httpClient.getParams(), false);

                List<NameValuePair> formData = new ArrayList<NameValuePair>(2);
                formData.add(new BasicNameValuePair("email", mEmail));
                formData.add(new BasicNameValuePair("password", mPassword));
                httpPost.setEntity(new UrlEncodedFormEntity(formData));

                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity entity = response.getEntity();

                for (Header h : response.getAllHeaders()) {
                    Log.d("XML HEADERS", h.toString());
                }

                // TODO: Error messages
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 302 && statusCode != 200) {
                    Log.d("XML ERROR WITH STATUS CODE?", response.getStatusLine().toString());
                    return false;
                }
                // TODO: save session cookie??
                final ArrayList<Student> students = HSParser.parseBatches(entity.getContent());
                if (students.size() > 0) {
                    HSParser.writeStudentsToDatabase(students, LoginActivity.this);
                    return true;
                }

            } catch (UnsupportedEncodingException e) {
                Log.e("Error", "error!!", e);
                return false;
            } catch (ClientProtocolException e) {
                Log.e("Error", "error!!", e);
                return false;
            } catch (IOException e) {
                Log.e("Error", "error!!", e);
                return false;
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (mDestroyed) return;

            mLoadingView.setVisibility(View.GONE);
            if (result) {
                // navigate to the game page
                Intent intent = new Intent(LoginActivity.this, HSListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, "Login error. Try again.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
