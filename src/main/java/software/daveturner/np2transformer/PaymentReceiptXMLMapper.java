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
import java.util.Locale;
import java.util.Map;

public class PaymentReceiptXMLMapper {

    private final String addressXML;
    private final String companyXML;
    private final String paymentDetailXML;

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
        this.addressXML = buildAddressXML();
        this.companyXML = buildCompanyXML();
        this.paymentDetailXML = buildPaymentDetailXML();
    }

    private String buildPaymentDetailXML() {
        Document doc = initDoc();
        Element e = doc.createElement("PaymentDetail");
        doc.appendChild(e);
        e.setAttributeNode(createAttribute(inputXmlDoc, doc, "BillingReceiptNr"));
        e.setAttributeNode(createAttribute(inputXmlDoc, doc, "CurrencySymbol"));
        e.setAttributeNode(createAttribute(inputXmlDoc, doc, "PaymentDisplayStatus"));
        e.setAttributeNode(createAttribute(inputXmlDoc, doc, "ReceivedDate"));
        e.setAttributeNode(createAttribute(inputXmlDoc, doc, "ReferenceTransactionNumber"));
        e.setAttributeNode(createAttribute(inputXmlDoc, doc, "ReferenceTransactionType"));
        e.setAttributeNode(createAttribute(inputXmlDoc, doc, "TransactionDate"));
        e.setAttributeNode(createAttribute(inputXmlDoc, doc, "TransactionType"));

        NodeList list = inputXmlDoc.getElementsByTagName("CurrencyAmount").item(0).getChildNodes();
        for (int i=0; i< list.getLength(); i++) {
            Node n = list.item(i);
            if(n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().toUpperCase(Locale.ENGLISH).equals("RAWAMOUNT")) {
                e.setAttributeNode(createAttribute(doc, "CurrencyAmount", n.getTextContent()));
            }
        }

        return docToString(doc);
    }

    private String buildCompanyXML() {
        Map<String, String> map = nodeToMap("CompanyAddress");
        System.out.println(map.keySet());
        Document doc = initDoc();
        Element e = doc.createElement("Company");
        doc.appendChild(e);
        e.setAttributeNode(createAttribute(doc, "Line1", map));
        e.setAttributeNode(createAttribute(doc, "Line2", map));
        e.setAttributeNode(createAttribute(doc, "Line3", map));
        return docToString(doc);
    }

    private String buildAddressXML() {
        Map<String, String> map = nodeToMap("AddressDetails");
        Document doc = initDoc();
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

    private Attr createAttribute(Document source, Document target, String key) {
        Node n = source.getElementsByTagName(key).item(0);
        return createAttribute(target, key, n.getTextContent());
    }

    private Attr createAttribute(Document doc, String key, Map<String, String> map) {
        return createAttribute(doc, key, map.get(key));
    }

    private Attr createAttribute(Document doc, String key, String value) {
        Attr attr = doc.createAttribute(key);
        attr.setValue(value);
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

    public String getPaymenntDetailXML() {
        return this.paymentDetailXML;
    }
}

