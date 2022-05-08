package org.openstreetmap.josm.plugins.ods.http;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

public interface OdsHttpClient {

    public Reader doXmlPostRequest(String url, String postRequest) throws IOException;

    public Reader doGetRequest(String url, Map<String, String> queryParameters) throws IOException;

    /**
     * Create an XML 5tring from a Dom Document.
     * 
     * @param node
     * @param indent
     * @return
     * @throws TransformerException
     */
//    public static String getXmlString(Document document, boolean indent) throws TransformerException {
//        DOMSource source = new DOMSource(document);
//
//        TransformerFactory transformerFactory = TransformerFactory.newInstance();
//        Transformer transf = transformerFactory.newTransformer();
//
//        transf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//        transf.setOutputProperty(OutputKeys.METHOD, "xml");
//        if (indent) {
//            transf.setOutputProperty(OutputKeys.INDENT, "yes");
//            transf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
//        }
//            
//        StringWriter sw = new StringWriter();
//        transf.transform(source, new StreamResult(sw));
//        return sw.toString();
//    }

    /**
     * Create an XML 5tring from a Dom Element.
     * 
     * @param node
     * @param indent
     * @return
     * @throws TransformerException
     */
    public static String getXmlString(Node element, boolean indent, boolean omitXmlDeclaration) throws TransformerException {
        DOMSource source = new DOMSource(element);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transf = transformerFactory.newTransformer();

        transf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transf.setOutputProperty(OutputKeys.METHOD, "xml");
        if (omitXmlDeclaration) {
            transf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }
        if (indent) {
            transf.setOutputProperty(OutputKeys.INDENT, "yes");
            transf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        }
            
        StringWriter sw = new StringWriter();
        transf.transform(source, new StreamResult(sw));
        return sw.toString();
    }


}
