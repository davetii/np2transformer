package software.daveturner.np2transformer;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class InvoiceReceiptNotificationRequest  extends NotificationRequest{

    DocumentBuilder builder = null;
    Document doc = null;

    public InvoiceReceiptNotificationRequest (
            String newLanguageCode,
            String newEmail,
            String newCustomerIdType,
            String newCustomerId,
            String xmlString
    ) {
        super( "INVOICERECEIPTNOTFN",
                newLanguageCode.toUpperCase() + "-" + "INVOICERECEIPTNOTFN" + "-EM",
                "INVOICERECEIPTNOTFN",
                newLanguageCode,
                "EMAIL",
                newEmail,
                newCustomerIdType,
                newCustomerId
        );
        mapXmlToNotificationRequest(xmlString);


    }
    public void mapXmlToNotificationRequest(String xmlString) {
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        InputSource is = new InputSource(new StringReader(xmlString));
        try {
            doc = builder.parse(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

        doc.getDocumentElement().normalize();
    }

    private void maybePutValues(String xmlNode, String newKey) {
        Node n = doc.getElementsByTagName(xmlNode).item(0);
        this.keyValues.put(newKey, n.getTextContent());
    }


}
