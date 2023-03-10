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

public class PaymentReceiptXMLMapper {

    private final String addressXML;
    private final String companyXML;

    Document inputXmlDoc;
    public PaymentReceiptXMLMapper(String inputXML) {
        DocumentBuilder builder;

        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        InputSource is = new InputSource(new StringReader(inputXML));
        try {
            inputXmlDoc = builder.parse(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

        inputXmlDoc.getDocumentElement().normalize();
        addressXML = buildAddressXML();
        companyXML = buildCompanyXML();
    }

    private String buildCompanyXML() {
        Map<String, String> map = nodeToMap("CompanyAddress");
        System.out.println(map.keySet());
        Document doc = initDoc();
        Element e = doc.createElement("Company");
        doc.appendChild(e);
        e.setAttributeNode(createAttribute(doc, map, "Line1", "Line1"));
        e.setAttributeNode(createAttribute(doc, map, "Line2", "Line2"));
        e.setAttributeNode(createAttribute(doc, map, "Line3", "Line3"));
        return docToString(doc);
    }

    private String buildAddressXML() {
        Map<String, String> map = nodeToMap("AddressDetails");
        Document doc = initDoc();
        Element element = doc.createElement("Address");
        doc.appendChild(element);
        element.setAttributeNode(createAttribute(doc, map, "City", "City"));
        element.setAttributeNode(createAttribute(doc, map, "Country", "Country"));
        element.setAttributeNode(createAttribute(doc, map, "State", "State"));
        element.setAttributeNode(createAttribute(doc, map, "Postcode", "Postcode"));
        element.setAttributeNode(createAttribute(doc, map, "Line1", "Line1"));
        element.setAttributeNode(createAttribute(doc, map, "Line2", "Line2"));
        element.setAttributeNode(createAttribute(doc, map, "Line3", "Line3"));
        return docToString(doc);
    }

    private Attr createAttribute(Document doc, Map<String, String> map, String sourceKey, String newAttribName) {
        Attr attr = doc.createAttribute(sourceKey);
        attr.setValue(map.get(newAttribName));
        return attr;
    }

    private String docToString (Document doc) {
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

    private Document initDoc() {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        return documentBuilder.newDocument();
    }

    public String getAddressXML() {
        return addressXML;
    }

    private Map<String, String> nodeToMap(String nodeName) {
        Map<String, String> map = new HashMap<>();
        NodeList list = inputXmlDoc.getElementsByTagName(nodeName).item(0).getChildNodes();
        for (int i=0; i< list.getLength(); i++) {
            Node n = list.item(i);
            if(n.getNodeType() == Node.ELEMENT_NODE) {
                map.put(n.getNodeName(), n.getTextContent());
            }
        }
        return map;
    }

    public String getCompanyXML() {
        return this.companyXML;
    }
}
