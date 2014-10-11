package knaps.hacker.school.models;

import android.database.Cursor;

import java.io.Serializable;

import knaps.hacker.school.data.HSData;
import knaps.hacker.school.utils.Constants;

import static knaps.hacker.school.data.HSData.HSer.*;

/**
 * Created by lisaneigut on 14 Sep 2013.
 */
public class Student implements Serializable {

    private static final long serialVersionUID = 0L;

    public long id = 0L;
    public String firstName = "";
    public String lastName = "";
    public String image = "";
    public String email = "";
    public String github = "";
    public String twitter = "";
    public String phoneNumber = "";
    public boolean hasPhoto = false;
    public boolean isFaculty = false;
    public boolean isHackerSchooler = true;
    public String job = "";
    public String[] skills = new String[0];

    public long batchId = 0; // for gson binding to save to db
    public Batch batch;

    // local to app
    public String mImageFilename = "";

    public Student(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_ID));
        firstName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_FIRST_NAME));
        lastName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_LAST_NAME));
        image = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_IMAGE_URL));
        mImageFilename = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_IMAGE_FILENAME));
        email = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_EMAIL));
        github = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_GITHUB));
        twitter = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_TWITTER));
        phoneNumber = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_PHONE_NUMBER));
        job = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_JOB));
        skills = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_SKILLS)).split(",");

        isHackerSchooler = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_IS_HACKER_SCHOOLER)) == 1;
        isFaculty = cursor.getInt(cursor.getColumnIndex(COLUMN_NAME_IS_FACULTY)) == 1;

        batch = new Batch(cursor);
        batchId = batch.id;
    }

    public String getFirstName() { return firstName == null ? "" : firstName; }
    public String getLastName() { return lastName == null ? "" : lastName; }
    public String getImage() { return image == null ? "" : image; }
    public String getEmail() { return email == null ? "" : email; }
    public String getGithub() { return github == null ? "" : github; }
    public String getTwitter() { return twitter == null ? "" : twitter; }
    public String getJob() { return job == null ? "" : job; }
    public String[] getSkills() { return skills == null ? new String[0] : skills; }
    public String getPhoneNumber() { return phoneNumber == null ? "" : phoneNumber; }
    public boolean hasPhoto() { return hasPhoto; }
    public boolean isHackerSchooler() { return isHackerSchooler; }
    public boolean isFaculty() {return isFaculty;}


    public String getTwitterUrl() {
        return Constants.TWITTER_URL + "/" + twitter;
    }

    public String getGithubUrl() {
        return Constants.GITHUB_URL + "/" + github;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
