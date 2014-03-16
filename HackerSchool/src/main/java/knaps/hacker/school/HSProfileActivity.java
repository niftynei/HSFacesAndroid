package knaps.hacker.school;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import java.util.Random;

import knaps.hacker.school.models.Student;
import knaps.hacker.school.networking.ImageDownloads;
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
        mEmailButton = (ImageButton) findViewById(R.id.buttonEmail);

        mStudent = (Student) getIntent().getSerializableExtra(Constants.STUDENT);
        populateViews(mStudent);
    }

    private void populateViews(Student student) {
        new ImageDownloads.HSGetImageTask(student.mImageUrl, this, this).execute();

        mNameView.setText(student.mName);
        mBatchView.setText(student.mBatch);

        if (!TextUtils.isEmpty(student.mSkills)) {
            mSkillsView.setText(student.mSkills);
        }
        else {
            mSkillsLabel.setVisibility(View.GONE);
            mSkillsView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(student.mJob)) {
            mJobView.setText(student.mJob);
            if (!TextUtils.isEmpty(student.mJobUrl)) {
                mJobView.setOnClickListener(this);
            }
        }
        else {
            mJobView.setVisibility(View.GONE);
            mJobLabel.setVisibility(View.GONE);
        }

        mGithubButton.setOnClickListener(this);
        mTwitterButton.setOnClickListener(this);
        mEmailButton.setOnClickListener(this);
        if (TextUtils.isEmpty(student.mGithubUrl)) {
            mGithubButton.setVisibility(View.INVISIBLE);
        }
        if (TextUtils.isEmpty(student.mTwitterUrl)) {
            mTwitterButton.setVisibility(View.INVISIBLE);
        }
        if (TextUtils.isEmpty(student.mEmail)) {
            mEmailButton.setVisibility(View.INVISIBLE);
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

    //    @Override
    //    public boolean onCreateOptionsMenu(Menu menu) {
    //        // Inflate the menu; this adds items to the action bar if it is present.
    //        getMenuInflater().inflate(R.menu.hsprofile, menu);
    //        return true;
    //    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonTwitter:
                Intent twitter = new Intent(Intent.ACTION_VIEW, Uri.parse(mStudent.mTwitterUrl));
                startActivity(twitter);
                break;
            case R.id.buttonGithub:
                Intent github = new Intent(Intent.ACTION_VIEW, Uri.parse(mStudent.mGithubUrl));
                startActivity(github);
                break;
            case R.id.buttonEmail:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", mStudent.mEmail, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hey " + mStudent.mName);
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
                break;
            case R.id.textJob:
                Intent job = new Intent(Intent.ACTION_VIEW, Uri.parse(mStudent.mJobUrl));
                startActivity(job);
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
