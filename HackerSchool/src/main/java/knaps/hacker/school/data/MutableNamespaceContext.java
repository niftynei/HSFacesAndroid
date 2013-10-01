package knaps.hacker.school.data;

import java.util.*;

import javax.xml.namespace.NamespaceContext;

/**
 * There is a bug in the JDK which omits the setNamespace declaration
 * from implementations of NamespaceContext. We have to create our
 * own implementation to work around it. Documented here:
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5101859
 *
 * @author Oliver Roup <oroup@oroup.com>
 */
class MutableNamespaceContext implements NamespaceContext {

    private Map<String, String> map;

    public MutableNamespaceContext() {
        map = new HashMap<String, String>();
    }

    public void setNamespace(String prefix, String namespaceURI) {
        map.put(prefix, namespaceURI);
    }

    public String getNamespaceURI(String prefix) {
        return map.get(prefix);
    }

    public String getPrefix(String namespaceURI) {
        for (String prefix : map.keySet()) {
            if (map.get(prefix).equals(namespaceURI)) {
                return prefix;
            }
        }
        return null;
    }

    public Iterator getPrefixes(String namespaceURI) {
        List prefixes = new ArrayList();
        for (String prefix : map.keySet()) {
            if (map.get(prefix).equals(namespaceURI)) {
                prefixes.add(prefix);
            }
        }
        return prefixes.iterator();
    }
}
