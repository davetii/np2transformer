package software.daveturner.np2transformer;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class NotificationXMLMapper {

    protected Document sourceDoc;

    private final String addressXML;
    private final String companyXML;
    public NotificationXMLMapper(String inputXML) {
        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        InputSource is = new InputSource(new StringReader(inputXML));
        try {
            sourceDoc = builder.parse(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

        sourceDoc.getDocumentElement().normalize();
        this.addressXML = buildAddressXML();
        this.companyXML = buildCompanyXML();
    }

    private String buildCompanyXML() {
        Map<String, String> map = nodeToMap(sourceDoc, "CompanyAddress");
        Document doc = newDoc();
        Element e = doc.createElement("Company");
        doc.appendChild(e);
        e.setAttributeNode(createAttribute(doc, "Line1", map));
        e.setAttributeNode(createAttribute(doc, "Line2", map));
        e.setAttributeNode(createAttribute(doc, "Line3", map));
        return docToString(doc);
    }

    private String buildAddressXML() {
        Map<String, String> map = nodeToMap(sourceDoc, "AddressDetails");
        Document doc = newDoc();
        Element element = doc.createElement("Address");
        doc.appendChild(element);
        element.setAttributeNode(createAttribute(doc, "City", map));
        element.setAttributeNode(createAttribute(doc, "Country", map));
        element.setAttributeNode(createAttribute(doc, "State", map));
        element.setAttributeNode(createAttribute(doc, "Postcode", map));
        element.setAttributeNode(createAttribute(doc, "Line1", map));
        element.setAttributeNode(createAttribute(doc, "Line2", map));
        element.setAttributeNode(createAttribute(doc, "Line3", map));
        return docToString(doc);
    }

    protected void maybeAddAttribute(Element order, Node child, String s) {
        if (isElementNamed(child, s)) {
            addAttribute(order, s, child.getTextContent());
        }
    }

    protected void addAttribute(Element element, String key, String value) {
        element.setAttribute( key, value );
    }

    protected Attr createAttribute(Document target, String key) {
        Node n = sourceDoc.getElementsByTagName(key).item(0);
        return createAttribute(target, key, n.getTextContent());
    }

    protected Attr createAttribute(Document doc, String key, Map<String, String> map) {
        return createAttribute(doc, key, map.get(key));
    }

    protected Attr createAttribute(Document doc, String key, String value) {
        Attr attr = doc.createAttribute(key);
        attr.setValue(value);
        return attr;
    }

    protected Map<String, String> nodeToMap(Document sourceDoc, String nodeName) {
        Map<String, String> map = new HashMap<>();
        NodeList list = sourceDoc.getElementsByTagName(nodeName).item(0).getChildNodes();
        for (int i=0; i< list.getLength(); i++) {
            Node n = list.item(i);
            if(n.getNodeType() == Node.ELEMENT_NODE) {
                map.put(n.getNodeName(), n.getTextContent());
            }
        }
        return map;
    }

    protected Document newDoc() {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        return documentBuilder.newDocument();
    }

    protected String docToString (Document doc) {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter sw = new StringWriter();
        try {
            transformer.transform(new DOMSource(doc), new StreamResult(sw));

        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        return sw.toString();
    }

    protected boolean isElementNamed(Node n, String s) {
        return (n != null && n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(s));
    }

    public String getAddressXML() {
        return addressXML;
    }

    public String getCompanyXML() {
        return this.companyXML;
    }


}
