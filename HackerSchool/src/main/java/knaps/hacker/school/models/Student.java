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

/**
 * Created by lisaneigut on 14 Sep 2013.
 */
public class Student implements Serializable {

    private static final long serialVersionUID = 0L;

    public int mId = 0;
    public String mName = "";
    public String mImageUrl = "";
    public String mJob = "";
    public String mJobUrl = "";
    public String mSkills = "";
    public String mEmail = "";
    public String mGithubUrl = "";
    public String mTwitterUrl = "";
    public String mBatch = "";
    public String mBatchId = "";

    public Student(Cursor cursor) {
        mId = cursor.getInt(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_ID));
        mName = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_FULL_NAME));
        mImageUrl = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_IMAGE_URL));
        mJob = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_JOB));
        mJobUrl = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_JOB_URL));
        mSkills = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_SKILLS));
        mEmail = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_EMAIL));
        mGithubUrl = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_GITHUB));
        mTwitterUrl = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_TWITTER));
        mBatch = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_BATCH));
        mBatchId = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_BATCH_ID));
    }

    public Student(String batch, String batchId, Node student, XPath path) throws XPathExpressionException {

        // TODO: why is this so slow??
        mBatch = batch;
        mBatchId = batchId;

        final Element imageNode = (Element) path.evaluate("html:a/html:img[@class='profile-image']", student, XPathConstants.NODE);
        mImageUrl = imageNode.getAttribute("src");
        final Element nameElem = (Element) path.evaluate("html:div[@class='name']/html:a", student, XPathConstants.NODE);
        mName = nameElem.getTextContent();
        mId = getPersonId(nameElem.getAttribute("href"));
        final Element jobElem = (Element) path.evaluate("html:div[@class='job']/html:a", student, XPathConstants.NODE);
        if (jobElem != null) {
            mJob = jobElem.getTextContent();
            mJobUrl = jobElem.getAttribute("href");
        }
        mSkills = path.evaluate("html:span[@class='skills']/text()", student);

        final NodeList contacts = (NodeList) path.evaluate("html:div[@class='icon-links']/html:a", student, XPathConstants.NODESET);
        if (contacts.getLength() > 0) {
            for (int i = 0; i < contacts.getLength(); i++) {
                Element node = (Element) contacts.item(i);
                String url = node.getAttribute("href").trim();
                figureOutContactUrl(url);
            }
        }
    }

    private void figureOutContactUrl(String url) {
        if (url.contains("github")) {
            mGithubUrl = url;
        }
        else if (url.contains("twitter")) {
            mTwitterUrl = url;
        }
        else if (url.contains("mailto:")) {
            mEmail = url.replace("mailto:", "");
            Log.d("XML -- parsing", "emails !" + mEmail);
        }
    }

    private int getPersonId(String personLink) {
        int id = -1;
        Matcher r = Pattern.compile("\\d+").matcher(personLink);
        r.find();
        try {
            id = Integer.parseInt(r.group(), 10);
        } catch (NumberFormatException ex) {
            // exception. leave it as -1
        }

        return id;
    }
}
