package software.daveturner.np2transformer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class InvoiceXMLMapper extends NotificationXMLMapper {

    private final String contactXML;

    public String getContactXML() {
        return contactXML;
    }

    public InvoiceXMLMapper(String xml) {
        super(xml);
        this.contactXML = buildContactXML();
    }

    private String buildContactXML() {
        String givenName = sourceDoc.getElementsByTagName("GivenName").item(0).getTextContent();
        Document newDoc = newDoc();
        Element contact = newDoc.createElement("Contact");
        addAttribute(contact, "GivenName", givenName);
        newDoc.appendChild(contact);
        return docToString(newDoc);
    }
}
