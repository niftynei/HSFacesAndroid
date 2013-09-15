package knaps.hacker.school.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.ccil.cowan.tagsoup.Parser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import knaps.hacker.school.models.Student;

/**
 * Created by lisaneigut on 14 Sep 2013.
 */
public class HSParser {

    public static ArrayList<Student> parseBatches(final InputStream xml) {
        final XPathFactory factory = XPathFactory.newInstance();
        final XPath path = factory.newXPath();
        final MutableNamespaceContext nc = new MutableNamespaceContext();
        nc.setNamespace("html", "http://www.w3.org/1999/xhtml");
        path.setNamespaceContext(nc);
        final ArrayList<Student> studentList = new ArrayList<Student>();

        try {
            final Node cleanedDom = getHtmlUrlNode(xml);
//            Log.d("XML - - cleaned dom..", dumpNode(cleanedDom, true));
            final NodeList batches = (NodeList) path.evaluate("//html:ul[@id='batches']/html:li", cleanedDom, XPathConstants.NODESET);
            Log.d("XML - - batch count..", batches.getLength()+"");

            for (int i = 0; i < batches.getLength(); i++) {
                final Element batch = (Element) batches.item(i);
                final String batchName = path.evaluate("html:h2/text()", batch).replace("\n", "");
                final String batchId = ((Element) path.evaluate("html:ul", batch, XPathConstants.NODE)).getAttribute("id");
                Log.d("XML - - parsing batch ..", batchName + ":" + batchId);
                // TODO: check if batch already exists in database

                final NodeList students = (NodeList) path.evaluate("html:ul/html:li[@class='person']", batch, XPathConstants.NODESET);

                for (int j = 0; j < students.getLength(); j++) {
                    final Element student = (Element) students.item(j);
                    studentList.add(new Student(batchName, batchId, student, path));
                }
            }
        } catch (XPathExpressionException e) {
            Log.e("XML - - error parsing xpath", "error parsing xpath", e);
        } catch (SAXException e) {
            Log.e("XML - - error parsing xpath", "error parsing xpath", e);
        } catch (TransformerConfigurationException e) {
            Log.e("XML - - error parsing xpath", "error parsing xpath", e);
        } catch (IOException e) {
            Log.e("XML - - error parsing xpath", "error parsing xpath", e);
        } catch (TransformerException e) {
            Log.e("XML - - error parsing xpath", "error parsing xpath", e);
        }

        return studentList;
    }

    private static Node getHtmlUrlNode(InputStream xml) throws TransformerConfigurationException, IOException, SAXException {

        SAXTransformerFactory stf = (SAXTransformerFactory) TransformerFactory.newInstance();
        TransformerHandler th = stf.newTransformerHandler();

        DOMResult result = new DOMResult();

        th.setResult(result);
        // Use a TagSoup parser to tidy this up?!
        Parser parser = new Parser();
        parser.setContentHandler(th);

        parser.parse(new InputSource(xml));

        return result.getNode();
    }

    public static String dumpNode(Node node, boolean omitDeclaration) throws TransformerException {
        Transformer xformer =
                TransformerFactory.newInstance().newTransformer();
        if (omitDeclaration) {
            xformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }
        StringWriter sw = new StringWriter();
        Result result = new StreamResult(sw);
        Source source = new DOMSource(node);
        xformer.transform(source, result);
        return sw.toString();
    }


    public static void writeStudentsToDatabase(List<Student> students, Context context) {
        HSDatabaseHelper mDbHelper = new HSDatabaseHelper(context);
        SQLiteDatabase db =   mDbHelper.getWritableDatabase();

        for (Student student : students) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(HSDataContract.StudentEntry.COLUMN_NAME_ID, student.mId);
            contentValues.put(HSDataContract.StudentEntry.COLUMN_NAME_FULL_NAME, student.mName);
            contentValues.put(HSDataContract.StudentEntry.COLUMN_NAME_IMAGE_URL, student.mImageUrl);
            contentValues.put(HSDataContract.StudentEntry.COLUMN_NAME_JOB, student.mJob);
            contentValues.put(HSDataContract.StudentEntry.COLUMN_NAME_JOB_URL, student.mJobUrl);
            contentValues.put(HSDataContract.StudentEntry.COLUMN_NAME_SKILLS, student.mSkills);
            contentValues.put(HSDataContract.StudentEntry.COLUMN_NAME_EMAIL, student.mEmail);
            contentValues.put(HSDataContract.StudentEntry.COLUMN_NAME_GITHUB, student.mGithubUrl);
            contentValues.put(HSDataContract.StudentEntry.COLUMN_NAME_TWITTER, student.mTwitterUrl);
            contentValues.put(HSDataContract.StudentEntry.COLUMN_NAME_BATCH, student.mBatch);
            contentValues.put(HSDataContract.StudentEntry.COLUMN_NAME_BATCH_ID, student.mBatch);

            db.insert(
                HSDataContract.StudentEntry.TABLE_NAME,
                null,
                contentValues);
        }
    }
}
