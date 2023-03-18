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

    private final String ordersXml;

    public PaymentReceiptXMLMapper(String inputXML) {
        DocumentBuilder builder;
        Document sourceDoc;
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
        this.addressXML = buildAddressXML(sourceDoc);
        this.companyXML = buildCompanyXML(sourceDoc);
        this.paymentDetailXML = buildPaymentDetailXML(sourceDoc);
        this.ordersXml = buildOrdersXML(sourceDoc);
    }

    private String buildOrdersXML(Document sourceDoc) {
        Document newDoc = newDoc();
        Element items = newDoc.createElement("OrderLineItems");
        newDoc.appendChild(items);
        NodeList list = sourceDoc.getElementsByTagName("OrderLineItems").item(0).getChildNodes();
        for (int i=0; i< list.getLength(); i++) {
            Node n = list.item(i);
            if(n.getNodeType() == Node.ELEMENT_NODE) {
                Element order = sourceOrderToTargetOrder(newDoc, n);
                items.appendChild(order);
            }
        }
        return docToString(newDoc);

    }

    private Element sourceOrderToTargetOrder(Document doc, Node n) {
        Element order = doc.createElement("Order");
        NodeList list = n.getChildNodes();
        for (int i=0; i< list.getLength(); i++) {
            Node child = list.item(i);
            if(child.getNodeType() == Node.ELEMENT_NODE) {
                maybeAddAttribute(order, child, "FromDate");
                maybeAddAttribute(order, child, "ToDate");
                maybeAddAttribute(order, child, "TransactionDate");
                maybeAddAttribute(order, child, "VIN");
                maybeAddAttribute(order, child, "OrderNumber");
                if (child.getNodeName().equals("PaymentItems")) {
                    NodeList items = child.getChildNodes();
                    for (int x=0; x< items.getLength(); x++) {
                        Node payItem = items.item(x);
                        if(payItem != null && payItem.getNodeType() == Node.ELEMENT_NODE && payItem.getNodeName().equals("PaymentItem")) {
                            order.appendChild(createPaymentItem(doc, payItem));
                        }
                    }

                }
            }
        }
        return order;
    }

    private Node createPaymentItem(Document doc, Node n) {
        Element element = doc.createElement("Payment");
        NodeList children = n.getChildNodes();
        for (int i=0; i< children.getLength(); i++) {
            Node child = children.item(i);

            if (isElementNamed(child, "Description")) {
                element.setAttribute("Description", child.getTextContent());
            }

            if (isElementNamed(child, "Amount")) {
                NodeList subChildren = child.getChildNodes();
                for (int amtCounter=0; amtCounter< children.getLength(); amtCounter++) {
                    Node subChild = subChildren.item(amtCounter);

                    if(isElementNamed(subChild, "RawAmount")) {
                        element.setTextContent(subChild.getTextContent());
                    }
                }

            }
        }
        return element;
    }

    private boolean isElementNamed(Node n, String s) {
        return (n != null && n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(s));
    }

    private void maybeAddAttribute(Element order, Node child, String s) {
        if (child.getNodeName().equals(s)) {
            maybeAddAttribute(order, s, child.getTextContent());
        }
    }

    private void maybeAddAttribute(Element element, String key, String value) {
        element.setAttribute( key, value );
    }

    private String buildPaymentDetailXML(Document sourceDoc) {
        Document doc = newDoc();
        Element e = doc.createElement("PaymentDetail");
        doc.appendChild(e);
        e.setAttributeNode(createAttribute(sourceDoc, doc, "BillingReceiptNr"));
        e.setAttributeNode(createAttribute(sourceDoc, doc, "CurrencySymbol"));
        e.setAttributeNode(createAttribute(sourceDoc, doc, "PaymentDisplayStatus"));
        e.setAttributeNode(createAttribute(sourceDoc, doc, "ReceivedDate"));
        e.setAttributeNode(createAttribute(sourceDoc, doc, "ReferenceTransactionNumber"));
        e.setAttributeNode(createAttribute(sourceDoc, doc, "ReferenceTransactionType"));
        e.setAttributeNode(createAttribute(sourceDoc, doc, "TransactionDate"));
        e.setAttributeNode(createAttribute(sourceDoc, doc, "TransactionType"));
        maybeSetCurrencyAmount(sourceDoc, doc, e);
        return docToString(doc);
    }

    private void maybeSetCurrencyAmount(Document sourceDoc, Document targetDoc, Element e) {
        NodeList list = sourceDoc.getElementsByTagName("CurrencyAmount").item(0).getChildNodes();
        for (int i=0; i< list.getLength(); i++) {
            Node n = list.item(i);
            if(n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().toUpperCase(Locale.ENGLISH).equals("RAWAMOUNT")) {
                e.setAttributeNode(createAttribute(targetDoc, "CurrencyAmount", n.getTextContent()));
                return;
            }
        }
    }

    private String buildCompanyXML(Document sourceDoc) {
        Map<String, String> map = nodeToMap(sourceDoc, "CompanyAddress");
        Document doc = newDoc();
        Element e = doc.createElement("Company");
        doc.appendChild(e);
        e.setAttributeNode(createAttribute(doc, "Line1", map));
        e.setAttributeNode(createAttribute(doc, "Line2", map));
        e.setAttributeNode(createAttribute(doc, "Line3", map));
        return docToString(doc);
    }

    private String buildAddressXML(Document sourceDoc) {
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

    private Document newDoc() {
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

    private Map<String, String> nodeToMap(Document sourceDoc, String nodeName) {
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

    public String getCompanyXML() {
        return this.companyXML;
    }

    public String getPaymenntDetailXML() {
        return this.paymentDetailXML;
    }

    public String getOrdersXML() {
        return this.ordersXml;
    }
}

