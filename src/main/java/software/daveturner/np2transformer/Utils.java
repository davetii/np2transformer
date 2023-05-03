package software.daveturner.np2transformer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class Utils {

    public String removeRoot(String xml) {
        Document d =  xmlToDoc(xml);
        Element rootElement = d.getDocumentElement();
        boolean hasNodes = false;
        NodeList list = rootElement.getChildNodes();
        for(int i=0; i< list.getLength(); i++) {
            Node child = list.item(i);
            if(child.getNodeType() == Node.ELEMENT_NODE) { hasNodes = true; break; }
        }
        if (!hasNodes) { return ""; }
        String parentName = rootElement.getNodeName();
        String top = "<" + parentName + ">";
        String bottom = "</" + parentName + ">";
        String end = "<" + parentName + "/>";
        String s;
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(d), new StreamResult(sw));
            s = sw.toString();
            return s.replace(top, "").replace(bottom, "").replace(end, "").trim();
        } catch (Exception ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }

    public Document xmlToDoc(String xml) {
        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        InputSource is = new InputSource(new StringReader(xml));
        try {
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }


    }
}
