package api.atompub;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.commons.xml.DOMUtil;
import com.ibm.commons.xml.XMLException;

enum AtomPubUtil {
    ;

    public Document createResponse(final String tagName) throws XMLException {
        Document result = DOMUtil.createDocument();
        Element root = DOMUtil.createElement(result, tagName);
        root.setAttribute("xmlns", "http://purl.org/atom/app#"); //$NON-NLS-1$ //$NON-NLS-2$
        root.setAttribute("xmlns:atom", "http://www.w3.org/2005/Atom"); //$NON-NLS-1$ //$NON-NLS-2$
        root.setAttribute("xmlns:app", "http://www.w3.org/2007/app"); //$NON-NLS-1$ //$NON-NLS-2$

        return result;
    }
}
