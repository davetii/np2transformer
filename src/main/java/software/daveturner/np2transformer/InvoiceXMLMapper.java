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
        Document doc = newDoc();
        Element e = doc.createElement("Adjustments");
        doc.appendChild(e);
        return docToString(doc);
    }

    private String buildPaymentsXML() {
        Document doc = newDoc();
        Element e = doc.createElement("Payments");
        doc.appendChild(e);
        return docToString(doc);
    }

    private String buildCancelXML() {
        Document doc = newDoc();
        Element e = doc.createElement("Cancellation");
        doc.appendChild(e);
        return docToString(doc);
    }

    private String buildNrcXML() {
        Document doc = newDoc();
        Element e = doc.createElement("NRC");
        doc.appendChild(e);
        return docToString(doc);
    }

    private String buildMrcXML() {
        Document doc = newDoc();
        Element e = doc.createElement("MRC");
        doc.appendChild(e);
        return docToString(doc);
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
        maybeAddCurrency(e);
        doc.appendChild(e);
        return docToString(doc);
    }

    private void maybeAddAttribute(Element e, String nodeName) {
        NodeList results = sourceDoc.getElementsByTagName(nodeName);
        if(results == null) { return; }
        for(int i=0; i< results.getLength(); i++) {
            Node n = results.item(i);
            if(isElementNamed(n, nodeName)) {
                e.setAttribute(nodeName, n.getTextContent());
            }
        }
    }

    private void maybeAddAttributeAmount(Element e, String sourceTagName, String newAtributeName) {
        NodeList results = sourceDoc.getElementsByTagName(sourceTagName);
        if(results == null) { return; }


        for(int i=0; i< results.getLength(); i++) {
            Node n = results.item(i);
            if(isElementNamed(n, sourceTagName)) {
                NodeList children = results.item(0).getChildNodes();
                for (int x = 0; x < children.getLength(); x++) {
                    Node item = children.item(x);
                    if(isElementNamed(item, "RawAmount")) {
                        e.setAttribute(newAtributeName, item.getTextContent());
                        break;
                    }
                }
            }
        }
    }

    private void maybeAddCurrency(Element e) {
        NodeList results = sourceDoc.getElementsByTagName("NetForBaseOnsServices");
        if(results == null) { return; }

        for(int i=0; i< results.getLength(); i++) {
            Node n = results.item(i);
            if(isElementNamed(n, "NetForBaseOnsServices")) {
                NodeList children = results.item(0).getChildNodes();
                for (int x = 0; x < children.getLength(); x++) {
                    Node item = children.item(x);
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
