package knaps.hacker.school.utils;

import java.text.Normalizer;

/**
 * Created by lisaneigut on 22 Sep 2013.
 */
public class StringUtil {

    public static String removeAccents(String string) {
        return string == null ? null :
                Normalizer.normalize(string, Normalizer.Form.NFD)
                          .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}
