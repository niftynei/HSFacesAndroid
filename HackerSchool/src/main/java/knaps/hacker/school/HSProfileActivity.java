package knaps.hacker.school;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.protocol.HTTP;

import java.util.Arrays;
import java.util.Random;

import knaps.hacker.school.models.Student;
import knaps.hacker.school.networking.ImageDownloads;
import knaps.hacker.school.utils.AppUtil;
import knaps.hacker.school.utils.Constants;

public class HSProfileActivity extends BaseFragmentActivity
        implements View.OnClickListener, ImageDownloads.ImageDownloadCallback {

    private ImageView mImageView;
    private TextView mNameView;
    private View mJobLabel;
    private TextView mJobView;
    private TextView mSkillsView;
    private View mSkillsLabel;
    private ImageButton mGithubButton;
    private ImageButton mTwitterButton;
    private ImageButton mEmailButton;
    private ImageButton mSmsButton;
    private TextView mBatchView;
    private Student mStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // Show the Up button in the action bar.
        setupActionBar();

        // Get all the views
        mImageView = (ImageView) findViewById(R.id.imageStudent);
        mNameView = (TextView) findViewById(R.id.textName);
        mBatchView = (TextView) findViewById(R.id.textBatch);
        mSkillsView = (TextView) findViewById(R.id.textPasswordNotice);
        mSkillsLabel = findViewById(R.id.textSkillsLabel);
        mJobLabel = findViewById(R.id.textJobLabel);
        mJobView = (TextView) findViewById(R.id.textJob);
        mGithubButton = (ImageButton) findViewById(R.id.buttonGithub);
        mTwitterButton = (ImageButton) findViewById(R.id.buttonTwitter);
        mSmsButton = (ImageButton) findViewById(R.id.buttonSms);
        mEmailButton = (ImageButton) findViewById(R.id.buttonEmail);

        mStudent = (Student) getIntent().getSerializableExtra(Constants.STUDENT);
        populateViews(mStudent);
    }

    private void populateViews(Student student) {
        new ImageDownloads.HSGetImageTask(student.image, this, this).execute();

        mNameView.setText(student.getFullName());
        mBatchView.setText(student.batch.name);

        if (student.getSkills().length > 0) {
            String[] skills = student.getSkills();
            StringBuilder builder = new StringBuilder();
            for (String skill : skills) {
                if (builder.length() > 0) {
                    builder.append(", ");
                }
                builder.append(skill);
            }

            mSkillsView.setText(builder.toString());
        }
        else {
            mSkillsLabel.setVisibility(View.GONE);
            mSkillsView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(student.getJob())) {
            mJobView.setText(student.getJob());
        }
        else {
            mJobView.setVisibility(View.GONE);
            mJobLabel.setVisibility(View.GONE);
        }

        mGithubButton.setOnClickListener(this);
        mTwitterButton.setOnClickListener(this);
        mEmailButton.setOnClickListener(this);
        mSmsButton.setOnClickListener(this);

        if (TextUtils.isEmpty(student.github)) {
            mGithubButton.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(student.twitter)) {
            mTwitterButton.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(student.email)) {
            mEmailButton.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(student.getPhoneNumber())) {
            mSmsButton.setVisibility(View.GONE);
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonTwitter:
                Intent twitter = new Intent(Intent.ACTION_VIEW, Uri.parse(mStudent.getTwitterUrl()));
                if (twitter.resolveActivity(getPackageManager()) != null) {
                    startActivity(twitter);
                }
                break;
            case R.id.buttonGithub:
                Intent github = new Intent(Intent.ACTION_VIEW, Uri.parse(mStudent.getGithubUrl()));
                if (github.resolveActivity(getPackageManager()) != null) {
                    startActivity(github);
                }
                break;
            case R.id.buttonEmail:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mStudent.email, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hey there " + mStudent.firstName);
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                break;
            case R.id.buttonSms:
                Intent smsIntent;
                if (AppUtil.isKitKat()) {
                    String defaultPackageName = Telephony.Sms.getDefaultSmsPackage(this);
                    smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + mStudent.getPhoneNumber()));
                    if (defaultPackageName != null) {
                        smsIntent.setPackage(defaultPackageName);
                    }
                }
                else {
                    smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setData(Uri.parse("smsto:" + mStudent.getPhoneNumber()));
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("address", mStudent.getPhoneNumber());
                }

                if (smsIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(smsIntent);
                }
                break;
            default:
                // do nothing
        }
    }

    @Override
    public void onPreImageDownload() {
        mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
        int drawable = Constants.sLoadingIcons[Math.abs(new Random().nextInt() % Constants.sLoadingIcons.length)];
        mImageView.setImageDrawable(getResources().getDrawable(drawable));
        final Animation animation = new RotateAnimation(0.0f, -359.0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(500);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        mImageView.startAnimation(animation);
    }

    @Override
    public void onImageDownloaded(Bitmap bitmap) {
        mImageView.clearAnimation();
        mImageView.setImageBitmap(bitmap);
    }

    @Override
    public void onImageFailed(boolean hadNetwork) {
        mImageView.clearAnimation();
        mImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
        mImageView.setAlpha(100);
        if (ImageDownloads.isOnline(this)) {
            Toast.makeText(this, "Error loading image.", Toast.LENGTH_SHORT).show();
        }
    }
}
