package software.daveturner.np2transformer;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PaymentReceiptXMLMapper extends NotificationXMLMapper {

    private final String paymentDetailXML;
    private final String ordersXml;

    private final String contactXML;



    public PaymentReceiptXMLMapper(String inputXML) {
        super(inputXML);
        this.paymentDetailXML = buildPaymentDetailXML();
        this.ordersXml = buildOrdersXML();
        this.contactXML = buildContactXML();
    }

    private String buildContactXML() {
        NodeList list = sourceDoc.getElementsByTagName("ContactDetails").item(0).getChildNodes();
        Document newDoc = newDoc();
        Element contact = newDoc.createElement("Contact");
        newDoc.appendChild(contact);
        for (int i=0; i< list.getLength(); i++) {
            Node n = list.item(i);
            maybeAddAttribute(contact, n, "OfficialName");
        }
        return docToString(newDoc);
    }

    private String buildOrdersXML() {
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



    private String buildPaymentDetailXML() {
        Document doc = newDoc();
        Element e = doc.createElement("PaymentDetail");
        doc.appendChild(e);
        e.setAttributeNode(createAttribute(doc, "BillingReceiptNr"));
        e.setAttributeNode(createAttribute(doc, "CurrencySymbol"));
        e.setAttributeNode(createAttribute(doc, "PaymentDisplayStatus"));
        e.setAttributeNode(createAttribute(doc, "ReceivedDate"));
        e.setAttributeNode(createAttribute(doc, "ReferenceTransactionNumber"));
        e.setAttributeNode(createAttribute(doc, "ReferenceTransactionType"));
        e.setAttributeNode(createAttribute(doc, "TransactionDate"));
        e.setAttributeNode(createAttribute(doc, "TransactionType"));
        maybeSetCurrencyAmount(doc, e);
        return docToString(doc);
    }

    private void maybeSetCurrencyAmount(Document targetDoc, Element e) {
        NodeList list = sourceDoc.getElementsByTagName("CurrencyAmount").item(0).getChildNodes();
        for (int i=0; i< list.getLength(); i++) {
            Node n = list.item(i);
            if(isElementNamed(n, "RawAmount")) {
                e.setAttributeNode(createAttribute(targetDoc, "CurrencyAmount", n.getTextContent()));
                return;
            }
        }
    }

    public String getPaymentDetailXML() {
        return this.paymentDetailXML;
    }

    public String getOrdersXML() {
        return this.ordersXml;
    }

    public String getContactXML() {
        return contactXML;
    }
}

