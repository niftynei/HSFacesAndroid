package knaps.hacker.school.models;

import android.database.Cursor;
import android.util.Log;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import knaps.hacker.school.data.HSData;
import knaps.hacker.school.utils.Constants;

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
    public long batchId = 0; // for gson binding to save to db
    public Batch batch;

    // Things not returned by the API
    public String mJob = "";
    public String mJobUrl = "";
    public String mSkills = "";

    // local to app
    public String mImageFilename = "";

    public Student(Cursor cursor) {
        id = cursor.getLong(HSData.HSer.COL_ID);
        firstName = cursor.getString(HSData.HSer.COL_FIRST_NAME);
        lastName = cursor.getString(HSData.HSer.COL_LAST_NAME);
        image = cursor.getString(HSData.HSer.COL_IMAGE_URL);
        mImageFilename = cursor.getString(HSData.HSer.COL_IMAGE_FILENAME);
        email = cursor.getString(HSData.HSer.COL_EMAIL);
        github = cursor.getString(HSData.HSer.COL_GITHUB);
        twitter = cursor.getString(HSData.HSer.COL_TWITTER);
        batch = new Batch(cursor);

        // TODO: ask for these in the API
        mJob = cursor.getString(HSData.HSer.COL_JOB);
        mJobUrl = cursor.getString(HSData.HSer.COL_JOB_URL);
        mSkills = cursor.getString(HSData.HSer.COL_SKILLS);
    }

    public String getFirstName() { return firstName == null ? "" : firstName; }
    public String getLastName() { return lastName == null ? "" : lastName; }
    public String getImage() { return image == null ? "" : image; }
    public String getEmail() { return email == null ? "" : email; }
    public String getGithub() { return github == null ? "" : github; }
    public String getTwitter() { return twitter == null ? "" : twitter; }
    public String getJob() { return mJob == null ? "" : mJob; }
    public String getJobUrl() { return mJobUrl == null ? "" : mJobUrl; }
    public String getSkills() { return mSkills == null ? "" : mSkills; }


    public String getTwitterUrl() {
        return Constants.TWITTER_URL + "/" + twitter;
    }

    public String getGithubUrl() {
        return Constants.GITHUB_URL + "/" + github;
    }

}
