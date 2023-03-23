package software.daveturner.np2transformer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class InvoiceXMLMapper extends NotificationXMLMapper {

    private final String contactXML;
    private final String invoiceXML;
    private final String mrcXML;
    private final String nrcXML;
    private final String cancelXML;
    private final String adjustmentsXML;
    private final String paymentsXML;

    public InvoiceXMLMapper(String xml) {
        super(xml);
        this.contactXML = buildContactXML();
        this.invoiceXML = buildInvoiceXML();
        this.mrcXML = buildMrcXML();
        this.nrcXML = buildNrcXML();
        this.cancelXML = buildCancelXML();
        this.adjustmentsXML = buildAdjustmentsXML();
        this.paymentsXML = buildPaymentsXML();
    }

    private String buildAdjustmentsXML() {
        NodeList lists = sourceDoc.getElementsByTagName("AdjustmentList");
        if(lists == null) { return ""; }

        Document doc = newDoc();
        Element e = doc.createElement("Adjustments");

        NodeList list = lists.item(0).getChildNodes();
        for(int i=0; i< list.getLength(); i++) {
            Node n = list.item(i);
            if(n.getNodeType() !=  Node.ELEMENT_NODE) { continue; }
            if(isElementNamed(n, "AdjustmentDetail")) {
                Element adj = doc.createElement("Adjustment");
                NodeList children = n.getChildNodes();
                for(int x=0; x< children.getLength(); x++) {
                    Node child = children.item(x);
                    if(child.getNodeType() !=  Node.ELEMENT_NODE) { continue; }
                    maybeAddAttribute(adj, child, "AdjustmentText", "AdjustTxt");
                    maybeAddAttribute(adj, child, "AdjustmentNr", "AdjustNr");
                    maybeAddAttribute(adj, child, "TransactionDate");
                    adj.setTextContent(maybeAddCurrencyRaw(child));
                    e.appendChild(adj);
                }
            }
        }
        doc.appendChild(e);
        return docToString(doc);
    }

    private String buildPaymentsXML() {
        NodeList lists = sourceDoc.getElementsByTagName("PaymentList");
        if(lists == null) { return ""; }

        Document doc = newDoc();
        Element e = doc.createElement("Payments");

        NodeList list = lists.item(0).getChildNodes();
        for(int i=0; i< list.getLength(); i++) {
            Node n = list.item(i);
            if(n.getNodeType() !=  Node.ELEMENT_NODE) { continue; }
            if(isElementNamed(n, "PaymentDetail")) {
                Element payment = doc.createElement("Payment");
                NodeList children = n.getChildNodes();
                for(int x=0; x< children.getLength(); x++) {
                    Node child = children.item(x);
                    if(child.getNodeType() !=  Node.ELEMENT_NODE) { continue; }
                    maybeAddAttribute(payment, child, "TransactionDate");
                    maybeAddAttribute(payment, child, "TransactionType");
                    maybeAddAttribute(payment, child, "ReferenceNum");
                    maybeAddAttribute(payment, child, "BillingReceiptNr");
                    payment.setTextContent(maybeAddCurrencyRaw(child));
                    e.appendChild(payment);
                }
            }
        }

        doc.appendChild(e);
        return docToString(doc);
    }

    private String buildCancelXML() {
        return docToString(buildInvoiceOrder("Cancellation", "CancellationDetails"));
    }

    private String buildNrcXML() {
        return docToString(buildInvoiceOrder("NRC", "NRCDetails"));
    }

    private String buildMrcXML() {
        return docToString(buildInvoiceOrder("MRC", "MRCDetails"));
    }



    private Document buildInvoiceOrder( String newElementName, String sourceTagName) {
        NodeList results = sourceDoc.getElementsByTagName(sourceTagName);
        if(results == null || results.getLength() == 0) { return null; }

        Document doc = newDoc();
        Element e = doc.createElement(newElementName);
        Element order = doc.createElement("Order");

        for(int i=0; i< results.item(0).getChildNodes().getLength(); i++) {
            Node n = results.item(0).getChildNodes().item(i);
            if(n.getNodeType() !=  Node.ELEMENT_NODE) { continue; }
            if(isElementNamed(n, "VINDetails")) {
                NodeList vinDetailsChildren = n.getChildNodes();
                for (int x = 0; x < vinDetailsChildren.getLength(); x++) {
                    Node item = vinDetailsChildren.item(x);
                    if(item.getNodeType() !=  Node.ELEMENT_NODE) { continue; }
                    maybeAddAttribute(order, item, "VIN");
                    maybeAddVinTotalAttrib(order, item);
                    maybeAddDetails(doc, order, item, "Charges");
                    maybeAddDetails(doc, order, item, "Taxes");
                }
            }
        }
        e.appendChild(order);
        doc.appendChild(e);
        return doc;
    }

    private void maybeAddDetails( Document doc, Element order, Node n, String parentNodeName) {
        if (n ==  null || n.getChildNodes().getLength() == 0) { return; }
        if (isElementNamed(n, "OrderDetail")) {
            NodeList list = n.getChildNodes();
            for (int i=0; i < list.getLength(); i++) {
                Node n1 = list.item(i);
                if(n1.getNodeType() == Node.ELEMENT_NODE && n1.getNodeName().equals(parentNodeName)) {
                    NodeList list2 = n1.getChildNodes();
                    for (int x=0; x < list2.getLength(); x++) {
                        Node n3  = list2.item(x);
                        if(n3.getNodeType() == Node.ELEMENT_NODE && n3.getNodeName().equals("Detail")) {
                            if(parentNodeName.equals("Charges")) {
                                order.appendChild(newCharge(doc, n3.getChildNodes()));
                            } else if (parentNodeName.equals("Taxes")) {
                                order.appendChild(newTax(doc, n3.getChildNodes()));
                            }
                        }
                    }

                }

            }
        }

    }

    private Node newTax(Document doc, NodeList nodes) {
        if (nodes == null || nodes.getLength() == 0) { return null; }
        Element e = doc.createElement("Taxes");
        for(int i =0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if(n.getNodeType() !=  Node.ELEMENT_NODE) { continue; }
            maybeAddAttribute(e, n, "Label");
            maybeAddAmount(e, n );
        }
        return e;
    }

    private Element newCharge( Document doc, NodeList nodes) {
        if (nodes == null || nodes.getLength() == 0) { return null; }
        Element e = doc.createElement("Charge");
        for(int i =0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if(n.getNodeType() !=  Node.ELEMENT_NODE) { continue; }
            maybeAddAttribute(e, n, "TransactionDate");
            maybeAddAttribute(e, n, "InvoiceTxt");
            maybeAddAttribute(e, n, "FromDate");
            maybeAddAttribute(e, n, "ToDate");
            maybeAddAmount(e, n );
        }
        return e;

    }

    private void maybeAddAmount(Element e, Node n) {
        if (n.getNodeType() ==  Node.ELEMENT_NODE && n.getNodeName().equals("Amount")) {
            NodeList list = n.getChildNodes();
            for(int i =0; i < list.getLength(); i++ ) {
                Node child = list.item(i);
                if (child.getNodeType() ==  Node.ELEMENT_NODE && child.getNodeName().equals("RawAmount")) {
                    e.setTextContent(child.getTextContent());
                    break;
                }
            }
        }
    }

    private void maybeAddVinTotalAttrib(Element e, Node n) {
        if (isElementNamed(n, "VINTotal")) {
            NodeList list = n.getChildNodes();
            for (int i=0; i < list.getLength(); i++) {
                Node n2  = list.item(i);
                if(n2.getNodeType() == Node.ELEMENT_NODE && n2.getNodeName().equals("Amount")) {
                    NodeList list2 = n2.getChildNodes();
                    for (int x=0; x < list2.getLength(); x++) {
                        Node n3  = list2.item(x);
                        if(n3.getNodeType() == Node.ELEMENT_NODE && n3.getNodeName().equals("RawAmount")) {
                            e.setAttribute("VINTotal", n3.getTextContent());
                            break;
                        }
                    }
                }
            }
        }

    }

    private String buildInvoiceXML() {
        Document doc = newDoc();
        Element e = doc.createElement("Invoice");

        maybeAddAttribute(e,  "AccountNumber");
        maybeAddAttribute(e,  "IssueDate");
        maybeAddAttribute(e,  "ReferenceNumber");

        maybeAddAttributeAmount(e, "AccountAmountDue", "AmountDue");
        maybeAddAttributeAmount(e,  "NetForBaseOnsServices", "NetForBaseSvcs");
        maybeAddAttributeAmount(e,  "NetForSupplOnsServices", "NetForSupplSvcs");
        maybeAddAttributeAmount(e,  "PreviousBalance", "PreviousBalance");
        maybeAddAttributeAmount(e,  "TotalAdjCanVoids", "TotalAdj");
        maybeAddAttributeAmount(e, "TotalPayments", "TotalPayment");
        maybeAddAttributeAmount(e,  "TotalTaxes", "TotalTax");
        maybeAddAttributeAmount(e,  "MRCTotal", "TotalMRC");
        maybeAddAttributeAmount(e,  "NRCTotal", "TotalNRC");
        maybeAddCurrencyForInvoice(e);
        doc.appendChild(e);
        return docToString(doc);
    }

    private String maybeAddCurrencyRaw(Node node) {
        if (node == null || node.getChildNodes().getLength() == 0) { return ""; }
        NodeList list = node.getChildNodes();
        for(int i=0; i< list.getLength(); i++) {
            Node n = list.item(i);
            if(n.getNodeType() !=  Node.ELEMENT_NODE) { continue; }
            if(isElementNamed(n, "RawAmount")) {
               return n.getTextContent();
            }
        }
        return "";
    }

    private void maybeAddAttribute(Element e, String nodeName) {
        NodeList results = sourceDoc.getElementsByTagName(nodeName);
        if(results == null) { return; }
        for(int i=0; i< results.getLength(); i++) {
            Node n = results.item(i);
            if(n.getNodeType() !=  Node.ELEMENT_NODE) { continue; }
            if(isElementNamed(n, nodeName)) {
                e.setAttribute(nodeName, n.getTextContent());
                break;
            }
        }
    }

    private void maybeAddAttributeAmount(Element e, String sourceTagName, String newAtributeName) {
        NodeList results = sourceDoc.getElementsByTagName(sourceTagName);
        if(results == null) { return; }

        for(int i=0; i< results.getLength(); i++) {
            Node n = results.item(i);
            if(n.getNodeType() !=  Node.ELEMENT_NODE) { continue; }
            if(isElementNamed(n, sourceTagName)) {
                NodeList children = results.item(0).getChildNodes();
                for (int x = 0; x < children.getLength(); x++) {
                    Node item = children.item(x);
                    if(item.getNodeType() !=  Node.ELEMENT_NODE) { continue; }
                    if(isElementNamed(item, "RawAmount")) {
                        e.setAttribute(newAtributeName, item.getTextContent());
                        break;
                    }
                }
            }
        }
    }

    private void maybeAddCurrencyForInvoice(Element e) {
        NodeList results = sourceDoc.getElementsByTagName("NetForBaseOnsServices");
        if(results == null) { return; }

        for(int i=0; i< results.getLength(); i++) {
            Node n = results.item(i);
            if(n.getNodeType() !=  Node.ELEMENT_NODE) { continue; }
            if(isElementNamed(n, "NetForBaseOnsServices")) {
                NodeList children = results.item(0).getChildNodes();
                for (int x = 0; x < children.getLength(); x++) {
                    Node item = children.item(x);
                    if(item.getNodeType() !=  Node.ELEMENT_NODE) { continue; }
                    if(isElementNamed(item, "RawAmount")) {
                        e.setAttribute("Currency", item.getAttributes().item(0).getTextContent());
                        break;
                    }
                }
            }
        }
    }


    private String buildContactXML() {
        String givenName = sourceDoc.getElementsByTagName("GivenName").item(0).getTextContent();
        Document newDoc = newDoc();
        Element contact = newDoc.createElement("Contact");
        addAttribute(contact, "GivenName", givenName);
        newDoc.appendChild(contact);
        return docToString(newDoc);
    }

    public String getContactXML() {
        return contactXML;
    }

    public String getInvoiceXML() {
        return invoiceXML;
    }

    public String getMrcXML() {
        return mrcXML;
    }

    public String getNrcXML() {
        return nrcXML;
    }

    public String getCancelXML() {
        return cancelXML;
    }

    public String getAdjustmentsXML() {
        return adjustmentsXML;
    }

    public String getPaymentsXML() {
        return paymentsXML;
    }
}
