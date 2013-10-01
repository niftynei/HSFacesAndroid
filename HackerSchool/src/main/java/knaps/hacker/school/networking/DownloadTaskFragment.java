package knaps.hacker.school.networking;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import knaps.hacker.school.BuildConfig;
import knaps.hacker.school.data.HSDatabaseHelper;
import knaps.hacker.school.data.HSParser;
import knaps.hacker.school.models.Student;
import knaps.hacker.school.utils.Constants;

/**
 * Created by lisaneigut on 22 Sep 2013.
 */
public class DownloadTaskFragment extends Fragment {

    private final boolean mHasData;
    private TaskCallbacks mCallbacks;
    private LoginAsyncTask mTask;
    private String mEmail;
    private String mPassword;

    public static interface TaskCallbacks {
        void onPreExecute();

        void onPostExecute(String message);

        void onCancelled();
    }


    public DownloadTaskFragment(final String email, final String password, final boolean hasData) {
        mEmail = email;
        mPassword = password;
        mHasData = hasData;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mCallbacks = (TaskCallbacks) getActivity();
        mTask = (LoginAsyncTask) new LoginAsyncTask(mEmail, mPassword).execute();
    }

    public boolean isTaskRunning() {
        return mTask != null;
    }

    private void logTiming(long time, String eventName) {
        if (!BuildConfig.DEBUG) {
            final Tracker easyTracker = EasyTracker.getInstance(getActivity());

            if (easyTracker != null)
                easyTracker.send(MapBuilder
                        .createTiming("app_inits",    // Timing category (required)
                                time,       // Timing interval in milliseconds (required)
                                eventName,  // Timing name
                                null)           // Timing label
                        .build());
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
            if (mCallbacks != null)
                mCallbacks.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            // run a network request to log me into hacker school.
            if (ImageDownloads.isOnline(getActivity())) {
                try {
                    final DefaultHttpClient httpClient = new DefaultHttpClient();
                    final HttpPost httpPost = new HttpPost(
                            Constants.HACKER_SCHOOL_URL + Constants.LOGIN_PAGE);
                    final List<NameValuePair> formData = new ArrayList<NameValuePair>(2);
                    formData.add(new BasicNameValuePair("email", mEmail));
                    formData.add(new BasicNameValuePair("password", mPassword));
                    httpPost.setEntity(new UrlEncodedFormEntity(formData));

                    long starTimingDownload = System.currentTimeMillis();
                    final HttpResponse response = httpClient.execute(httpPost);
                    final HttpEntity entity = response.getEntity();
                    long endTimingDownload = System.currentTimeMillis() - starTimingDownload;
                    logTiming(endTimingDownload, "download_data");

                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode != 200) {
                        return "Request failed. Error:" + statusCode + " Check username and password.";
                    }

                    final ArrayList<String> existingBatches = new HSDatabaseHelper(getActivity())
                            .getExistingBatches();

                    long startTimingBatches = System.currentTimeMillis();
                    final ArrayList<Student> students = HSParser
                            .parseBatches(entity.getContent(), existingBatches);
                    long endTimingBatches = System.currentTimeMillis() - startTimingBatches;
                    logTiming(endTimingBatches, "parse_batches");

                    if (students.size() > 0) {
                        long startTimingWriteDb = System.currentTimeMillis();
                        HSParser.writeStudentsToDatabase(students, getActivity());
                        long endTimingWriteDb = System.currentTimeMillis() - startTimingWriteDb;
                        logTiming(endTimingWriteDb, "write_db");
                        return null;
                    }
                    else if (existingBatches.size() > 0 && mHasData) {
                        return null;
                    }
                    else {
                        return "No results returned. Check username and password.";
                    }

                }
                catch (UnsupportedEncodingException e) {
                    Log.e("Error", "error!!", e);
                    return "Error with network request: 100";
                }
                catch (ClientProtocolException e) {
                    Log.e("Error", "error!!", e);
                    return "Error with network request: 200";
                }
                catch (IOException e) {
                    Log.e("Error", "error!!", e);
                    return "Error with network request: 300";
                }
                catch (SAXException e) {
                    Log.e("Error", "error!!", e);
                    return "Error with parsing: 400";
                }
                catch (XPathExpressionException e) {
                    Log.e("Error", "error!!", e);
                    return "Error with parsing: 500";
                }
                catch (TransformerConfigurationException e) {
                    Log.e("Error", "error!!", e);
                    return "Error with parsing: 600";
                }
            }
            else {
                return "Network unavailable. Make sure you're connected, then try again.";
            }
        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) mCallbacks.onCancelled();
        }

        @Override
        protected void onPostExecute(String result) {
            mTask = null;
            if (mCallbacks != null) mCallbacks.onPostExecute(result);
        }
    }
}
