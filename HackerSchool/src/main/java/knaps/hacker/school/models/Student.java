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

    public long id = 0;
    public String firstName = "";
    public String lastName = "";
    public String image = "";
    public String email = "";
    public String github = "";
    public String twitter = "";
    public String batchId = "";

    // Things not returned by the API
    public String mJob = "";
    public String mJobUrl = "";
    public String mSkills = "";
    public String mImageFilename = "";
    public String mBatch = "";

    public Student(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_ID));
        firstName = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_FULL_NAME));
        image = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_IMAGE_URL));
        mImageFilename = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_IMAGE_FILENAME));
        mJob = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_JOB));
        mJobUrl = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_JOB_URL));
        mSkills = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_SKILLS));
        email = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_EMAIL));
        github = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_GITHUB));
        twitter = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_TWITTER));
        mBatch = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_BATCH));
        batchId = cursor.getString(cursor.getColumnIndex(HSData.HSer.COLUMN_NAME_BATCH_ID));
    }

    public Student(String batch, String batchId, Node student, XPath path) throws
            XPathExpressionException {

        mBatch = batch;
        this.batchId = batchId;

        final Element imageNode = (Element) path
                .evaluate("html:a/html:img[@class='profile-image']", student, XPathConstants.NODE);
        image = imageNode.getAttribute("src");
        Log.i("ImageUrl", image);
        final Element nameElem = (Element) path.evaluate("html:div[@class='name']/html:a", student, XPathConstants.NODE);
        firstName = nameElem.getTextContent();
        id = getPersonId(nameElem.getAttribute("href"));
        final Element jobElem = (Element) path
                .evaluate("html:div[@class='job']/html:a", student, XPathConstants.NODE);
        if (jobElem != null) {
            mJob = jobElem.getTextContent();
            mJobUrl = jobElem.getAttribute("href");
        }
        mSkills = path.evaluate("html:span[@class='skills']/text()", student);

        final NodeList contacts = (NodeList) path
                .evaluate("html:div[@class='icon-links']/html:a", student, XPathConstants.NODESET);
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
            github = url;
        }
        else if (url.contains("twitter")) {
            twitter = url;
        }
        else if (url.contains("mailto:")) {
            email = url.replace("mailto:", "");
            Log.d("XML -- parsing", "emails -- " + email);
        }
    }

    private int getPersonId(String personLink) {
        int id = -1;
        Matcher r = Pattern.compile("\\d+").matcher(personLink);
        r.find();
        try {
            id = Integer.parseInt(r.group(), 10);
        }
        catch (NumberFormatException ex) {
            // exception. leave it as -1
        }

        return id;
    }
}
