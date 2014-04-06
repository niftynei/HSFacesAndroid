package knaps.hacker.school.utils;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by lisaneigut on 22 Sep 2013.
 */
public class StringUtil {

    private static SimpleDateFormat sDateFormat;

    public static String removeAccents(String string) {
        return string == null ? null :
                Normalizer.normalize(string, Normalizer.Form.NFD)
                          .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static SimpleDateFormat getSimpleDateFormatter() {
        if (sDateFormat == null) {
            sDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        }

        return sDateFormat;
    }
}
