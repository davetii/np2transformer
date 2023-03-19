package software.daveturner.np2transformer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


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
        doc.appendChild(e);
        return docToString(doc);
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
