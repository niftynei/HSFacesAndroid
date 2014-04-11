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
    public Batch batch;

    // Things not returned by the API
    public String mJob = "";
    public String mJobUrl = "";
    public String mSkills = "";

    // local to app
    public String mImageFilename = "";

    public Student(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_ID));
        firstName = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_FIRST_NAME));
        lastName = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_LAST_NAME));
        image = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_IMAGE_URL));
        mImageFilename = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_IMAGE_FILENAME));
        email = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_EMAIL));
        github = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_GITHUB));
        twitter = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_TWITTER));
        batch = new Batch(cursor);

        // TODO: ask for these in the API
        mJob = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_JOB));
        mJobUrl = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_JOB_URL));
        mSkills = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_SKILLS));
    }

    public String getTwitterUrl() {
        return Constants.TWITTER_URL + "/" + twitter;
    }

    public String getGithubUrl() {
        return Constants.GITHUB_URL + "/" + github;
    }

}
