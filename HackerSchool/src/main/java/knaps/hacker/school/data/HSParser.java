package knaps.hacker.school.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
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
import java.util.HashSet;
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

    public static ArrayList<Student> parseBatches(final InputStream xml, ArrayList<String> existingBatches)
            throws IOException, SAXException, TransformerConfigurationException, XPathExpressionException {
        long startTime = System.currentTimeMillis();
        final XPathFactory factory = XPathFactory.newInstance();
        final XPath path = factory.newXPath();
        final MutableNamespaceContext nc = new MutableNamespaceContext();
        nc.setNamespace("html", "http://www.w3.org/1999/xhtml");
        path.setNamespaceContext(nc);
        final ArrayList<Student> studentList = new ArrayList<Student>();

        final Node cleanedDom = getHtmlUrlNode(xml);
        final NodeList batches = (NodeList) path.evaluate("//html:ul[@id='batches']/html:li", cleanedDom, XPathConstants.NODESET);
        Log.d("XML - - batch count..", batches.getLength()+"");

        for (int i = 0; i < batches.getLength(); i++) {
            final Element batch = (Element) batches.item(i);
            final String batchName = path.evaluate("html:h2/text()", batch).replace("\n", "");
            final String batchId = ((Element) path.evaluate("html:ul", batch, XPathConstants.NODE)).getAttribute("id").trim().toLowerCase();
            Log.d("XML - - parsing batch .. ", batchName + ":" + batchId);

            final NodeList students = (NodeList) path.evaluate("html:ul/html:li[@class='person']", batch, XPathConstants.NODESET);
            for (int j = 0; j < students.getLength(); j++) {
                final Element student = (Element) students.item(j);
                studentList.add(new Student(batchName, batchId, student, path));
            }
        }

        Log.d("XML _ timing", "Total time for parsing: " + (System.currentTimeMillis() - startTime) + "ms");
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


    public static void writeStudentsToDatabase(final List<knaps.hacker.school.models.Student> students, final Context context) {
        long startTime = System.currentTimeMillis();
        final HSDatabaseHelper mDbHelper = new HSDatabaseHelper(context);
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        final SQLiteStatement stmt = db.compileStatement(HSData.HSer.SQL_UPSERT_ALL);

        for (knaps.hacker.school.models.Student student : students) {
            stmt.bindLong(1, student.mId);
            stmt.bindString(2, student.mName);
            stmt.bindString(3, student.mImageUrl);
            stmt.bindString(4, student.mJob);
            stmt.bindString(5, student.mJobUrl);
            stmt.bindString(6, student.mSkills);
            stmt.bindString(7, student.mEmail);
            stmt.bindString(8, student.mGithubUrl);
            stmt.bindString(9, student.mTwitterUrl);
            stmt.bindString(10, student.mBatchId);
            stmt.bindString(11, student.mBatch);
            stmt.execute();
            stmt.clearBindings();
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        Log.d("XML _ timing", "Total time for writing to db: " + (System.currentTimeMillis() - startTime) + "ms");
    }
}
